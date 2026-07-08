package com.tiji.silcef;

import com.tiji.silcef.internals.*;
import com.tiji.silcef.internals.cefimpl.RenderHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Slicef {
    /// Whether if CEF is running in different language than user's Minecraft locale
    public static volatile boolean isFallbackLang = false;
    /// Whether if Silcef has finished its loading process
    public static volatile boolean isLoaded = false;
    /// Whether if Silcef is running with accelerated paint enabled
    public static boolean isAcceleratedPaintAllowed = false;

    private static List<Runnable> scheduledTasks = Collections.synchronizedList(new ArrayList<>());

    /// Whether if this mod is release or not
    public static final boolean INDEV = true;

    public static final Logger LOGGER = LoggerFactory.getLogger("slicef");

    /// Creates a browser instance with url loaded
    ///
    /// @param url initial website to start on
    /// @return newly created browser instance
    /// @since 1.0
    /// @author Tiji
    public static SlicefBrowser getBrowser(String url) {
        return getBrowser(url, false);
    }

    /// Creates a browser instance with url loaded
    ///
    /// @param url initial website to start on
    /// @param loggingEnabled whether if website's logs should be forwarded to Minecraft logs
    /// @return newly created browser instance
    /// @author Tiji
    /// @since 1.0
    public static SlicefBrowser getBrowser(String url, boolean loggingEnabled) {
        if (!isLoaded)
            throw new IllegalStateException("Slicef is not loaded yet. Use scheduleStartup to run something immediately after Slicef is ready.");
        SlicefBrowser browser = new SlicefBrowser(SlicefInitializer.getClient(), url, loggingEnabled);
        browser.createImmediately();
        return browser;
    }

    /// Destroys the browser instance and cleans up resource related to it
    ///
    /// @since 1.0
    /// @author Tiji
    public static void destroyBrowser(SlicefBrowser browser) {
        browser.setCloseAllowed();
        browser.close(true);
        ((RenderHandlerImpl) browser.getRenderHandler()).destroy();
    }

    /// Schedule a task to be run after Silcef is loaded. The task should
    /// not hang. Note that this task will not be run in render thread.
    ///
    /// If this is called after Silcef initialized, the task will be run
    /// immediately, on the thread calling this method, and block until
    /// the task is completed.
    ///
    /// @since 1.0
    /// @author Tiji
    public static void scheduleStartup(Runnable runnable) {
        if (isLoaded) runnable.run();
        else {
            scheduledTasks.add(runnable);
        }
    }

    /// Not to be used by other mods. This runs tasks scheduled in
    /// [scheduleStartup] method.
    public static void executeScheduledTasks() {
        if (Thread.currentThread().getName().contains("Slicef")) {
            scheduledTasks.forEach(Runnable::run);
            scheduledTasks = null;
        } else {
            throw new IllegalCallerException("This may only be called in Slicef Message Thread");
        }
    }
}
