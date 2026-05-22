package com.tiji.silcef.internals;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.tiji.silcef.Slicef;
import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefDragData;
import org.cef.handler.CefAcceleratedPaintInfo;
import org.cef.handler.CefRenderHandler;
import org.cef.handler.CefScreenInfo;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class RenderHandlerImpl implements CefRenderHandler {
    public Rectangle popupBounds, mcPopupBounds;
    public int width, height;
    public int mcWidth, mcHeight;
    private SoftwareTexture texture;
    private boolean popupVisible;

    public RenderHandlerImpl(int width, int height, int mcWidth, int mcHeight) {
        this.width = width;
        this.height = height;
        this.mcWidth = mcWidth;
        this.mcHeight = mcHeight;
        texture = new SoftwareTexture(width, height);
    }

    @Override
    public void onPaint(CefBrowser cefBrowser, boolean popup, Rectangle[] dirtyRects, ByteBuffer pixels, int w, int h) {
        if (dirtyRects.length == 0) return;
        ByteBuffer safeBuffer = BufferUtils.createByteBuffer(pixels.remaining());
        safeBuffer.put(pixels);
        Minecraft.getInstance().execute(() -> texture.onPaint(dirtyRects, safeBuffer, w, h));
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
    public double getDeviceScaleFactor(CefBrowser cefBrowser) {
        return 0;
    }

    @Override
    public void onAcceleratedPaint(CefBrowser cefBrowser, boolean b, Rectangle[] rectangles, CefAcceleratedPaintInfo info) {
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

    @Override
    public boolean onCursorChange(CefBrowser browser, int cursorType) {
        currentCursor = convertCursor(cursorType);
        return true;
    }

    private static final Map<Integer, CursorType> cursorMap = Map.ofEntries(
            Map.entry(Cursor.DEFAULT_CURSOR,    CursorType.createStandardCursor(GLFW_ARROW_CURSOR,         "slicef_DEFAULT_CURSOR",     CursorType.DEFAULT)),
            Map.entry(Cursor.CROSSHAIR_CURSOR,  CursorType.createStandardCursor(GLFW_CROSSHAIR_CURSOR,     "slicef_CROSSHAIR_CURSOR",   CursorType.DEFAULT)),
            Map.entry(Cursor.HAND_CURSOR,       CursorType.createStandardCursor(GLFW_POINTING_HAND_CURSOR, "slicef_HAND_CURSOR",        CursorType.DEFAULT)),
            Map.entry(Cursor.MOVE_CURSOR,       CursorType.createStandardCursor(GLFW_RESIZE_ALL_CURSOR,    "slicef_MOVE_CURSOR",        CursorType.DEFAULT)),
            Map.entry(Cursor.TEXT_CURSOR,       CursorType.createStandardCursor(GLFW_IBEAM_CURSOR,         "slicef_TEXT_CURSOR",        CursorType.DEFAULT)),
            //Map.entry(Cursor.WAIT_CURSOR,        CursorType.createStandardCursor(,         "slicef_WAIT_CURSOR",         CursorType.DEFAULT)),
            Map.entry(Cursor.N_RESIZE_CURSOR,   CursorType.createStandardCursor(GLFW_VRESIZE_CURSOR,       "slicef_N_RESIZE_CURSOR",    CursorType.DEFAULT)),
            Map.entry(Cursor.S_RESIZE_CURSOR,   CursorType.createStandardCursor(GLFW_VRESIZE_CURSOR,       "slicef_S_RESIZE_CURSOR",    CursorType.DEFAULT)),
            Map.entry(Cursor.E_RESIZE_CURSOR,   CursorType.createStandardCursor(GLFW_HRESIZE_CURSOR,       "slicef_E_RESIZE_CURSOR",    CursorType.DEFAULT)),
            Map.entry(Cursor.W_RESIZE_CURSOR,   CursorType.createStandardCursor(GLFW_HRESIZE_CURSOR,       "slicef_W_RESIZE_CURSOR",    CursorType.DEFAULT)),

            Map.entry(Cursor.NW_RESIZE_CURSOR,  CursorType.createStandardCursor(GLFW_RESIZE_NWSE_CURSOR,   "slicef_NW_RESIZE_CURSOR",   CursorType.DEFAULT)),
            Map.entry(Cursor.NE_RESIZE_CURSOR,  CursorType.createStandardCursor(GLFW_RESIZE_NWSE_CURSOR,   "slicef_NE_RESIZE_CURSOR",   CursorType.DEFAULT)),
            Map.entry(Cursor.SW_RESIZE_CURSOR,  CursorType.createStandardCursor(GLFW_RESIZE_NWSE_CURSOR,   "slicef_SW_RESIZE_CURSOR",   CursorType.DEFAULT)),
            Map.entry(Cursor.SE_RESIZE_CURSOR,  CursorType.createStandardCursor(GLFW_RESIZE_NWSE_CURSOR,   "slicef_SE_RESIZE_CURSOR",   CursorType.DEFAULT))
    );
    private static CursorType convertCursor(int cursorType) {
        //if (cursorType == 37) return null; // CT_NONE, no cursor.

        if (cursorMap.containsKey(cursorType)) {
            return cursorMap.get(cursorType);
        }

        Slicef.LOGGER.warn("Unsupported cursor type: {}", cursorType);

        return CursorType.DEFAULT;
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

        texture.destroy();
        texture = new SoftwareTexture(this.width, this.height);
    }

    public Rectangle getMinecraftBounds() {
        return new Rectangle(0, 0, mcWidth, mcHeight);
    }

    public SoftwareTexture getTexture() {
        return texture;
    }
}
