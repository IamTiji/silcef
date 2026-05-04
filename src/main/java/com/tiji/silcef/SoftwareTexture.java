package com.tiji.silcef;

import com.mojang.blaze3d.opengl.GlSampler;
import com.mojang.blaze3d.opengl.GlTextureView;
import com.mojang.blaze3d.textures.*;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;

import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.opengl.GL43C.glObjectLabel;

public class SoftwareTexture {
    private final int GLTextureId;
    private final int width, height;
    public GlTextureWrapper mcGlTexture;
    public GpuTextureView mcTextureView;
    public GpuSampler mcSampler;

    private boolean destroyed = false;

    public SoftwareTexture(int width, int height) {
        this.width = width;
        this.height = height;
        this.GLTextureId = glGenTextures();
        String name = Slicef.getUniqueName("softwareTexture");

        glBindTexture(GL_TEXTURE_2D, GLTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0L);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_B, GL_RED);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_R, GL_BLUE);
        glObjectLabel(GL_TEXTURE, GLTextureId, name);

        mcGlTexture = new GlTextureWrapper(GpuTexture.USAGE_RENDER_ATTACHMENT,
                name,
                TextureFormat.RGBA8,
                width, height,
                0, 0, GLTextureId
        );

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

        mcSampler = new GlSampler(
                AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE,
                FilterMode.LINEAR, FilterMode.LINEAR,
                0,
                OptionalDouble.of(1)
        );
    }


    public void onPaint(Rectangle[] dirtyRects, ByteBuffer pixels) {
        if (destroyed) throw new IllegalStateException("Texture has already been destroyed");

        glBindTexture(GL_TEXTURE_2D, GLTextureId);
        glPixelStorei(GL_UNPACK_ROW_LENGTH, width);
        glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
        glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
        for (Rectangle dirtyRect : dirtyRects) {
            int x = (int) dirtyRect.getX();
            int y = (int) dirtyRect.getY();
            int w = (int) dirtyRect.getWidth();
            int h = (int) dirtyRect.getHeight();

            pixels.position(x * 4 + y * width * 4);

            glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, w, h, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        }
        glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
    }

    public void destroy() {
        if (destroyed) throw new IllegalStateException("Texture has already been destroyed");

        destroyed = true;
        glDeleteTextures(GLTextureId);
    }
}
