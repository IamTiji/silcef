package com.tiji.silcef;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/// Render state implementation for Silcef browser.
/// You may use this to render browser to GUI, but
/// it is recommended to use [SlicefWidget] instead.
///
/// @since 1.0
/// @author Tiji
public class SlicefRenderState implements GuiElementRenderState {
    private final SlicefBrowser browser;
    private int x, y, width, height;

    public SlicefRenderState(SlicefBrowser browser) {
        this.browser = browser;

        Rectangle rect = browser.getMinecraftBounds();
        this.width = rect.width;
        this.height = rect.height;
    }

    public Rectangle getSize() {
        return new Rectangle(width, height);
    }

    @Override
    public void buildVertices(@NotNull VertexConsumer consumer) {
        // A-B
        // | |
        // C-D

        // 1. C - Bottom-Left
        consumer.addVertex(x, y + height, 0f)
                .setColor(255, 255, 255, 255)
                .setUv(0, 1);

        // 2. D - Bottom-Right
        consumer.addVertex(x + width, y + height, 0f)
                .setColor(255, 255, 255, 255)
                .setUv(1, 1);

        // 3. B - Top-Right
        consumer.addVertex(x + width, y, 0f)
                .setColor(255, 255, 255, 255)
                .setUv(1, 0);

        // 4. A - Top-Left
        consumer.addVertex(x, y, 0f)
                .setColor(255, 255, 255, 255)
                .setUv(0, 0);
    }

    @Override
    public @NotNull RenderPipeline pipeline() {
        return RenderPipelines.GUI_TEXTURED;
    }

    @Override
    public @NotNull TextureSetup textureSetup() {
        return TextureSetup.singleTexture(
                browser.getTexture().getTextureView(),
                browser.getTexture().getSampler()
        );
    }

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        return null;
    }

    @Override
    public @NotNull ScreenRectangle bounds() {
        return ScreenRectangle.of(ScreenAxis.HORIZONTAL, x, y, width, height);
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        browser.resize(width, height);
        this.width = width;
        this.height = height;
    }
}
