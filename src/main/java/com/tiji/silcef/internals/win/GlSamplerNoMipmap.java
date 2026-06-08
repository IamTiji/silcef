package com.tiji.silcef.internals.win;

import com.mojang.blaze3d.opengl.GlSampler;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import org.lwjgl.opengl.GL33C;

import java.util.OptionalDouble;

import static org.lwjgl.opengl.GL11C.*;

public class GlSamplerNoMipmap extends GlSampler {
    public GlSamplerNoMipmap(AddressMode addressModeU,
                             AddressMode addressModeV,
                             FilterMode minFilter,
                             FilterMode magFilter,
                             int maxAnisotropy,
                             OptionalDouble maxLod) {
        super(addressModeU, addressModeV, minFilter, magFilter, maxAnisotropy, maxLod);

        switch (minFilter) {
            case NEAREST -> GL33C.glSamplerParameteri(this.getId(), GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            case LINEAR -> GL33C.glSamplerParameteri(this.getId(), GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        }
    }
}
