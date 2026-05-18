package com.tiji.silcef;

import com.mojang.blaze3d.platform.cursor.CursorType;
import net.minecraft.client.Minecraft;
import org.cef.CefBrowserSettings;
import org.cef.CefClient;
import org.cef.browser.CefBrowserOsrWithHandler;
import org.cef.browser.CefRequestContext;

import java.awt.*;

public class SlicefBrowser extends CefBrowserOsrWithHandler {
    public volatile String currentTitle;

    private final RenderHandlerImpl renderHandler;
    private SlicefWarning warnings = new SlicefWarning();

    public SlicefBrowser(CefClient cefClient, String url, int width, int height, boolean loggingEnabled) {
        int scaleFactor = Minecraft.getInstance().getWindow().getGuiScale();

        CefBrowserSettings settings = new CefBrowserSettings();
        settings.windowless_frame_rate = 60;

        RenderHandlerImpl renderHandler = new RenderHandlerImpl(width * scaleFactor, height * scaleFactor, width, height);
        super(cefClient, url, CefRequestContext.getGlobalContext(), renderHandler, null, settings);
        this.renderHandler = renderHandler;

        super.createImmediately();

        if (loggingEnabled) {
            DisplayHandlerImpl.logBrowser(this);
        }
    }

    // Do note that warnings that are constant (like unsupported platform) will not be cleared.
    public void clearWarnings() {
        warnings = new SlicefWarning();
    }

    @Override
    public void close(boolean force) {
        DisplayHandlerImpl.unlogBrowser(this);
        super.close(force);
    }

    public SlicefWarning getWarnings() {
        return warnings;
    }

    public SoftwareTexture getTexture() {
        return renderHandler.getTexture();
    }

    public Rectangle getMinecraftBounds() {
        return renderHandler.getMinecraftBounds();
    }

    public void resize(int width, int height) {
        renderHandler.resize(width, height);
        wasResized(renderHandler.width, renderHandler.height);
    }

    public Rectangle getViewRect() {
        return renderHandler.getViewRect(this);
    }

    public CursorType getCurrentCursor() {
        return renderHandler.getCurrentCursor();
    }
}
