package com.cinemamod.mcef;

import com.tiji.silcef.Silcef;
import com.tiji.silcef.SilcefBrowser;
import com.tiji.silcef.internals.SilcefInitializer;
import org.cef.CefClient;

public class MCEF {
    private static MCEFClient client;
    private static CefClient rawClient;

    public static void initialize() {
        rawClient = SilcefInitializer.getApp().createClient();
        client = new MCEFClient(rawClient);
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
