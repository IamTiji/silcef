package com.tiji.silcef;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

import java.util.HashMap;
import java.util.OptionalDouble;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL33C.GL_TEXTURE_SWIZZLE_B;
import static org.lwjgl.opengl.GL33C.GL_TEXTURE_SWIZZLE_R;
import static org.lwjgl.opengl.GL43C.glObjectLabel;
import static org.lwjgl.opengl.WGLNVDXInterop.*;

public class DxTexture {
    public final long DXTextureHandle;
    public final int GLTextureId;
    public final PointerBuffer DXTextureHandleBuffer;

    public final int width;
    public final int height;

    public final GlTexture mcGlTexture;
    public final GpuTextureView mcTextureView;
    public final GpuSampler mcSampler;

    private boolean destroyed = false;

    private static final HashMap<Long, DxTexture> PREINIT_INSTANCES = new HashMap<>();

    public static void destroyAll() {
        for (DxTexture dxTexture : PREINIT_INSTANCES.values()) {
            dxTexture.destroy();
        }
        PREINIT_INSTANCES.clear();
    }

    public static DxTexture create(long dxTexture, int width, int height) {
        DxTexture dxTextureInstance = PREINIT_INSTANCES.get(dxTexture);
        if (dxTextureInstance != null) {
            return dxTextureInstance;
        }

        PREINIT_INSTANCES.put(dxTexture, new DxTexture(dxTexture, width, height));
        return PREINIT_INSTANCES.get(dxTexture);
    }

    private DxTexture(long dxTexture, int width, int height) {
        GLTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, GLTextureId);

        DXTextureHandle = wglDXRegisterObjectNV(Slicef.DXDevice,
                dxTexture,
                GLTextureId,
                GL_TEXTURE_2D,
                WGL_ACCESS_READ_ONLY_NV
        );
        DXTextureHandleBuffer = BufferUtils.createPointerBuffer(1);
        DXTextureHandleBuffer.put(0, DXTextureHandle);
        DXTextureHandleBuffer.flip();

        this.width = width;
        this.height = height;

        String name = Slicef.getUniqueName("acceleratedTexture");
        mcGlTexture = new GlTextureWrapper(GpuTexture.USAGE_RENDER_ATTACHMENT,
                name,
                TextureFormat.RGBA8,
                width, height,
                0, 0, GLTextureId
        );
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_B, GL_RED);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_R, GL_BLUE);
        glObjectLabel(GL_TEXTURE, GLTextureId, name);

        mcTextureView = new GpuTextureView(mcGlTexture, 0, 0) {
            @Override
            public void close() {
                destroy();
            }

            @Override
            public boolean isClosed() {
                return destroyed;
            }
        };

        mcSampler = new GpuSampler() {
            @Override
            public @NotNull AddressMode getAddressModeU() {
                return AddressMode.CLAMP_TO_EDGE;
            }

            @Override
            public @NotNull AddressMode getAddressModeV() {
                return AddressMode.CLAMP_TO_EDGE;
            }

            @Override
            public @NotNull FilterMode getMinFilter() {
                return FilterMode.LINEAR;
            }

            @Override
            public @NotNull FilterMode getMagFilter() {
                return FilterMode.LINEAR;
            }

            @Override
            public int getMaxAnisotropy() {
                return 0;
            }

            @Override
            public @NotNull OptionalDouble getMaxLod() {
                return OptionalDouble.of(1);
            }

            @Override
            public void close() {
                destroy();
            }
        };
    }

    public void lock() {
        if (destroyed) throw new IllegalStateException("Texture already destroyed");

        wglDXLockObjectsNV(Slicef.DXDevice, DXTextureHandleBuffer);
    }

    public void unlock() {
        if (destroyed) throw new IllegalStateException("Texture already destroyed");

        wglDXUnlockObjectsNV(Slicef.DXDevice, DXTextureHandleBuffer);
    }

    public void destroy() {
        if (destroyed) throw new IllegalStateException("Texture already destroyed");
        destroyed = true;
        wglDXUnregisterObjectNV(Slicef.DXDevice, DXTextureHandle);
        glDeleteTextures(GLTextureId);
    }
}
