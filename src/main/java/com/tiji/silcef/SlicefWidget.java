package com.tiji.silcef;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.tiji.silcef.internals.utils.KeycodeUtils;
import com.tiji.silcef.internals.utils.UnsafeFieldOverride;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import java.awt.event.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import static org.lwjgl.glfw.GLFW.*;

public class SlicefWidget extends AbstractWidget {
    private final SlicefBrowser browser;
    private final SlicefRenderState state;

    private static final java.awt.Component fakeComponent = new Label();
    static {
        fakeComponent.setVisible(true); // awt is awful
    }

    public SlicefWidget(SlicefBrowser browser, int x, int y) {
        super(x, y, browser.getViewRect().width, browser.getViewRect().height, Component.literal("Slicef Browser Widget"));
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
        if (key == 1) return 3;
        if (key == 2) return 2;
        return key + 1;
    }

    private static int getModifiers() {
        long window = Minecraft.getInstance().getWindow().handle();
        int mod = 0;
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT  ) == GLFW_PRESS) mod |= KeyEvent.BUTTON1_DOWN_MASK;
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_MIDDLE) == GLFW_PRESS) mod |= KeyEvent.BUTTON2_DOWN_MASK;
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT ) == GLFW_PRESS) mod |= KeyEvent.BUTTON3_DOWN_MASK;

        //alt
        if (glfwGetKey        (window, GLFW_KEY_LEFT_ALT)        == GLFW_PRESS) mod |= KeyEvent.ALT_DOWN_MASK;
        if (glfwGetKey        (window, GLFW_KEY_RIGHT_ALT)       == GLFW_PRESS) mod |= KeyEvent.ALT_DOWN_MASK;

        //ctrl
        if (glfwGetKey        (window, GLFW_KEY_LEFT_CONTROL)     == GLFW_PRESS) mod |= KeyEvent.CTRL_DOWN_MASK;
        if (glfwGetKey        (window, GLFW_KEY_RIGHT_CONTROL)    == GLFW_PRESS) mod |= KeyEvent.CTRL_DOWN_MASK;

        //shift
        if (glfwGetKey        (window, GLFW_KEY_LEFT_SHIFT)      == GLFW_PRESS) mod |= KeyEvent.SHIFT_DOWN_MASK;
        if (glfwGetKey        (window, GLFW_KEY_RIGHT_SHIFT)     == GLFW_PRESS) mod |= KeyEvent.SHIFT_DOWN_MASK;

        return mod;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);

        //noinspection MagicConstant
        MouseEvent cefEvent = new MouseEvent(
                fakeComponent,
                MouseEvent.MOUSE_MOVED,
                0,
                getModifiers(),
                normalizeMouse(mouseX, getX()), normalizeMouse(mouseY, getY()),
                0, 0,
                0,
                false,
                0
        );
        browser.sendMouseEvent(cefEvent);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean isDoubleClick) {
        if (!this.isHovered) return super.mouseClicked(event, isDoubleClick);

        //noinspection MagicConstant
        MouseEvent cefEvent = new MouseEvent(
                fakeComponent,
                MouseEvent.MOUSE_PRESSED,
                0,
                getModifiers(),
                normalizeMouse(event.x(), getX()), normalizeMouse(event.y(), getY()),
                0, 0,
                isDoubleClick ? 2 : 1,
                false,
                fixMouse(event.button())
        );
        browser.sendMouseEvent(cefEvent);
        browser.setFocus(true);

        return true;
    }

    @Override
    public boolean mouseReleased(@NotNull MouseButtonEvent event) {
        if (!this.isHovered) return super.mouseReleased(event);

        //noinspection MagicConstant
        MouseEvent cefEvent = new MouseEvent(
                fakeComponent,
                MouseEvent.MOUSE_RELEASED,
                0,
                getModifiers(),
                normalizeMouse(event.x(), getX()), normalizeMouse(event.y(), getY()),
                0, 0,
                0,
                false,
                fixMouse(event.button())
        );
        browser.sendMouseEvent(cefEvent);

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!this.isHovered) return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        //noinspection MagicConstant
        MouseWheelEvent mouseWheelEvent = new MouseWheelEvent(
                fakeComponent,
                MouseEvent.MOUSE_WHEEL,
                0,
                getModifiers(),
                normalizeMouse(mouseX, getX()), normalizeMouse(mouseY, getY()),
                0,
                false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL,
                (int) scrollY*120,
                1
        );
        browser.sendMouseWheelEvent(mouseWheelEvent);

        return true;
    }

    @Override
    public boolean keyPressed(@NotNull net.minecraft.client.input.KeyEvent event) {
        if (!this.isFocused()) return super.keyPressed(event);

        //noinspection MagicConstant
        KeyEvent cefEvent = new KeyEvent(
                fakeComponent,
                KeyEvent.KEY_PRESSED,
                0,
                getModifiers(),
                KeycodeUtils.KEYCODE_MAP[event.key()],
                event.key() == GLFW_KEY_BACKSPACE ? '\b' : KeyEvent.CHAR_UNDEFINED,
                KeycodeUtils.KEY_LOCATION_MAP[event.key()]
        );
        UnsafeFieldOverride.overrideLongField("scancode", cefEvent, event.scancode());
        // Technically macos and linux has different map, but it is only read on windows, so should be fine
        UnsafeFieldOverride.overrideLongField("rawCode", cefEvent, KeycodeUtils.KEYCODE_MAP[event.key()]);
        browser.sendKeyEvent(cefEvent);

        return true;
    }

    @Override
    public boolean keyReleased(@NotNull net.minecraft.client.input.KeyEvent event) {
        if (!this.isFocused()) return super.keyReleased(event);

        //noinspection MagicConstant
        KeyEvent cefEvent = new KeyEvent(
                fakeComponent,
                KeyEvent.KEY_RELEASED,
                0,
                getModifiers(),
                KeycodeUtils.KEYCODE_MAP[event.key()],
                event.key() == GLFW_KEY_BACKSPACE ? '\b' : KeyEvent.CHAR_UNDEFINED,
                KeycodeUtils.KEY_LOCATION_MAP[event.key()]
        );
        UnsafeFieldOverride.overrideLongField("scancode", cefEvent, event.scancode());
        // Technically macos and linux has different map, but it is only read on windows, so should be fine
        UnsafeFieldOverride.overrideLongField("rawCode", cefEvent, KeycodeUtils.KEYCODE_MAP[event.key()]);
        browser.sendKeyEvent(cefEvent);

        return true;
    }

    @Override
    public boolean charTyped(@NotNull CharacterEvent event) {
        if (!this.isFocused()) return super.charTyped(event);

        //noinspection MagicConstant
        KeyEvent cefEvent = new KeyEvent(
                fakeComponent,
                KeyEvent.KEY_TYPED,
                0,
                getModifiers(),
                KeyEvent.VK_UNDEFINED,
                (char) event.codepoint()
        );
        browser.sendKeyEvent(cefEvent);

        return true;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);

        browser.setFocus(focused);
    }
}
