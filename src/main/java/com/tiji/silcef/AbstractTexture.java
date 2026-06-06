package com.tiji.silcef;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;

abstract public class AbstractTexture {
    public abstract GlTexture getMcTexture();
    public abstract GpuTextureView getTextureView();
    public abstract GpuSampler getSampler();

    public abstract int getWidth();
    public abstract int getHeight();

    public abstract void destroy();
}
