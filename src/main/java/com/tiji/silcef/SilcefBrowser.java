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
import org.cef.callback.CefRunContextMenuCallback;
import org.cef.handler.CefRenderHandler;
import org.cef.handler.CefScreenInfo;
import org.cef.misc.CefAcceleratedPaintInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/// Browser class representing all browser made by Silcef mod.
///
/// @since 1.0
/// @author Tiji
public class SilcefBrowser extends CefBrowser_N implements CefRenderHandler {
    /// Title of current website
    public volatile String currentTitle;
    /// Tooltip of element that is hovered over by cursor
    public volatile TooltipStatus currentTooltip = TooltipStatus.ofInvisible();
    /// Status text of current website (Hovered link, load state, etc.)
    public volatile String statusText;
    /// How much the website is loaded normalized to 0-1. 1 means that website is fully loaded
    public volatile double loadProgress;
    /// Link to current website's favicon
    public volatile String faviconUrl;

    private final RenderHandlerImpl renderHandler;
    private SilcefWarning warnings = new SilcefWarning();

    /// Constructs browser instance. You should probably use [Silcef#getBrowser] instead.
    public SilcefBrowser(CefClient cefClient, String url, boolean loggingEnabled) {
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

    /// Clears warning that has been raised since last warning clear or browser construction.
    /// Do note that warnings that are constant (like unsupported platform) will not be cleared.
    ///
    /// @since 1.0
    /// @author Tiji
    public void clearWarnings() {
        warnings = new SilcefWarning();
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

    /// Returns instance of [SilcefWarning], containing warnings
    /// that has been raised. Warning instance will never be null.
    ///
    /// @since 1.0
    /// @author Tiji
    public SilcefWarning getWarnings() {
        return warnings;
    }

    /// Returns texture instance, with current render of website.
    /// This is never null, but texture instance may contain empty
    /// unpopulated texture.
    ///
    /// @since 1.0
    /// @author Tiji
    public AbstractTexture getTexture() {
        AbstractTexture texture = renderHandler.getTexture();
        warnings.addConditionalWarning(
                SilcefWarning.Warning.WARN_SOFTWARE_FALLBACK,

                (texture instanceof SoftwareTexture)
                        && Silcef.isAcceleratedPaintAllowed
                        && renderHandler.textureReady());
        return texture;
    }

    /// Returns bounds in Minecraft pixels; meaning that it scales
    /// with Minecraft's UI scale.
    ///
    /// @since 1.0
    /// @author Tiji
    public Rectangle getMinecraftBounds() {
        return renderHandler.getMinecraftBounds();
    }

    /// Resize the browser to new size. This is a heavy task; you
    /// should never call this frequently.
    ///
    /// @since 1.0
    /// @author Tiji
    public void resize(int width, int height) {
        renderHandler.resize(width, height);
        wasResized(renderHandler.width, renderHandler.height);
    }

    /// Returns bounds in raw pixels; meaning that it does not scale
    /// with Minecraft's UI scale.
    ///
    /// @since 1.0
    /// @author Tiji
    public Rectangle getViewRect() {
        return renderHandler.getViewRect(this);
    }

    /// Returns appropriate cursor to be used when user hovers over
    /// browser. This uses internal hack to use more cursors, so you
    /// cannot check if cursor is equal to pre-initialized instances
    /// in [CursorTypes] class of Blaze3D.
    ///
    /// @since 1.0
    /// @author Tiji
    public CursorType getCurrentCursor() {
        return renderHandler.getCurrentCursor();
    }

    /// Sets permission handler to be used when website requests for
    /// some permission. You may use pre-defined handlers from
    /// [PermissionHandlers] here.
    ///
    /// @since 1.0
    /// @author Tiji
    public void setPermissionHandler(Consumer<PermissionRequest> permissionHandler) {
        this.permissionHandler = permissionHandler;
    }

    private Consumer<PermissionRequest> permissionHandler;
    public void onPermissionRequest(PermissionRequest request) {
        if (permissionHandler != null)
            permissionHandler.accept(request);
    }

    /// Sets context menu handler to be used when context menu pops
    /// up on a website.
    ///
    /// @since 1.0
    /// @author Tiji
    public void setContextMenuHandler(ContextMenuHandler handler) {
        if (handler == null) throw new IllegalArgumentException("Handler may not be null");
        this.handler = handler;
    }

    private ContextMenuHandler handler = ContextMenuHandler.noopHandler();

    public void onContextMenu(ContextMenuItem[] contextMenu, CefRunContextMenuCallback callback) {
        handler.onContextMenu(contextMenu, callback);
    }

    public void onDismiss() {
        handler.onDismiss();
    }


    @Override
    public boolean isAcceleratedPaintEnabled() {
        return Silcef.isAcceleratedPaintAllowed;
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
    public void onAcceleratedPaint(CefBrowser cefBrowser, boolean b, Rectangle[] rectangles,
                                   CefAcceleratedPaintInfo cefAcceleratedPaintInfo, int width, int height) {
        renderHandler.onAcceleratedPaint(cefBrowser, b, rectangles, cefAcceleratedPaintInfo, width, height);
    }
}
