package com.tiji.silcef;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;

/// A class representing all textures used in Silcef.
///
/// @since 1.0
/// @author Tiji
abstract public class AbstractTexture {
    /// @return [GlTexture] instance representing this texture
    /// @since 1.0
    /// @author Tiji
    public abstract GlTexture getMcTexture();
    /// @return [GpuTextureView] instance representing this texture
    /// @since 1.0
    /// @author Tiji
    public abstract GpuTextureView getTextureView();
    /// @return [GpuSampler] instance representing this texture
    /// @since 1.0
    /// @author Tiji
    public abstract GpuSampler getSampler();

    /// @return width of this texture
    /// @since 1.0
    /// @author Tiji
    public abstract int getWidth();
    /// @return width of this texture
    /// @since 1.0
    /// @author Tiji
    public abstract int getHeight();

    /// Destroys this texture. This texture must not be used after this
    /// method call.
    ///
    /// @since 1.0
    /// @author Tiji
    public abstract void destroy();
}
