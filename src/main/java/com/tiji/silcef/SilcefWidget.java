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
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.glfw.GLFW.*;

/// Widget instance that may be used to render browser onto
/// GUI. This handles cursor and inputs, so you may just add
/// it to your GUI.
///
/// You need to add this line at the end of your screen
/// constructor; Minecraft doesn't give mouse release events
/// for mouse clicks that aren't primary click. This will
/// make user experience horrible, so make sure to add this.
/// ```java
/// ScreenMouseEvents.beforeMouseRelease(this).register(((screen, context) -> widget.mouseReleased(context)));
/// ```
///
/// @since 1.0
/// @author Tiji
public class SilcefWidget extends AbstractWidget {
    private final SilcefBrowser browser;
    private final SilcefRenderState state;
    private final SilcefEventHandler eventHandler;

    /// Constructs browser widget. You should never construct this every
    /// time `init` method is called. Instead, you should construct this
    /// in screen constructor, then resize accordingly in `init` method.
    ///
    /// Also, this widget is not functional until first `resize` is
    /// called. Make sure to call it in `init` method.
    ///
    /// @since 1.0
    /// @author Tiji
    public SilcefWidget(SilcefBrowser browser, int x, int y) {
        super(x, y, browser.getViewRect().width, browser.getViewRect().height, Component.literal("Silcef Browser Widget"));
        this.browser = browser;
        this.state = new SilcefRenderState(browser);
        this.eventHandler = new SilcefEventHandler(browser);
        state.setPos(x, y);
    }

    /// Resizes widget to new size. Browser is also resized, therefore
    /// resizing often should be avoided.
    ///
    /// @since 1.0
    /// @author Tiji
    /// @see SilcefBrowser#resize(int, int)
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

    private static int normalizeMouse(double value, int start) {
        return (int) ((value - start) * Minecraft.getInstance().getWindow().getGuiScale());
    }

    @Override
    public @NotNull NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);

        eventHandler.mouseMoved(
                normalizeMouse(mouseX, getX()),
                normalizeMouse(mouseY, getY())
        );
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean isDoubleClick) {
        if (!this.isHovered) return super.mouseClicked(event, isDoubleClick);

        eventHandler.mousePressed(
                normalizeMouse(event.x(), getX()),
                normalizeMouse(event.y(), getY()),
                event.button()
        );

        return true;
    }

    @Override
    public boolean mouseReleased(@NotNull MouseButtonEvent event) {
        if (!this.isHovered) return super.mouseReleased(event);

        eventHandler.mouseReleased(
                normalizeMouse(event.x(), getX()),
                normalizeMouse(event.y(), getY()),
                event.button()
        );

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!this.isHovered) return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        eventHandler.mouseScrolled(
                normalizeMouse(mouseX, getX()),
                normalizeMouse(mouseY, getY()),
                scrollY
        );

        return true;
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        if (!this.isFocused()) return super.keyPressed(event);

        eventHandler.keyPressed(event.key(), event.scancode());

        return true;
    }

    @Override
    public boolean keyReleased(@NotNull KeyEvent event) {
        if (!this.isFocused()) return super.keyReleased(event);


        eventHandler.keyReleased(event.key(), event.scancode());

        return true;
    }

    @Override
    public boolean charTyped(@NotNull CharacterEvent event) {
        if (!this.isFocused()) return super.charTyped(event);

        eventHandler.charTyped((char) event.codepoint());

        return true;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);

        browser.setFocus(focused);
    }
}
