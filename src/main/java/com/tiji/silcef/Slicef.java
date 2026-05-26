package com.tiji.silcef;

import com.jetbrains.cef.JCefAppConfig;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.tiji.silcef.internals.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.handler.CefAppHandlerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

import static org.lwjgl.opengl.WGLNVDXInterop.*;

public class Slicef implements ModInitializer {
    public static final boolean INDEV = true;

    public static boolean isFallbackLang = false;
    public static boolean isLoaded = false;
    public static boolean isAcceleratedPaintAllowed = true;

    private static ArrayList<Runnable> scheduledTasks = new ArrayList<>();

    public static long DXDevice;

    private static CefApp app;
    private static CefClient client;

    public static final Logger LOGGER = LoggerFactory.getLogger("slicef");

    @Override
    public void onInitialize() {
        ClientLifecycleEvents.CLIENT_STARTED.register((mc) -> mc.execute(() -> this.startup(mc)));
        CommandRegistrationCallback.EVENT.register((dispatcher, context, commandSelection) -> {
            dispatcher.register(
                    Commands.literal(
                            "opentest"
                    ).then(
                            Commands.argument("url", StringArgumentType.string())
                                    .executes((context_) -> {
                                        Minecraft.getInstance().execute(
                                                () -> Minecraft.getInstance().setScreen(
                                                        new TestBrowserScreen(context_.getArgument("url", String.class)))
                                        );
                                        return 0;
                                    })
                    ));
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register((_) -> {
            wglDXCloseDeviceNV(DXDevice);
            DxTexture.destroyAll();
            client.dispose();
            app.dispose();
        });
    }

    public void startup(Minecraft mc) {
        if (!System.getProperty("os.name").contains("Windows")) {
            LOGGER.warn("Slicef doesn't support this platform for accelerated painting. This won't stop you from using this, but note that rendering might stutter.");
            isAcceleratedPaintAllowed = false;
        }

        System.setProperty("java.awt.headless", "false");

        JCefAppConfig appConfig = JCefAppConfig.getInstance();
        CefSettings settings = appConfig.getCefSettings();
        settings.windowless_rendering_enabled = true;
        settings.cache_path = Path.of("./slicef/browser_cache").toAbsolutePath().toString();
        settings.user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36";
        settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_VERBOSE;
        settings.log_file = Path.of("./slicef/cef_log.txt").toAbsolutePath().toString();
        String locale = mc.options.languageCode;
        settings.locale = LocaleHelper.getCEFLanguageCode(locale);
        isFallbackLang = !LocaleHelper.isSupported(locale);

        ArrayList<String> args = new ArrayList<>(appConfig.getAppArgsAsList());
        args.add("--off-screen-rendering-enabled");
        if (isAcceleratedPaintAllowed) {
            D3D11.initialize();
            args.add("--accelerated-painting-enabled");
        }
        String[] argArray = args.toArray(new String[0]);

        CefApp.addAppHandler(new CefAppHandlerAdapter(new String[0]) {
            @Override
            public void onContextInitialized() {
                LOGGER.info("Cef is initialized; CEF version {}", app.getVersion().getCefVersion());
            }
        });
        CefApp.startup(argArray);

        app = CefApp.getInstance(argArray, settings, null);

        client = app.createClient();
        client.addDisplayHandler(new DisplayHandlerImpl());

        if (isAcceleratedPaintAllowed) {
            getDXDevice();
            LOGGER.info("DirectX device is linked");
        }

        isLoaded = true;
        scheduledTasks.forEach(Runnable::run);
        scheduledTasks = null;
    }

    private static Method loopworkMethod;
    public static void doLoopwork() {
        if (!isLoaded) return; // we wouldn't want to do loopwork on nothing
        if (loopworkMethod == null) {
            try {
                loopworkMethod = CefApp.class.getDeclaredMethod("N_DoMessageLoopWork");
                loopworkMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            loopworkMethod.invoke(app);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static SlicefBrowser getBrowser(String url, int width, int height) {
        if (!isLoaded) throw new IllegalStateException("Slicef is not loaded yet. Use scheduleStartup to run something immediately after Slicef is ready.");
        return new SlicefBrowser(client, url, width, height, true);
    }

    public static void destroyBrowser(SlicefBrowser browser) {
        browser.close(true);
        ((RenderHandlerImpl) browser.getRenderHandler()).destroy();
    }

    public static void scheduleStartup(Runnable runnable) {
        if (isLoaded) runnable.run();
        else {
            scheduledTasks.add(runnable);
        }
    }

    private static void getDXDevice() {
        PointerByReference ppDevice = new PointerByReference();
        PointerByReference ppContext = new PointerByReference();

        D3D11.get().D3D11CreateDevice(
                null,
                D3D11.D3D_DRIVER_TYPE_HARDWARE,
                null,
                0,
                null, 0,
                D3D11.D3D11_SDK_VERSION,
                ppDevice,
                null,
                ppContext
        );
        DXDevice = wglDXOpenDeviceNV(Pointer.nativeValue(ppDevice.getValue()));
    }

    public static @NotNull String getUniqueName(String type) {
        return "slicef_%s_%s".formatted(type, UUID.randomUUID());
    }
}
