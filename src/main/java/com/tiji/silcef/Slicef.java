package com.tiji.silcef;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

import static org.lwjgl.opengl.WGLNVDXInterop.wglDXOpenDeviceNV;

public class Slicef implements ModInitializer {
    public static final String NATIVE_PATH =
            Path.of("./../jcef") // Hardcoded, but will be replaced with actual downloader
                    .toAbsolutePath()
                    .normalize()
                    .toString();
    public static final boolean INDEV = true;

    public static boolean isFallbackLang = false;
    public static boolean isLoaded = false;
    public static boolean isAcceleratedPaintAllowed = true;

    public static long DXDevice;

    private static CefApp app;
    private static CefClient client;

    public static final Logger LOGGER = LoggerFactory.getLogger("slicef");

    @Override
    public void onInitialize() {
        ClientLifecycleEvents.CLIENT_STARTED.register((mc) -> mc.execute(() -> this.startup(mc)));
    }

    public void startup(Minecraft mc) {
        if (!System.getProperty("os.name").contains("Windows")) {
            LOGGER.warn("Slicef doesn't support this platform for accelerated painting. This won't stop you from using this, but note that rendering might stutter.");
            isAcceleratedPaintAllowed = false;
        }

        LOGGER.info("Loading natives from {}", NATIVE_PATH);
        System.setProperty("jcef.path", NATIVE_PATH);

        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = true;
        settings.browser_subprocess_path = Path.of(NATIVE_PATH, "/jcef_helper.exe").toString();
        settings.resources_dir_path = NATIVE_PATH;
        settings.locales_dir_path = Path.of(NATIVE_PATH, "/locales").toString();
        settings.cache_path = Path.of("./slicef/browser_cache").toAbsolutePath().toString();
        settings.user_agent_product = "Slicef/beta";
        settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_WARNING;
        settings.log_file = Path.of("./slicef/cef_log.txt").toAbsolutePath().toString();
        String locale = mc.options.languageCode;
        settings.locale = LocaleHelper.getCEFLanguageCode(locale);
        isFallbackLang = !LocaleHelper.isSupported(locale);

        if (!CefApp.startup(new String[]{})) throw new RuntimeException("Failed to initialize CEF");

        ArrayList<String> args = new ArrayList<>();
        args.add("--off-screen-rendering-enabled");
        if (isAcceleratedPaintAllowed) {
            D3D11.initialize();
            args.add("--accelerated-painting-enabled");
        }
        app = CefApp.getInstance(args.toArray(new String[0]), settings);
        client = app.createClient();
        client.addDisplayHandler(new LogHandler());

        LOGGER.info("Cef is initialized; CEF version {}", app.getVersion().getCefVersion());

        if (isAcceleratedPaintAllowed) {
            getDXDevice();
            LOGGER.info("DirectX device is linked");
        }

        CommandRegistrationCallback.EVENT.register((dispatcher, context, commandSelection) -> {
            dispatcher.register(
                    Commands.literal(
                            "opentest"
                    ).then(
                            Commands.argument("url", StringArgumentType.string())
                                    .executes((context_) -> {
                                        Minecraft.getInstance().execute(
                                                () -> Minecraft.getInstance().setScreen(
                                                        new BrowserScreen(context_.getArgument("url", String.class)))
                                                );
                                        return 0;
                    })
            ));
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register((unused) -> {
            DxTexture.destroyAll();
            client.dispose();
            app.dispose();
        });

        isLoaded = true;
    }

    public static void doLoopwork() {
        app.N_DoMessageLoopWork();
    }

    public static SlicefBrowser getBrowser(String url, int width, int height) {
        if (!isLoaded) throw new IllegalStateException("Slicef is not loaded yet. Wait until Minecraft is fully loaded");
        return new SlicefBrowser(client, url, width, height, true);
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
