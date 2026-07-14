package com.tiji.silcef;

import com.tiji.silcef.internals.utils.KeycodeUtils;
import com.tiji.silcef.internals.utils.UnsafeFieldOverride;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;

/// Utility class to handle input events easily.
/// Methods are self-explanatory with their name,
/// so documentations for those are omitted.
///
/// @since 1.0
/// @author Tiji
public class SilcefEventHandler {
    private static final java.awt.Component fakeComponent = new Label();
    static {
        fakeComponent.setVisible(true); // awt is awful
    }

    private static final int SCROLL_MULTIPLIER = 120;

    private final SilcefBrowser browser;

    public SilcefEventHandler(SilcefBrowser browser) {
        this.browser = browser;
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

    private static final boolean shouldFixScancode
            = System.getProperty("os.name").toLowerCase().contains("win");
    private static int fixScancode(int scancode) {
        if (shouldFixScancode) return scancode & ~0x100;
        return scancode;
    }

    public void mouseMoved(int mouseX, int mouseY) {
        //noinspection MagicConstant
        MouseEvent cefEvent = new MouseEvent(
                fakeComponent,
                MouseEvent.MOUSE_MOVED,
                0,
                getModifiers(),
                mouseX, mouseY,
                0, 0,
                0,
                false,
                0
        );
        browser.sendMouseEvent(cefEvent);
    }

    public void mousePressed(int x, int y, int button) {
        //noinspection MagicConstant
        MouseEvent cefEvent = new MouseEvent(
                fakeComponent,
                MouseEvent.MOUSE_PRESSED,
                0,
                getModifiers(),
                x, y,
                0, 0,
                1,
                false,
                fixMouse(button)
        );
        browser.sendMouseEvent(cefEvent);
        browser.setFocus(true);
    }

    public void mouseReleased(int x, int y, int button) {
        //noinspection MagicConstant
        MouseEvent cefEvent = new MouseEvent(
                fakeComponent,
                MouseEvent.MOUSE_RELEASED,
                0,
                getModifiers(),
                x, y,
                0, 0,
                1,
                false,
                fixMouse(button)
        );
        browser.sendMouseEvent(cefEvent);
        browser.setFocus(true);
    }

    public void mouseScrolled(int x, int y, double scrollAmount) {
        //noinspection MagicConstant
        MouseWheelEvent mouseWheelEvent = new MouseWheelEvent(
                fakeComponent,
                MouseEvent.MOUSE_WHEEL,
                0,
                getModifiers(),
                x, y,
                0,
                false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL,
                (int) scrollAmount* SCROLL_MULTIPLIER,
                1
        );
        browser.sendMouseWheelEvent(mouseWheelEvent);
    }

    public void keyPressed(int key, int scancode) {
        //noinspection MagicConstant
        KeyEvent cefEvent = new KeyEvent(
                fakeComponent,
                KeyEvent.KEY_PRESSED,
                0,
                getModifiers(),
                KeycodeUtils.KEYCODE_MAP[key],
                key == GLFW_KEY_BACKSPACE ? '\b' : KeyEvent.CHAR_UNDEFINED,
                KeycodeUtils.KEY_LOCATION_MAP[key]
        );
        UnsafeFieldOverride.overrideLongField("scancode", cefEvent, fixScancode(scancode));
        // Technically macos and linux has different map, but it is only read on windows, so should be fine
        UnsafeFieldOverride.overrideLongField("rawCode", cefEvent, KeycodeUtils.KEYCODE_MAP[key]);
        browser.sendKeyEvent(cefEvent);
    }

    public void keyReleased(int key, int scancode) {
        //noinspection MagicConstant
        KeyEvent cefEvent = new KeyEvent(
                fakeComponent,
                KeyEvent.KEY_RELEASED,
                0,
                getModifiers(),
                KeycodeUtils.KEYCODE_MAP[key],
                key == GLFW_KEY_BACKSPACE ? '\b' : KeyEvent.CHAR_UNDEFINED,
                KeycodeUtils.KEY_LOCATION_MAP[key]
        );
        UnsafeFieldOverride.overrideLongField("scancode", cefEvent, fixScancode(scancode));
        // Technically macos and linux has different map, but it is only read on windows, so should be fine
        UnsafeFieldOverride.overrideLongField("rawCode", cefEvent, KeycodeUtils.KEYCODE_MAP[key]);
        browser.sendKeyEvent(cefEvent);
    }

    public void charTyped(char character) {
        //noinspection MagicConstant
        KeyEvent cefEvent = new KeyEvent(
                fakeComponent,
                KeyEvent.KEY_TYPED,
                0,
                getModifiers(),
                KeyEvent.VK_UNDEFINED,
                character
        );
        browser.sendKeyEvent(cefEvent);
    }
}
