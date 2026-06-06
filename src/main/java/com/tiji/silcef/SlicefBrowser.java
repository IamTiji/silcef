package com.tiji.silcef;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.tiji.silcef.internals.DisplayHandlerImpl;
import com.tiji.silcef.internals.RenderHandlerImpl;
import com.tiji.silcef.internals.SoftwareTexture;
import net.minecraft.client.Minecraft;
import org.cef.CefBrowserSettings;
import org.cef.CefClient;
import org.cef.browser.CefBrowser_N;
import org.cef.browser.CefRequestContext;
import org.cef.handler.CefRenderHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

public class SlicefBrowser extends CefBrowser_N {
    public volatile String currentTitle;
    public volatile TooltipStatus currentTooltip = TooltipStatus.ofInvisible();

    private final RenderHandlerImpl renderHandler;
    private SlicefWarning warnings = new SlicefWarning();

    public SlicefBrowser(CefClient cefClient, String url, int width, int height, boolean loggingEnabled) {
        super(cefClient, url, CefRequestContext.getGlobalContext(), null, null, getBrowserSettings());

        int scaleFactor = Minecraft.getInstance().getWindow().getGuiScale();
        renderHandler = new RenderHandlerImpl(width * scaleFactor, height * scaleFactor, width, height);

        if (loggingEnabled) {
            DisplayHandlerImpl.logBrowser(this);
        }
    }

    private static CefBrowserSettings getBrowserSettings() {
        CefBrowserSettings settings = new CefBrowserSettings();
        settings.windowless_frame_rate = 60;
        return settings;
    }

    // Do note that warnings that are constant (like unsupported platform) will not be cleared.
    public void clearWarnings() {
        warnings = new SlicefWarning();
    }

    @Override
    public CefRenderHandler getRenderHandler() {
        return renderHandler;
    }

    @Override
    protected CefBrowser_N createDevToolsBrowser(CefClient cefClient, String s, CefRequestContext cefRequestContext, CefBrowser_N cefBrowserN, Point point) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void createImmediately() {
        if (getNativeRef("CefBrowser") == 0) {
            createBrowser(getClient(), 0L, getUrl(), true, false, null,
                    getRequestContext());
        }
    }

    @Override
    public Component getUIComponent() {
        throw new UnsupportedOperationException("Why would you do this...");
    }

    @Override
    public void close(boolean force) {
        super.close(force);
    }

    @Override
    public synchronized void onBeforeClose() {
        DisplayHandlerImpl.unlogBrowser(this);
        super.onBeforeClose();
    }

    @Override
    public CompletableFuture<BufferedImage> createScreenshot(boolean b) {
        throw new UnsupportedOperationException("not implemented");
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
