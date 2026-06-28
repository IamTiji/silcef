package com.tiji.silcef;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.tiji.silcef.internals.cefimpl.DisplayHandlerImpl;
import com.tiji.silcef.internals.cefimpl.RenderHandlerImpl;
import com.tiji.silcef.internals.SoftwareTexture;
import org.cef.CefBrowserSettings;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowser_N;
import org.cef.browser.CefPaintEvent;
import org.cef.browser.CefRequestContext;
import org.cef.callback.CefDragData;
import org.cef.handler.CefRenderHandler;
import org.cef.handler.CefScreenInfo;
import org.cef.misc.CefAcceleratedPaintInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SlicefBrowser extends CefBrowser_N implements CefRenderHandler {
    public volatile String currentTitle;
    public volatile TooltipStatus currentTooltip = TooltipStatus.ofInvisible();
    public volatile String statusText;
    public volatile double loadProgress;
    public volatile String faviconUrl;

    private final RenderHandlerImpl renderHandler;
    private SlicefWarning warnings = new SlicefWarning();

    public SlicefBrowser(CefClient cefClient, String url, boolean loggingEnabled) {
        super(cefClient, url, CefRequestContext.getGlobalContext(), null, null, getBrowserSettings());

        renderHandler = new RenderHandlerImpl();

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
        return null;
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

    public AbstractTexture getTexture() {
        AbstractTexture texture = renderHandler.getTexture();
        warnings.addConditionalWarning(SlicefWarning.Warning.WARN_SOFTWARE_FALLBACK, texture instanceof SoftwareTexture);
        return texture;
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

    public void setPermissionHandler(Consumer<PermissionRequest> permissionHandler) {
        this.permissionHandler = permissionHandler;
    }

    private Consumer<PermissionRequest> permissionHandler;
    public void onPermissionRequest(PermissionRequest request) {
        permissionHandler.accept(request);
    }


    // Just so that JCEF finds it
    @Override
    public Rectangle getViewRect(CefBrowser cefBrowser) {
        return renderHandler.getViewRect(cefBrowser);
    }

    @Override
    public boolean getScreenInfo(CefBrowser cefBrowser, CefScreenInfo cefScreenInfo) {
        return renderHandler.getScreenInfo(cefBrowser, cefScreenInfo);
    }

    @Override
    public Point getScreenPoint(CefBrowser cefBrowser, Point point) {
        return renderHandler.getScreenPoint(cefBrowser, point);
    }

    @Override
    public void onPopupShow(CefBrowser cefBrowser, boolean b) {
        renderHandler.onPopupShow(cefBrowser, b);
    }

    @Override
    public void onPopupSize(CefBrowser cefBrowser, Rectangle rectangle) {
        renderHandler.onPopupSize(cefBrowser, rectangle);
    }

    @Override
    public void onPaint(CefBrowser cefBrowser, boolean b, Rectangle[] rectangles, ByteBuffer byteBuffer, int i, int i1) {
        renderHandler.onPaint(cefBrowser, b, rectangles, byteBuffer, i, i1);
    }

    @Override
    public void addOnPaintListener(Consumer<CefPaintEvent> consumer) {
        renderHandler.addOnPaintListener(consumer);
    }

    @Override
    public void setOnPaintListener(Consumer<CefPaintEvent> consumer) {
        renderHandler.setOnPaintListener(consumer);
    }

    @Override
    public void removeOnPaintListener(Consumer<CefPaintEvent> consumer) {
        renderHandler.removeOnPaintListener(consumer);
    }

    @Override
    public boolean onCursorChange(CefBrowser cefBrowser, int i) {
        return renderHandler.onCursorChange(cefBrowser, i);
    }

    @Override
    public boolean startDragging(CefBrowser cefBrowser, CefDragData cefDragData, int i, int i1, int i2) {
        return renderHandler.startDragging(cefBrowser, cefDragData, i, i1, i2);
    }

    @Override
    public void updateDragCursor(CefBrowser cefBrowser, int i) {
        renderHandler.updateDragCursor(cefBrowser, i);
    }

    @Override
    public void onAcceleratedPaint(CefBrowser cefBrowser, boolean b, Rectangle[] rectangles, CefAcceleratedPaintInfo cefAcceleratedPaintInfo) {
        renderHandler.onAcceleratedPaint(cefBrowser, b, rectangles, cefAcceleratedPaintInfo);
    }
}
