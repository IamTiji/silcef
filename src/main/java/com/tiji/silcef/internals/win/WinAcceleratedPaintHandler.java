package com.tiji.silcef.internals.win;

import com.tiji.silcef.AbstractTexture;
import com.tiji.silcef.Slicef;
import com.tiji.silcef.internals.AcceleratedPaintHandler;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.time.StopWatch;
import org.cef.misc.CefAcceleratedPaintInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.glfwExtensionSupported;

public class WinAcceleratedPaintHandler implements AcceleratedPaintHandler {
    private DxTexture hardwareTexture;

    /// **Things you should understand before using this:**
    ///
    /// OpenGL commands used in this method forces pipeline flush;
    /// this means that all the pending work that Minecraft haven't
    /// done yet will add on to this call. The time logged from
    /// here shouldn't be a measure of how performant this mod is.
    private static final boolean shouldLogTime = false;
    private final StopWatch timer = StopWatch.create();
    @Override
    public void onPaint(CefAcceleratedPaintInfo info, int width, int height) {
        if (shouldLogTime)
            timer.start();

        if (hardwareTexture == null) return;

        CompletableFuture<Void> future = new CompletableFuture<>();

        Minecraft.getInstance().execute(() -> {
            hardwareTexture.onPaint(info, width, height);
            future.complete(null);
        });

        future.join();

        if (shouldLogTime) {
            timer.stop();
            Slicef.LOGGER.info("Painting took {} μs", timer.getTime(TimeUnit.MICROSECONDS));
            timer.reset();
        }
    }

    @Override
    public void onResize(int width, int height) {
        if (hardwareTexture != null) hardwareTexture.destroy();
        hardwareTexture = new DxTexture(width, height);
    }

    @Override
    public void destroy() {
        hardwareTexture.destroy();
    }

    @Override
    public AbstractTexture getTexture() {
        return hardwareTexture;
    }

    public static void initialize() {
        if (!glfwExtensionSupported("GL_EXT_memory_object")) {
            Slicef.LOGGER.warn("GL_EXT_memory_object extension is not supported on this system. " +
                    "If your GPU supports it, check if you have appropriate drivers installed");
            throw new RuntimeException("DirectX initialization failed");
        }
    }
}
