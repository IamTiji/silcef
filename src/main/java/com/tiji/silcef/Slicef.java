package com.tiji.silcef;

import com.tiji.silcef.internals.*;
import com.tiji.silcef.internals.cefimpl.RenderHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Slicef {
    public static volatile boolean isFallbackLang = false;
    public static volatile boolean isLoaded = false;
    public static boolean isAcceleratedPaintAllowed = false;

    private static List<Runnable> scheduledTasks = Collections.synchronizedList(new ArrayList<>());

    public static final boolean INDEV = true;

    public static final Logger LOGGER = LoggerFactory.getLogger("slicef");

    public static SlicefBrowser getBrowser(String url) {
        if (!isLoaded) throw new IllegalStateException("Slicef is not loaded yet. Use scheduleStartup to run something immediately after Slicef is ready.");
        SlicefBrowser browser = new SlicefBrowser(SlicefInitializer.getClient(), url, true);
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

    public static void executeScheduledTasks() {
        if (Thread.currentThread().getName().contains("Slicef")) {
            scheduledTasks.forEach(Runnable::run);
            scheduledTasks = null;
        } else {
            throw new IllegalCallerException("This may only be called in Slicef Message Thread");
        }
    }
}
