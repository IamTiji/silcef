package com.tiji.silcef.internals;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.tiji.silcef.AbstractTexture;
import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefPaintEvent;
import org.cef.callback.CefDragData;
import org.cef.misc.CefAcceleratedPaintInfo;
import org.cef.handler.CefRenderHandler;
import org.cef.handler.CefScreenInfo;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class RenderHandlerImpl implements CefRenderHandler {
    public Rectangle popupBounds, mcPopupBounds;
    public int width, height;
    public int mcWidth, mcHeight;

    private boolean wasPreviousPaintAccelerated = true;

    private SoftwareTexture softwareTexture;
    private final AcceleratedPaintHandler acceleratedPaintHandler = AcceleratedPaintHandler.getInstance();

    private boolean popupVisible;

    public void destroy() {
        softwareTexture.destroy();
        acceleratedPaintHandler.destroy();
    }

    @Override
    public void onPaint(CefBrowser cefBrowser, boolean popup, Rectangle[] dirtyRects, ByteBuffer pixels, int w, int h) {
        wasPreviousPaintAccelerated = false;

        if (softwareTexture == null) return;
        if (dirtyRects.length == 0) return;

        ByteBuffer safeBuffer = BufferUtils.createByteBuffer(pixels.remaining());
        safeBuffer.put(pixels);
        Minecraft.getInstance().execute(() -> softwareTexture.onPaint(dirtyRects, safeBuffer, w, h));
    }

    @Override
    public boolean startDragging(CefBrowser browser, CefDragData dragData, int mask, int x, int y) {
        return false; // true doesn't really work
    }

    @Override
    public void updateDragCursor(CefBrowser cefBrowser, int i) {

    }

    @Override
    public void onPopupSize(CefBrowser cefBrowser, Rectangle rectangle) {
        if (!popupVisible) return;
        popupBounds = rectangle;

        int scaleFactor = Minecraft.getInstance().getWindow().getGuiScale();
        int x = popupBounds.x * scaleFactor;
        int y = popupBounds.y * scaleFactor;
        int width = popupBounds.width * scaleFactor;
        int height = popupBounds.height * scaleFactor;
        mcPopupBounds = new Rectangle(x, y, width, height);
    }

    @Override
    public void onPopupShow(CefBrowser cefBrowser, boolean show) {
        popupVisible = show;
    }

    @Override
    public Point getScreenPoint(CefBrowser cefBrowser, Point point) {
        return new Point(0, 0);
    }

    @Override
    public void onAcceleratedPaint(CefBrowser cefBrowser, boolean b, Rectangle[] rectangles, CefAcceleratedPaintInfo info) {
        wasPreviousPaintAccelerated = true;

        acceleratedPaintHandler.onPaint(info);
    }

    @Override
    public Rectangle getViewRect(CefBrowser cefBrowser) {
        return new Rectangle(0, 0, width, height);
    }

    @Override
    public boolean getScreenInfo(CefBrowser cefBrowser, CefScreenInfo cefScreenInfo) {
        return false;
    }

    private CursorType currentCursor = CursorType.DEFAULT;

    protected static final CursorConverter cursorConverter = CursorConverter.getInstance();
    @Override
    public boolean onCursorChange(CefBrowser browser, int cursorType) {
        currentCursor = cursorConverter.convert(cursorType);
        return true;
    }

    public CursorType getCurrentCursor() {
        return currentCursor;
    }

    public void resize(int width, int height) {
        int scaleFactor = Minecraft.getInstance().getWindow().getGuiScale();
        this.width = width * scaleFactor;
        this.height = height * scaleFactor;

        this.mcWidth = width;
        this.mcHeight = height;

        if (softwareTexture != null) softwareTexture.destroy();
        softwareTexture = new SoftwareTexture(this.width, this.height);

        acceleratedPaintHandler.onResize(this.width, this.height);
    }

    public Rectangle getMinecraftBounds() {
        return new Rectangle(0, 0, mcWidth, mcHeight);
    }

    public AbstractTexture getTexture() {
        return wasPreviousPaintAccelerated ? acceleratedPaintHandler.getTexture() : softwareTexture;
    }

    // Does nothing
    @Override public void addOnPaintListener(Consumer<CefPaintEvent> consumer) {}

    @Override public void setOnPaintListener(Consumer<CefPaintEvent> consumer) {}

    @Override public void removeOnPaintListener(Consumer<CefPaintEvent> consumer) {}
}
