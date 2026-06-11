package com.tiji.silcef;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.tiji.silcef.internals.*;
import com.tiji.silcef.internals.win.D3D11;
import com.tiji.silcef.internals.win.D3D11Device;
import com.tiji.silcef.internals.win.DxTexture;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.SystemBootstrap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.lwjgl.glfw.GLFW.glfwExtensionSupported;
import static org.lwjgl.opengl.WGLNVDXInterop.*;

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

    private static ArrayList<Runnable> scheduledTasks = new ArrayList<>();

    public static long DXDevice;
    public static D3D11Device DXDeviceContainer;

    private static CefApp app;
    private static CefClient client;

    public static final Logger LOGGER = LoggerFactory.getLogger("slicef");

    @Override
    public void onInitialize() {
        ClientLifecycleEvents.CLIENT_STARTED.register((mc) ->
                new Thread(null, () -> this.start(mc), "Slicef CEF Message Worker").start());
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

        ClientLifecycleEvents.CLIENT_STOPPING.register((unused) -> {
            wglDXCloseDeviceNV(DXDevice);
            DxTexture.destroyAll();
            client.dispose();
            app.dispose();
        });
    }

    public void start(Minecraft mc) {
        String noAccelerationWarning = "This won't stop you from using this, but note that rendering might stutter.";
        boolean isWindows = System.getProperty("os.name").contains("Windows");
        if (!isWindows) {
            LOGGER.warn("Slicef doesn't support this platform for accelerated painting. {}", noAccelerationWarning);
            isAcceleratedPaintAllowed = false;
        }

        System.setProperty("java.awt.headless", "false"); // Why java...

        LOGGER.info("Loading natives from {}", NATIVE_PATH);
        SystemBootstrap.setLoader(s -> {
            Path libPath = Path.of(NATIVE_PATH, System.mapLibraryName(s));
            if (libPath.toFile().exists()) {
                System.load(libPath.toAbsolutePath().toString());
            } else {
                // We can assume that this library is JVM library because we don't have options anymore
                System.loadLibrary(s);
            }
        });

        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            // Last check on render thread
            if (isWindows && !glfwExtensionSupported("WGL_NV_DX_interop2")) {
                LOGGER.warn("WGL_NV_DX_interop2 extension is not supported on this system. " +
                        "If your GPU supports it, check if you have appropriate drivers installed. {}", noAccelerationWarning);
                isAcceleratedPaintAllowed = false;
            }

            // These needs to be called in render thread
            if (isWindows && isAcceleratedPaintAllowed) {
                try {
                    D3D11.initialize();
                    getDXDevice();
                    LOGGER.info("DirectX device is linked");
                } catch (Throwable t) {
                    LOGGER.error("Failed to link DirectX device", t);
                    isAcceleratedPaintAllowed = false;
                }
            }
            future.complete(null);
        });
        future.join();

        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = true;
        settings.browser_subprocess_path = Path.of(NATIVE_PATH, "/jcef_helper.exe").toAbsolutePath().toString();
        settings.resources_dir_path = NATIVE_PATH;
        settings.locales_dir_path = Path.of(NATIVE_PATH, "/locales").toString();
        settings.cache_path = Path.of("./slicef/browser_cache").toAbsolutePath().toString();
        settings.user_agent_product = "Slicef/beta";
        settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_VERBOSE;
        settings.log_file = Path.of("./slicef/cef_log.log").toAbsolutePath().toString();
        String locale = mc.options.languageCode;
        settings.locale = LocaleHelper.getCEFLanguageCode(locale);
        isFallbackLang = !LocaleHelper.isSupported(locale);

        ArrayList<String> args = new ArrayList<>();
        args.add("--no-sandbox");
        args.add("--force-high-performance-gpu");
        args.add("--disable-features=ThreadNaming");
        if (isAcceleratedPaintAllowed) {
            args.add("--shared-texture-enabled");
            LOGGER.info("Accelerated painting is enabled!");
        }
        String[] argsArray = args.toArray(new String[0]);

        if (!CefApp.startup(argsArray)) throw new RuntimeException("Failed to initialize CEF");

        app = CefApp.getInstance(argsArray, settings);
        client = app.createClient();
        client.addDisplayHandler(new DisplayHandlerImpl());

        isLoaded = true;
        scheduledTasks.forEach(Runnable::run);
        scheduledTasks = null;

        app.runMessageLoop();
    }

    public static SlicefBrowser getBrowser(String url) {
        if (!isLoaded) throw new IllegalStateException("Slicef is not loaded yet. Use scheduleStartup to run something immediately after Slicef is ready.");
        SlicefBrowser browser = new SlicefBrowser(client, url, true);
        browser.createImmediately();
        return browser;
    }

    public static void destroyBrowser(SlicefBrowser browser) {
        browser.setCloseAllowed();
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

        int hr = D3D11.get().D3D11CreateDevice(
                null,
                D3D11.D3D_DRIVER_TYPE_HARDWARE,
                null,
                2,
                null, 0,
                D3D11.D3D11_SDK_VERSION,
                ppDevice,
                null,
                ppContext
        );

        if (hr != 0) throw new RuntimeException("Failed to create DirectX device");

        // I should probably cast it to D3D11Device1 but every additional methods are pain to add

        DXDevice = wglDXOpenDeviceNV(Pointer.nativeValue(ppDevice.getValue()));
        DXDeviceContainer = new D3D11Device(ppDevice.getValue());
    }

    public static @NotNull String getUniqueName(String type) {
        return "slicef_%s_%s".formatted(type, UUID.randomUUID());
    }
}
