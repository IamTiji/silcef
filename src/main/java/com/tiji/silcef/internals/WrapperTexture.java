package com.tiji.silcef.internals;

import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.jspecify.annotations.NonNull;

public class WrapperTexture extends AbstractTexture {
    private final com.tiji.silcef.AbstractTexture parent;

    public WrapperTexture(com.tiji.silcef.AbstractTexture parent) {
        this.parent = parent;
    }

    @Override
    public void close() {}

    @Override
    public @NonNull GpuTexture getTexture() {
        return parent.getMcTexture();
    }

    @Override
    public @NonNull GpuTextureView getTextureView() {
        return parent.getTextureView();
    }

    @Override
    public @NonNull GpuSampler getSampler() {
        return parent.getSampler();
    }
}
