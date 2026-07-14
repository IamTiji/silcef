package com.tiji.silcef.internals;

import com.tiji.silcef.AbstractTexture;
import com.tiji.silcef.Silcef;
import com.tiji.silcef.internals.win.WinAcceleratedPaintHandler;
import org.cef.misc.CefAcceleratedPaintInfo;

public interface AcceleratedPaintHandler {
    void onPaint(CefAcceleratedPaintInfo info, int width, int height);
    void onResize(int width, int height);
    void destroy();
    AbstractTexture getTexture();

    class VoidHandler implements AcceleratedPaintHandler {
        @Override public void onPaint(CefAcceleratedPaintInfo info, int width, int height) {}

        @Override public void onResize(int width, int height) {}

        @Override public void destroy() {}

        @Override public AbstractTexture getTexture() { return null; }
    }

    static AcceleratedPaintHandler getInstance() {
        if (Silcef.isAcceleratedPaintAllowed) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                return new WinAcceleratedPaintHandler();
            }
        }
        return new VoidHandler();
    }

    static boolean initialize() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            WinAcceleratedPaintHandler.initialize();
            return true;
        }
        return false;
    }
}
