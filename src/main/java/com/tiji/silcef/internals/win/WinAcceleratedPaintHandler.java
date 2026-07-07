package com.tiji.silcef.internals.win;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.tiji.silcef.AbstractTexture;
import com.tiji.silcef.Slicef;
import com.tiji.silcef.internals.AcceleratedPaintHandler;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.time.StopWatch;
import org.cef.misc.CefAcceleratedPaintInfo;

import java.util.concurrent.CompletableFuture;

import static org.lwjgl.glfw.GLFW.glfwExtensionSupported;
import static org.lwjgl.opengl.WGLNVDXInterop.wglDXOpenDeviceNV;

public class WinAcceleratedPaintHandler implements AcceleratedPaintHandler {
    public static long DXDevice;
    public static D3D11Device DXDeviceContainer;
    private DxTexture hardwareTexture;

    private static final boolean shouldLogTime = false;
    private final StopWatch timer = StopWatch.create();
    @Override
    public void onPaint(CefAcceleratedPaintInfo info) {
        if (shouldLogTime)
            timer.start();

        if (hardwareTexture == null) return;

        CompletableFuture<Void> future = new CompletableFuture<>();

        Minecraft.getInstance().execute(() -> {
            hardwareTexture.onPaint(info); // Doesn't throw exception; if it does, dont worry as its gonna crash the JVM
            future.complete(null);
        });

        future.join();

        if (shouldLogTime) {
            timer.stop();
            Slicef.LOGGER.info("Painting took {} ms", timer.getTime());
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
        if (!glfwExtensionSupported("WGL_NV_DX_interop2")) {
            Slicef.LOGGER.warn("WGL_NV_DX_interop2 extension is not supported on this system. " +
                    "If your GPU supports it, check if you have appropriate drivers installed");
            throw new RuntimeException("DirectX initialization failed");
        }

        D3D11.initialize();

        PointerByReference ppDevice = new PointerByReference();
        PointerByReference ppContext = new PointerByReference();

        int hr = D3D11.get().D3D11CreateDevice(
                null,
                D3D11.D3D_DRIVER_TYPE_HARDWARE,
                null,
                0,
                null, 0,
                D3D11.D3D11_SDK_VERSION,
                ppDevice,
                null,
                ppContext
        );

        if (hr != 0) throw new RuntimeException("DirectX initialization failed: %s".formatted(hr));

        DXDevice = wglDXOpenDeviceNV(Pointer.nativeValue(ppDevice.getValue()));
        DXDeviceContainer = new D3D11Device(ppDevice.getValue());

        Slicef.LOGGER.info("DirectX device is linked");
    }
}
