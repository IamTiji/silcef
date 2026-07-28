package com.tiji.silcef;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.tiji.silcef.internals.WrapperTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

/// A class representing all textures used in Silcef.
///
/// @since 1.0
/// @author Tiji
abstract public class AbstractTexture {
    private static final TextureManager tm = Minecraft.getInstance().getTextureManager();

    private Identifier id;

    protected void registerToManager(String name) {
        id = Identifier.fromNamespaceAndPath("silcef", name);

        tm.register(id, new WrapperTexture(this));
    }

    protected void unregisterFromManager() {
        tm.release(id);
    }

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

    /// Returns [net.minecraft.resources.Identifier] to this texture. This
    /// may directly be used on [net.minecraft.client.gui.GuiGraphics].
    /// This value will never be null.
    ///
    /// @return identifier to browser's texture
    /// @since 1.0
    /// @author Tiji
    public Identifier getId() {
        return id;
    }
}
