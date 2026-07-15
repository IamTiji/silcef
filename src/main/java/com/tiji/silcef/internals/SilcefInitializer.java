package com.tiji.silcef.internals;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.tiji.silcef.Silcef;
import com.tiji.silcef.internals.cefimpl.ContextMenuHandlerImpl;
import com.tiji.silcef.internals.cefimpl.DisplayHandlerImpl;
import com.tiji.silcef.internals.cefimpl.PermissionHandlerImpl;
import com.tiji.silcef.internals.utils.LocaleHelper;
import com.tiji.silcef.internals.utils.PermissionSentenceUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.SystemBootstrap;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class SilcefInitializer implements ModInitializer {
    private static CefApp app;
    private static CefClient client;

    @Override
    public void onInitialize() {
        ClientLifecycleEvents.CLIENT_STARTED.register((mc) ->
                new Thread(null, () -> this.start(mc), "Silcef CEF Message Worker").start());
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
            client.dispose();
            app.dispose();
        });
    }

    public void start(Minecraft mc) {
        Silcef.LOGGER.info("Loading natives from {}", JcefLoader.NATIVE_PATH);
        SystemBootstrap.setLoader(s -> {
            Path libPath = Path.of(JcefLoader.NATIVE_PATH, System.mapLibraryName(s));
            if (libPath.toFile().exists()) {
                System.load(libPath.toAbsolutePath().toString());
            } else {
                // We can assume that this library is JVM library because we don't have options anymore
                System.loadLibrary(s);
            }
        });

        String noAccelerationWarning = "This won't stop you from using this, but note that rendering might stutter.";
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                Silcef.isAcceleratedPaintAllowed = AcceleratedPaintHandler.initialize();
            } catch (Throwable e) { Silcef.LOGGER.error(e.getMessage()); }

            // These needs to be called in render thread
            if (!Silcef.isAcceleratedPaintAllowed) {
                Silcef.LOGGER.warn("Failed to initialize accelerated painting. {}", noAccelerationWarning);
            }
            future.complete(null);
        });
        future.join();

        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = true;
        settings.browser_subprocess_path = Path.of(JcefLoader.NATIVE_PATH, "/jcef_helper.exe").toAbsolutePath().toString();
        settings.resources_dir_path = JcefLoader.NATIVE_PATH;
        settings.locales_dir_path = Path.of(JcefLoader.NATIVE_PATH, "/locales").toString();
        settings.cache_path = Path.of("./silcef/browser_cache").toAbsolutePath().toString();
        //settings.user_agent_product = "Silcef/beta";
        settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_VERBOSE;
        settings.log_file = Path.of("./silcef/cef_log.log").toAbsolutePath().toString();
        String locale = mc.options.languageCode;
        PermissionSentenceUtils.load(locale);
        settings.locale = LocaleHelper.getCEFLanguageCode(locale);
        Silcef.isFallbackLang = !LocaleHelper.isSupported(locale);

        ArrayList<String> args = new ArrayList<>();
        args.add("--no-sandbox");
        args.add("--force-high-performance-gpu");
        args.add("--disable-features=ThreadNaming");
        if (Silcef.isAcceleratedPaintAllowed) {
            args.add("--shared-texture-enabled");
            Silcef.LOGGER.info("Accelerated painting is enabled!");
        }
        String[] argsArray = args.toArray(new String[0]);

        if (!CefApp.startup(argsArray)) throw new RuntimeException("Failed to initialize CEF");

        app = CefApp.getInstance(argsArray, settings);
        client = app.createClient();
        client.addDisplayHandler(new DisplayHandlerImpl());
        client.addPermissionHandler(new PermissionHandlerImpl());
        client.addContextMenuHandler(new ContextMenuHandlerImpl());

        Silcef.isLoaded = true;
        Silcef.executeScheduledTasks();

        app.runMessageLoop();
    }

    public static CefClient getClient() {
        return client;
    }
}
