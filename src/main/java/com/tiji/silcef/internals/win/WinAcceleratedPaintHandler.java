package com.tiji.silcef.internals.win;

import com.tiji.silcef.AbstractTexture;
import com.tiji.silcef.internals.AcceleratedPaintHandler;
import net.minecraft.client.Minecraft;
import org.cef.misc.CefAcceleratedPaintInfo;

import java.util.concurrent.CompletableFuture;

public class WinAcceleratedPaintHandler implements AcceleratedPaintHandler {
    private DxTexture hardwareTexture;

    @Override
    public void onPaint(CefAcceleratedPaintInfo info) {
        if (hardwareTexture == null) return;

        CompletableFuture<Void> future = new CompletableFuture<>();

        Minecraft.getInstance().execute(() -> {
            hardwareTexture.onPaint(info); // Doesn't throw exception; if it does, dont worry as its gonna crash the JVM
            future.complete(null);
        });

        future.join();
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
}
