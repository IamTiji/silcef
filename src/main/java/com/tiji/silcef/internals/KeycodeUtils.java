package com.tiji.silcef.internals;

import java.awt.event.KeyEvent;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class KeycodeUtils {
    public static final int[] KEYCODE_MAP = new int[348];
    public static final int[] KEY_LOCATION_MAP = new int[348];

    static {
        Arrays.fill(KEYCODE_MAP, KeyEvent.VK_UNDEFINED);

        for (char i = 'A'; i <= 'Z'; i++) KEYCODE_MAP[i] = i;
        for (char i = '0'; i <= '9'; i++) KEYCODE_MAP[i] = i;

        // Navigation & Action keys
        KEYCODE_MAP[GLFW_KEY_ESCAPE]        = KeyEvent.VK_ESCAPE;
        KEYCODE_MAP[GLFW_KEY_ENTER]         = KeyEvent.VK_ENTER;
        KEYCODE_MAP[GLFW_KEY_TAB]           = KeyEvent.VK_TAB;
        KEYCODE_MAP[GLFW_KEY_BACKSPACE]     = KeyEvent.VK_BACK_SPACE;
        KEYCODE_MAP[GLFW_KEY_INSERT]        = KeyEvent.VK_INSERT;
        KEYCODE_MAP[GLFW_KEY_DELETE]        = KeyEvent.VK_DELETE;
        KEYCODE_MAP[GLFW_KEY_RIGHT]         = KeyEvent.VK_RIGHT;
        KEYCODE_MAP[GLFW_KEY_LEFT]          = KeyEvent.VK_LEFT;
        KEYCODE_MAP[GLFW_KEY_DOWN]          = KeyEvent.VK_DOWN;
        KEYCODE_MAP[GLFW_KEY_UP]            = KeyEvent.VK_UP;
        KEYCODE_MAP[GLFW_KEY_PAGE_UP]       = KeyEvent.VK_PAGE_UP;
        KEYCODE_MAP[GLFW_KEY_PAGE_DOWN]     = KeyEvent.VK_PAGE_DOWN;
        KEYCODE_MAP[GLFW_KEY_HOME]          = KeyEvent.VK_HOME;
        KEYCODE_MAP[GLFW_KEY_END]           = KeyEvent.VK_END;
        KEYCODE_MAP[GLFW_KEY_CAPS_LOCK]     = KeyEvent.VK_CAPS_LOCK;
        KEYCODE_MAP[GLFW_KEY_SCROLL_LOCK]   = KeyEvent.VK_SCROLL_LOCK;
        KEYCODE_MAP[GLFW_KEY_NUM_LOCK]      = KeyEvent.VK_NUM_LOCK;
        KEYCODE_MAP[GLFW_KEY_PRINT_SCREEN]  = KeyEvent.VK_PRINTSCREEN;
        KEYCODE_MAP[GLFW_KEY_PAUSE]         = KeyEvent.VK_PAUSE;
        KEYCODE_MAP[GLFW_KEY_SPACE]         = KeyEvent.VK_SPACE;

        // Modifiers
        KEYCODE_MAP[GLFW_KEY_LEFT_SHIFT]    = KeyEvent.VK_SHIFT;
        KEYCODE_MAP[GLFW_KEY_RIGHT_SHIFT]   = KeyEvent.VK_SHIFT;
        KEYCODE_MAP[GLFW_KEY_LEFT_CONTROL]  = KeyEvent.VK_CONTROL;
        KEYCODE_MAP[GLFW_KEY_RIGHT_CONTROL] = KeyEvent.VK_CONTROL;
        KEYCODE_MAP[GLFW_KEY_LEFT_ALT]      = KeyEvent.VK_ALT;
        KEYCODE_MAP[GLFW_KEY_RIGHT_ALT]     = KeyEvent.VK_ALT;
        KEYCODE_MAP[GLFW_KEY_LEFT_SUPER]    = KeyEvent.VK_META;
        KEYCODE_MAP[GLFW_KEY_RIGHT_SUPER]   = KeyEvent.VK_META;

        // Punctuation & Symbols
        KEYCODE_MAP[GLFW_KEY_APOSTROPHE]    = KeyEvent.VK_QUOTE;
        KEYCODE_MAP[GLFW_KEY_COMMA]         = KeyEvent.VK_COMMA;
        KEYCODE_MAP[GLFW_KEY_MINUS]         = KeyEvent.VK_MINUS;
        KEYCODE_MAP[GLFW_KEY_PERIOD]        = KeyEvent.VK_PERIOD;
        KEYCODE_MAP[GLFW_KEY_SLASH]         = KeyEvent.VK_SLASH;
        KEYCODE_MAP[GLFW_KEY_SEMICOLON]     = KeyEvent.VK_SEMICOLON;
        KEYCODE_MAP[GLFW_KEY_EQUAL]         = KeyEvent.VK_EQUALS;
        KEYCODE_MAP[GLFW_KEY_LEFT_BRACKET]  = KeyEvent.VK_OPEN_BRACKET;
        KEYCODE_MAP[GLFW_KEY_BACKSLASH]     = KeyEvent.VK_BACK_SLASH;
        KEYCODE_MAP[GLFW_KEY_RIGHT_BRACKET] = KeyEvent.VK_CLOSE_BRACKET;
        KEYCODE_MAP[GLFW_KEY_GRAVE_ACCENT]  = KeyEvent.VK_BACK_QUOTE;

        // Function keys
        KEYCODE_MAP[GLFW_KEY_F1]            = KeyEvent.VK_F1;
        KEYCODE_MAP[GLFW_KEY_F2]            = KeyEvent.VK_F2;
        KEYCODE_MAP[GLFW_KEY_F3]            = KeyEvent.VK_F3;
        KEYCODE_MAP[GLFW_KEY_F4]            = KeyEvent.VK_F4;
        KEYCODE_MAP[GLFW_KEY_F5]            = KeyEvent.VK_F5;
        KEYCODE_MAP[GLFW_KEY_F6]            = KeyEvent.VK_F6;
        KEYCODE_MAP[GLFW_KEY_F7]            = KeyEvent.VK_F7;
        KEYCODE_MAP[GLFW_KEY_F8]            = KeyEvent.VK_F8;
        KEYCODE_MAP[GLFW_KEY_F9]            = KeyEvent.VK_F9;
        KEYCODE_MAP[GLFW_KEY_F10]           = KeyEvent.VK_F10;
        KEYCODE_MAP[GLFW_KEY_F11]           = KeyEvent.VK_F11;
        KEYCODE_MAP[GLFW_KEY_F12]           = KeyEvent.VK_F12;

        // Keypad numbers
        KEYCODE_MAP[GLFW_KEY_KP_0]          = KeyEvent.VK_NUMPAD0;
        KEYCODE_MAP[GLFW_KEY_KP_1]          = KeyEvent.VK_NUMPAD1;
        KEYCODE_MAP[GLFW_KEY_KP_2]          = KeyEvent.VK_NUMPAD2;
        KEYCODE_MAP[GLFW_KEY_KP_3]          = KeyEvent.VK_NUMPAD3;
        KEYCODE_MAP[GLFW_KEY_KP_4]          = KeyEvent.VK_NUMPAD4;
        KEYCODE_MAP[GLFW_KEY_KP_5]          = KeyEvent.VK_NUMPAD5;
        KEYCODE_MAP[GLFW_KEY_KP_6]          = KeyEvent.VK_NUMPAD6;
        KEYCODE_MAP[GLFW_KEY_KP_7]          = KeyEvent.VK_NUMPAD7;
        KEYCODE_MAP[GLFW_KEY_KP_8]          = KeyEvent.VK_NUMPAD8;
        KEYCODE_MAP[GLFW_KEY_KP_9]          = KeyEvent.VK_NUMPAD9;

        // Keypad actions
        KEYCODE_MAP[GLFW_KEY_KP_DIVIDE]     = KeyEvent.VK_DIVIDE;
        KEYCODE_MAP[GLFW_KEY_KP_MULTIPLY]   = KeyEvent.VK_MULTIPLY;
        KEYCODE_MAP[GLFW_KEY_KP_SUBTRACT]   = KeyEvent.VK_SUBTRACT;
        KEYCODE_MAP[GLFW_KEY_KP_ADD]        = KeyEvent.VK_ADD;
        KEYCODE_MAP[GLFW_KEY_KP_DECIMAL]    = KeyEvent.VK_DECIMAL;
        KEYCODE_MAP[GLFW_KEY_KP_ENTER]      = KeyEvent.VK_ENTER;
    }

    static {
        Arrays.fill(KEY_LOCATION_MAP, KeyEvent.KEY_LOCATION_STANDARD);

        // Modifiers
        KEY_LOCATION_MAP[GLFW_KEY_LEFT_SHIFT]    = KeyEvent.KEY_LOCATION_LEFT;
        KEY_LOCATION_MAP[GLFW_KEY_RIGHT_SHIFT]   = KeyEvent.KEY_LOCATION_RIGHT;
        KEY_LOCATION_MAP[GLFW_KEY_LEFT_CONTROL]  = KeyEvent.KEY_LOCATION_LEFT;
        KEY_LOCATION_MAP[GLFW_KEY_RIGHT_CONTROL] = KeyEvent.KEY_LOCATION_RIGHT;
        KEY_LOCATION_MAP[GLFW_KEY_LEFT_ALT]      = KeyEvent.KEY_LOCATION_LEFT;
        KEY_LOCATION_MAP[GLFW_KEY_RIGHT_ALT]     = KeyEvent.KEY_LOCATION_RIGHT;
        KEY_LOCATION_MAP[GLFW_KEY_LEFT_SUPER]    = KeyEvent.KEY_LOCATION_LEFT;
        KEY_LOCATION_MAP[GLFW_KEY_RIGHT_SUPER]   = KeyEvent.KEY_LOCATION_RIGHT;

        // Keypad numbers
        KEY_LOCATION_MAP[GLFW_KEY_KP_0]          = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_1]          = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_2]          = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_3]          = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_4]          = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_5]          = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_6]          = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_7]          = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_8]          = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_9]          = KeyEvent.KEY_LOCATION_NUMPAD;

        // Keypad actions
        KEY_LOCATION_MAP[GLFW_KEY_KP_DIVIDE]     = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_MULTIPLY]   = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_SUBTRACT]   = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_ADD]        = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_DECIMAL]    = KeyEvent.KEY_LOCATION_NUMPAD;
        KEY_LOCATION_MAP[GLFW_KEY_KP_ENTER]      = KeyEvent.KEY_LOCATION_NUMPAD;
    }
}
