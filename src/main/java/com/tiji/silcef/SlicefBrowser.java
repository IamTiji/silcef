package com.tiji.silcef;

import com.mojang.blaze3d.platform.cursor.CursorType;
import net.minecraft.client.Minecraft;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserOsr;
import org.cef.browser.CefRequestContext;
import org.cef.callback.CefDragData;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class SlicefBrowser extends CefBrowserOsr {
    public Rectangle popupBounds, mcPopupBounds;
    private int width, height;
    private final int mcWidth, mcHeight;
    private SoftwareTexture texture;
    private boolean popupVisible;
    private CefDragData dragData;
    private int buttonMask;

    private SlicefWarning warnings = new SlicefWarning();

    public SlicefBrowser(CefClient cefClient, String url, int width, int height) {
        super(cefClient, url, true, CefRequestContext.getGlobalContext());
        int scaleFactor = Minecraft.getInstance().getWindow().getGuiScale();
        super.createImmediately();
        this.mcWidth = width;
        this.mcHeight = height;
        this.width = width * scaleFactor;
        this.height = height * scaleFactor;
        texture = new SoftwareTexture(this.width, this.height);
    }

    public SlicefWarning getWarnings() {
        return warnings;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        texture = new SoftwareTexture(this.width, this.height);
        wasResized(width, height);
    }

    @Override
    public void onPaint(CefBrowser cefBrowser, boolean popup, Rectangle[] dirtyRects, ByteBuffer pixels, int w, int h) {
        super.onPaint(cefBrowser, popup, dirtyRects, pixels, w, h);
        if (dirtyRects.length == 0) return;
        texture.onPaint(dirtyRects, pixels);
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
        return super.getScreenPoint(cefBrowser, point);
    }

    @Override
    public void onAcceleratedPaint(CefBrowser cefBrowser, boolean b, Rectangle[] rectangles, long l) {
        super.onAcceleratedPaint(cefBrowser, b, rectangles, l);
    }

    @Override
    public Rectangle getViewRect(CefBrowser cefBrowser) {
        return new Rectangle(0, 0, width, height);
    }

    public Rectangle getMinecraftBounds() {
        return new Rectangle(0, 0, mcWidth, mcHeight);
    }

    public SoftwareTexture getTexture() {
        return texture;
    }

    private CursorType currentCursor = CursorType.DEFAULT;

    @Override
    public boolean onCursorChange(CefBrowser browser, int cursorType) {
        currentCursor = convertCursor(cursorType);
        return super.onCursorChange(browser, cursorType);
    }

    private static final Map<Integer, CursorType> cursorMap = Map.ofEntries(
            Map.entry(0,  CursorType.createStandardCursor(GLFW_ARROW_CURSOR,         "slicef_CT_POINTER",          CursorType.DEFAULT)),
            Map.entry(1,  CursorType.createStandardCursor(GLFW_CROSSHAIR_CURSOR,     "slicef_CT_CROSS",            CursorType.DEFAULT)),
            Map.entry(2,  CursorType.createStandardCursor(GLFW_POINTING_HAND_CURSOR, "slicef_CT_HAND",             CursorType.DEFAULT)),
            Map.entry(3,  CursorType.createStandardCursor(GLFW_IBEAM_CURSOR,         "slicef_CT_IBEAM",            CursorType.DEFAULT)),
            Map.entry(4,  CursorType.createStandardCursor(GLFW_ARROW_CURSOR,         "slicef_CT_WAIT",             CursorType.DEFAULT)),
            Map.entry(5,  CursorType.createStandardCursor(GLFW_ARROW_CURSOR,         "slicef_CT_HELP",             CursorType.DEFAULT)),

            // Directional Resizing
            Map.entry(6,  CursorType.createStandardCursor(GLFW_RESIZE_EW_CURSOR,     "slicef_CT_EASTRESIZE",       CursorType.DEFAULT)),
            Map.entry(7,  CursorType.createStandardCursor(GLFW_RESIZE_NS_CURSOR,     "slicef_CT_NORTHRESIZE",      CursorType.DEFAULT)),
            Map.entry(8,  CursorType.createStandardCursor(GLFW_RESIZE_NESW_CURSOR,   "slicef_CT_NORTHEASTRESIZE",  CursorType.DEFAULT)),
            Map.entry(9,  CursorType.createStandardCursor(GLFW_RESIZE_NWSE_CURSOR,   "slicef_CT_NORTHWESTRESIZE",  CursorType.DEFAULT)),
            Map.entry(10, CursorType.createStandardCursor(GLFW_RESIZE_NS_CURSOR,     "slicef_CT_SOUTHRESIZE",      CursorType.DEFAULT)),
            Map.entry(11, CursorType.createStandardCursor(GLFW_RESIZE_NWSE_CURSOR,   "slicef_CT_SOUTHEASTRESIZE",  CursorType.DEFAULT)),
            Map.entry(12, CursorType.createStandardCursor(GLFW_RESIZE_NESW_CURSOR,   "slicef_CT_SOUTHWESTRESIZE",  CursorType.DEFAULT)),
            Map.entry(13, CursorType.createStandardCursor(GLFW_RESIZE_EW_CURSOR,     "slicef_CT_WESTRESIZE",       CursorType.DEFAULT)),

            // Double-ended Resizing
            Map.entry(14, CursorType.createStandardCursor(GLFW_RESIZE_NS_CURSOR,     "slicef_CT_NORTHSOUTHRESIZE", CursorType.DEFAULT)),
            Map.entry(15, CursorType.createStandardCursor(GLFW_RESIZE_EW_CURSOR,     "slicef_CT_EASTWESTRESIZE",   CursorType.DEFAULT)),
            Map.entry(18, CursorType.createStandardCursor(GLFW_RESIZE_EW_CURSOR,     "slicef_CT_COLUMNRESIZE",     CursorType.DEFAULT)),
            Map.entry(19, CursorType.createStandardCursor(GLFW_RESIZE_NS_CURSOR,     "slicef_CT_ROWRESIZE",        CursorType.DEFAULT)),

            // Movement and Status
            Map.entry(29, CursorType.createStandardCursor(GLFW_RESIZE_ALL_CURSOR,    "slicef_CT_MOVE",             CursorType.DEFAULT)),
            Map.entry(33, CursorType.createStandardCursor(GLFW_NOT_ALLOWED_CURSOR,   "slicef_CT_NOTALLOWED",       CursorType.DEFAULT)),

            // Grabbing
            Map.entry(41, CursorType.createStandardCursor(GLFW_POINTING_HAND_CURSOR, "slicef_CT_GRAB",             CursorType.DEFAULT)),
            Map.entry(42, CursorType.createStandardCursor(GLFW_POINTING_HAND_CURSOR, "slicef_CT_GRABBING",         CursorType.DEFAULT))
    );
    private static CursorType convertCursor(int cursorType) {
        if (cursorType == 37) return null; // CT_NONE, no cursor.

        if (cursorMap.containsKey(cursorType)) {
            return cursorMap.get(cursorType);
        }

        Slicef.LOGGER.warn("Unsupported cursor type: {}", cursorType);

        return CursorType.DEFAULT;
    }

    public CursorType getCurrentCursor() {
        return currentCursor;
    }
}
