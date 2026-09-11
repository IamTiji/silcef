package cc.tijiisreal.silcef.internals;

import cc.tijiisreal.silcef.AbstractTexture;
import cc.tijiisreal.silcef.Silcef;
import cc.tijiisreal.silcef.internals.win.WinAcceleratedPaintHandler;
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
            if (Platform.isWindows) {
                return new WinAcceleratedPaintHandler();
            }
        }
        return new VoidHandler();
    }

    static boolean initialize() {
        Platform.checkGPU();

        if (Platform.isGPUIntel) {
            return false; // Intel doesn't support necessary extensions
        }

        if (Platform.isWindows) {
            WinAcceleratedPaintHandler.initialize();
            return true;
        }
        return false;
    }
}
