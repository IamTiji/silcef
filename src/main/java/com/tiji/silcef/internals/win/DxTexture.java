package com.tiji.silcef.internals.win;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.opengl.GlTextureView;
import com.mojang.blaze3d.textures.*;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import com.tiji.silcef.AbstractTexture;
import com.tiji.silcef.Slicef;
import com.tiji.silcef.internals.GlTextureWrapper;
import org.cef.misc.CefAcceleratedPaintInfo;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.OptionalDouble;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12C.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12C.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL43C.glCopyImageSubData;
import static org.lwjgl.opengl.GL43C.glObjectLabel;
import static org.lwjgl.opengl.WGLNVDXInterop.*;
import static org.lwjgl.system.windows.WinBase.GetLastError;

public class DxTexture extends AbstractTexture {
    private record SharedTexture(long handle, int opengl) {}

    private final int GLTextureId;

    private final int width;
    private final int height;

    private final GlTexture mcGlTexture;
    private final GpuTextureView mcTextureView;
    private final GpuSampler mcSampler;

    private boolean destroyed = false;

    private static final HashMap<Long, SharedTexture> PREINIT_INSTANCES = new HashMap<>();

    public static void destroyAll() {
        for (SharedTexture sharedTexture : PREINIT_INSTANCES.values()) {
            wglDXUnregisterObjectNV(Slicef.DXDevice, sharedTexture.handle);
            glDeleteTextures(sharedTexture.opengl);
        }
        PREINIT_INSTANCES.clear();
    }

    public DxTexture(int width, int height) {
        this.width = width;
        this.height = height;

        GLTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, GLTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);

        String name = Slicef.getUniqueName("acceleratedTexture");
        mcGlTexture = new GlTextureWrapper(GpuTexture.USAGE_RENDER_ATTACHMENT,
                name,
                TextureFormat.RGBA8,
                width, height,
                0, 0, GLTextureId
        );
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_B, GL_RED);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_R, GL_BLUE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glObjectLabel(GL_TEXTURE, GLTextureId, name);

        mcTextureView = new GlTextureView(mcGlTexture, 0, 1) {
            @Override
            public void close() {
                destroy();
            }

            @Override
            public boolean isClosed() {
                return destroyed;
            }
        };

        mcSampler = new GlSamplerNoMipmap(
                AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE,
                FilterMode.LINEAR, FilterMode.LINEAR,
                1,
                OptionalDouble.of(1)
        );
    }

    public void onPaint(CefAcceleratedPaintInfo info) {
        int srcTextureId;
        long handle;
        PointerByReference texture = new PointerByReference();
        int hr = Slicef.DXDeviceContainer.openSharedResource1(
                new WinNT.HANDLE(Pointer.createConstant(info.shared_texture_handle)),
                new Guid.REFIID(Guid.IID.fromString("{6F15AAF2-D208-4E89-9AB4-489535D34F9C}").getPointer()),
                texture
        );
        if (hr != 0) {
            Slicef.LOGGER.warn("Failed to open texture {} with error {}", info.shared_texture_handle, "%X".formatted(hr));
            return;
        }

        srcTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, srcTextureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        long pdxResource = Pointer.nativeValue(texture.getValue());
        // nvidia, this wasn't on manual
        wglDXSetResourceShareHandleNV(pdxResource, info.shared_texture_handle);
        handle = wglDXRegisterObjectNV(
                Slicef.DXDevice,
                pdxResource,
                srcTextureId,
                GL_TEXTURE_2D,
                WGL_ACCESS_READ_ONLY_NV);

        if (handle == 0L) {
            Slicef.LOGGER.warn("Failed to register texture {} with error {}", info.shared_texture_handle, GetLastError());
            return;
        }

        PointerBuffer handlePointer = BufferUtils.createPointerBuffer(1).put(handle).flip();
        wglDXLockObjectsNV(Slicef.DXDevice, handlePointer);

        // this probably dont work on macos but future me will fix it
        glCopyImageSubData(
                srcTextureId,
                GL_TEXTURE_2D,
                0,
                0, 0, 0,
                GLTextureId,
                GL_TEXTURE_2D,
                0,
                0, 0, 0,
                width, height, 1
        );

        wglDXUnlockObjectsNV(Slicef.DXDevice, handlePointer);
    }

    @Override
    public GlTexture getMcTexture() {
        return mcGlTexture;
    }

    @Override
    public GpuTextureView getTextureView() {
        return mcTextureView;
    }

    @Override
    public GpuSampler getSampler() {
        return mcSampler;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void destroy() {
        if (destroyed) throw new IllegalStateException("Texture already destroyed");
        destroyed = true;
        glDeleteTextures(GLTextureId);
    }
}
