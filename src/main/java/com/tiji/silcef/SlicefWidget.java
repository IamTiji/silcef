package com.tiji.silcef;

import com.mojang.blaze3d.platform.cursor.CursorType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.cef.event.CefKeyEvent;
import org.cef.event.CefMouseEvent;
import org.cef.event.CefMouseWheelEvent;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.glfw.GLFW.*;

public class SlicefWidget extends AbstractWidget {
    private final SlicefBrowser browser;
    private final SlicefRenderState state;

    private int mouseModifier;

    public SlicefWidget(SlicefBrowser browser, int x, int y) {
        super(x, y, browser.getViewRect(browser).width, browser.getViewRect(browser).height, Component.literal("Slicef Browser Widget"));
        this.browser = browser;
        this.state = new SlicefRenderState(browser);
        state.setPos(x, y);
    }

    public void resize(int width, int height) {
        state.setSize(width, height);
        super.setRectangle(width, height, getX(), getY());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.guiRenderState.submitGuiElement(state);

        CursorType cursor = browser.getCurrentCursor();
        long handle = Minecraft.getInstance().getWindow().handle();
        if (cursor != null) {
            glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            guiGraphics.requestCursor(cursor);
        } else {
            glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        }
    }

    @Override
    public @NotNull NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

    private static int normalizeMouse(double value, int start) {
        return (int) ((value - start) * Minecraft.getInstance().getWindow().getGuiScale());
    }

    private static int fixMouse(int key) {
        if (key == 1) return 2;
        if (key == 2) return 1;
        return key;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        CefMouseEvent event = new CefMouseEvent(
                CefMouseEvent.MOUSE_MOVED,
                normalizeMouse(mouseX, getX()), normalizeMouse(mouseY, getY()),
                0, 0, 0
        );
        browser.sendMouseEvent(event);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean isDoubleClick) {
        if (!this.isHovered) return super.mouseClicked(event, isDoubleClick);
        CefMouseEvent cefEvent = new CefMouseEvent(
                GLFW_PRESS,
                normalizeMouse(event.x(), getX()), normalizeMouse(event.y(), getY()),
                isDoubleClick ? 2 : 1, fixMouse(event.button()), event.modifiers()
        );
        browser.sendMouseEvent(cefEvent);
        browser.setFocus(true);

        return true;
    }

    @Override
    public boolean mouseReleased(@NotNull MouseButtonEvent event) {
        if (!this.isHovered) return super.mouseReleased(event);
        CefMouseEvent cefEvent = new CefMouseEvent(
                GLFW_RELEASE,
                normalizeMouse(event.x(), getX()), normalizeMouse(event.y(), getY()),
                0, fixMouse(event.button()), event.modifiers()
        );
        browser.sendMouseEvent(cefEvent);

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!this.isHovered) return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        CefMouseWheelEvent cefEvent = new CefMouseWheelEvent(
                CefMouseWheelEvent.WHEEL_UNIT_SCROLL,
                normalizeMouse(mouseX, getX()), normalizeMouse(mouseY, getY()),
                scrollY*2, 0
        );
        browser.sendMouseWheelEvent(cefEvent);

        return true;
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        if (!this.isFocused()) return super.keyPressed(event);

        CefKeyEvent cefEvent = new CefKeyEvent(
                CefKeyEvent.KEY_PRESS,
                event.key(),
                (char) event.key(),
                event.modifiers()
        );
        browser.sendKeyEvent(cefEvent);

        return true;
    }

    @Override
    public boolean keyReleased(@NotNull KeyEvent event) {
        if (!this.isFocused()) return super.keyReleased(event);

        CefKeyEvent cefEvent = new CefKeyEvent(
                CefKeyEvent.KEY_RELEASE,
                event.key(),
                (char) event.key(),
                event.modifiers()
        );
        browser.sendKeyEvent(cefEvent);

        return true;
    }

    @Override
    public boolean charTyped(@NotNull CharacterEvent event) {
        if (!this.isFocused()) return super.charTyped(event);

        CefKeyEvent cefEvent = new CefKeyEvent(
                CefKeyEvent.KEY_TYPE,
                event.codepoint(),
                (char) event.codepoint(),
                event.modifiers()
        );
        browser.sendKeyEvent(cefEvent);

        return true;
    }
}
