package com.tiji.silcef.internals.win;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.opengl.GlTextureView;
import com.mojang.blaze3d.textures.*;
import com.tiji.silcef.AbstractTexture;
import com.tiji.silcef.internals.utils.GlTextureWrapper;
import com.tiji.silcef.internals.utils.NameUtils;
import org.cef.misc.CefAcceleratedPaintInfo;

import java.nio.ByteBuffer;
import java.util.OptionalDouble;

import static org.lwjgl.opengl.EXTMemoryObject.*;
import static org.lwjgl.opengl.EXTMemoryObjectWin32.*;
import static org.lwjgl.opengl.GL45C.*;

public class DxTexture extends AbstractTexture {
    private final int GLTextureId;

    private final int width;
    private final int height;

    private final GlTexture mcGlTexture;
    private final GpuTextureView mcTextureView;
    private final GpuSampler mcSampler;

    private boolean destroyed = false;

    public DxTexture(int width, int height) {
        this.width = width;
        this.height = height;

        GLTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, GLTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);

        String name = NameUtils.getUniqueName("acceleratedTexture");
        mcGlTexture = new GlTextureWrapper(GpuTexture.USAGE_RENDER_ATTACHMENT,
                name,
                TextureFormat.RGBA8,
                width, height,
                0, 0, GLTextureId
        );
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_R, GL_BLUE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_B, GL_RED);

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

    public void onPaint(CefAcceleratedPaintInfo info, int width, int height) {
        if (width != this.width || height != this.height) return;

        int srcTextureId;

        int memObject = glCreateMemoryObjectsEXT();
        glImportMemoryWin32HandleEXT(memObject, 0,
                GL_HANDLE_TYPE_D3D11_IMAGE_EXT, info.shared_texture_handle);

        srcTextureId = glCreateTextures(GL_TEXTURE_2D);
        glTextureStorageMem2DEXT(srcTextureId, 1, GL_RGBA8, width, height, memObject, 0);

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

        glDeleteTextures(srcTextureId);
        glDeleteMemoryObjectsEXT(memObject);

        glFlush(); // make sure that all commands are executed before cef takes it back
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
