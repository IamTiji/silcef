package com.tiji.silcef;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.TextureFormat;

public class GlTextureWrapper extends GlTexture {
    public GlTextureWrapper(@Usage int usage, String label, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels, int id) {
        super(usage, label, format, width, height, depthOrLayers, mipLevels, id);
    }
}
