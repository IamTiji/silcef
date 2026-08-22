package com.cinemamod.mcef;

import com.tiji.silcef.Silcef;
import com.tiji.silcef.SilcefBrowser;
import com.tiji.silcef.internals.SilcefInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.cef.CefClient;

import java.util.ArrayList;
import java.util.List;

public class MCEF {
    private static MCEFClient client;
    private static CefClient rawClient;

    public static List<SilcefBrowser> browsers = new ArrayList<>(10);

    public static void initialize() {
        rawClient = SilcefInitializer.getApp().createClient();
        client = new MCEFClient(rawClient);

        ClientLifecycleEvents.CLIENT_STOPPING.register((mc) -> {
            browsers.forEach(Silcef::destroyBrowser);

            rawClient.dispose();
        });
    }

    public static MCEFBrowser createBrowser(String url, boolean transparent) {
        SilcefBrowser parent = new SilcefBrowser(rawClient, url, false);
        parent.createImmediately();

        return new MCEFBrowser(parent);
    }

    public static MCEFBrowser createBrowser(String url, boolean transparent, int width, int height) {
        MCEFBrowser browser = createBrowser(url, transparent);

        browser.resize(width, height);

        return browser;
    }

    public static boolean isInitialized() {
        return Silcef.isLoaded;
    }

    public static MCEFClient getClient() {
        return client;
    }
}
