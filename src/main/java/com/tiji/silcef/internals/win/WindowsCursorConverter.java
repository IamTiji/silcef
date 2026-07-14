package com.tiji.silcef.internals.win;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.tiji.silcef.Silcef;
import com.tiji.silcef.internals.CursorConverter;
import com.tiji.silcef.internals.JcefLoader;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.sun.jna.platform.win32.WinUser.*;
import static org.cef.handler.CefRenderHandler.CursorTypes.*;
import static org.lwjgl.system.windows.User32.nLoadCursor;

public class WindowsCursorConverter implements CursorConverter {
    private static final Unsafe UNSAFE;
    private static final int POINTER_SIZE;
    static {
        Class<Unsafe> clazz = Unsafe.class;
        try {
            Field field = clazz.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        POINTER_SIZE = UNSAFE.addressSize();
    }

    private static long getCursor(long cursorId, long source) {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            // better than segfault
            throw new IllegalStateException("You can't call this on non-Windows");
        }

        long cursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR); // base cursor

        /* HCURSOR */ long hcursor = nLoadCursor(source, cursorId);

        // On windows, the GLFW cursor is structured like this:
        //
        // struct _GLFWcursor
        // {
        //     _GLFWcursor*    next;
        //     HCURSOR         handle;
        // };
        //
        // Since HCURSOR is also a pointer, it is padded equally to
        // `next` field. Offset would be same as native size of
        // pointers.

        UNSAFE.putLong(cursor + POINTER_SIZE, hcursor);

        return cursor;
    }

    private static long getDLLHandle(String name) {
        HINSTANCE hinstance = Kernel32.INSTANCE.LoadLibraryEx(name, null, 0x822);
        if (hinstance == null) {
            Silcef.LOGGER.error("Failed to load {}: {}", name, Kernel32.INSTANCE.GetLastError());
            return 0L;
        }

        return Pointer.nativeValue(hinstance.getPointer());
    }

    private static final long ole32DLL = getDLLHandle("ole32.dll");

    /**
     * Fetches cursor from ole32.dll file
     */
    private static long getOle32GLFWCursor(long cursorId) {
        if (ole32DLL == 0L) {
            throw new IllegalStateException("how did you even boot");
        }
        return getCursor(cursorId, ole32DLL);
    }

    private static final long libcefDLL =
            getDLLHandle(Path.of(JcefLoader.NATIVE_PATH, "libcef.dll").toString());

    /**
     * Fetches cursor from libcef.dll file
     */
    private static long getLibcefGLFWCursor(long cursorId) {
        return getCursor(cursorId, libcefDLL);
    }

    private static long getWindowsGLFWCursor(long cursorId) {
        return getCursor(cursorId, 0L);
    }

    private static CursorType wrapGLFWCursor(String id, long cursor) {
        try {
            Class<CursorType> cursorClass = CursorType.class;
            Constructor<?> constructor = cursorClass.getDeclaredConstructor(String.class, long.class); // name, handle
            constructor.setAccessible(true);
            return (CursorType) constructor.newInstance(id, cursor); // name, handle
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
        throw new RuntimeException(e);
        }
    }

    private static final Map<Integer, CursorType> cursorMap;

    static {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            cursorMap = Map.of();
        } else {
            cursorMap = new HashMap<>();

            // Standard Cursors
            cursorMap.put(CT_POINTER, wrapGLFWCursor("CT_POINTER", getWindowsGLFWCursor(IDC_ARROW)));
            cursorMap.put(CT_CROSS,   wrapGLFWCursor("CT_CROSS",   getWindowsGLFWCursor(IDC_CROSS)));
            cursorMap.put(CT_HAND,    wrapGLFWCursor("CT_HAND",    getWindowsGLFWCursor(IDC_HAND)));
            cursorMap.put(CT_IBEAM,   wrapGLFWCursor("CT_IBEAM",   getWindowsGLFWCursor(IDC_IBEAM)));
            cursorMap.put(CT_WAIT,    wrapGLFWCursor("CT_WAIT",    getWindowsGLFWCursor(IDC_WAIT)));
            cursorMap.put(CT_HELP,    wrapGLFWCursor("CT_HELP",    getWindowsGLFWCursor(IDC_HELP)));

            // Edge Resizing
            cursorMap.put(CT_EASTRESIZE,  wrapGLFWCursor("CT_EASTRESIZE",  getWindowsGLFWCursor(IDC_SIZEWE)));
            cursorMap.put(CT_NORTHRESIZE, wrapGLFWCursor("CT_NORTHRESIZE", getWindowsGLFWCursor(IDC_SIZENS)));
            cursorMap.put(CT_WESTRESIZE,  wrapGLFWCursor("CT_WESTRESIZE",  getWindowsGLFWCursor(IDC_SIZEWE)));
            cursorMap.put(CT_SOUTHRESIZE, wrapGLFWCursor("CT_SOUTHRESIZE", getWindowsGLFWCursor(IDC_SIZENS)));

            // Corner Resizing
            cursorMap.put(CT_NORTHEASTRESIZE, wrapGLFWCursor("CT_NORTHEASTRESIZE", getWindowsGLFWCursor(IDC_SIZENESW)));
            cursorMap.put(CT_NORTHWESTRESIZE, wrapGLFWCursor("CT_NORTHWESTRESIZE", getWindowsGLFWCursor(IDC_SIZENWSE)));
            cursorMap.put(CT_SOUTHEASTRESIZE, wrapGLFWCursor("CT_SOUTHEASTRESIZE", getWindowsGLFWCursor(IDC_SIZENESW)));
            cursorMap.put(CT_SOUTHWESTRESIZE, wrapGLFWCursor("CT_SOUTHWESTRESIZE", getWindowsGLFWCursor(IDC_SIZENWSE)));

            // Bidirectional Resizing & Splitting
            cursorMap.put(CT_NORTHSOUTHRESIZE,         wrapGLFWCursor("CT_NORTHSOUTHRESIZE",         getWindowsGLFWCursor(IDC_SIZENS)));
            cursorMap.put(CT_EASTWESTRESIZE,           wrapGLFWCursor("CT_EASTWESTRESIZE",           getWindowsGLFWCursor(IDC_SIZEWE)));
            cursorMap.put(CT_NORTHEASTSOUTHWESTRESIZE, wrapGLFWCursor("CT_NORTHEASTSOUTHWESTRESIZE", getWindowsGLFWCursor(IDC_SIZENESW)));
            cursorMap.put(CT_NORTHWESTSOUTHEASTRESIZE, wrapGLFWCursor("CT_NORTHWESTSOUTHEASTRESIZE", getWindowsGLFWCursor(IDC_SIZENWSE)));

            // Panning Cursors (These don't have name)
            cursorMap.put(CT_MIDDLEPANNING,    wrapGLFWCursor("CT_MIDDLEPANNING",    getLibcefGLFWCursor (49897)));
            cursorMap.put(CT_EASTPANNING,      wrapGLFWCursor("CT_EASTPANNING",      getWindowsGLFWCursor(32658)));
            cursorMap.put(CT_NORTHPANNING,     wrapGLFWCursor("CT_NORTHPANNING",     getWindowsGLFWCursor(32655)));
            cursorMap.put(CT_NORTHEASTPANNING, wrapGLFWCursor("CT_NORTHEASTPANNING", getWindowsGLFWCursor(32660)));
            cursorMap.put(CT_NORTHWESTPANNING, wrapGLFWCursor("CT_NORTHWESTPANNING", getWindowsGLFWCursor(32659)));
            cursorMap.put(CT_SOUTHPANNING,     wrapGLFWCursor("CT_SOUTHPANNING",     getWindowsGLFWCursor(32656)));
            cursorMap.put(CT_SOUTHEASTPANNING, wrapGLFWCursor("CT_SOUTHEASTPANNING", getWindowsGLFWCursor(32662)));
            cursorMap.put(CT_SOUTHWESTPANNING, wrapGLFWCursor("CT_SOUTHWESTPANNING", getWindowsGLFWCursor(32661)));
            cursorMap.put(CT_WESTPANNING,      wrapGLFWCursor("CT_WESTPANNING",      getWindowsGLFWCursor(32657)));

            // UI State & Drag/Drop Cursors
            cursorMap.put(CT_MOVE,         wrapGLFWCursor("CT_MOVE",         getWindowsGLFWCursor(IDC_SIZEALL)));
            cursorMap.put(CT_VERTICALTEXT, wrapGLFWCursor("CT_VERTICALTEXT", getLibcefGLFWCursor (49908)));
            cursorMap.put(CT_ALIAS,        wrapGLFWCursor("CT_ALIAS",        getOle32GLFWCursor  (4)));
            cursorMap.put(CT_COPY,         wrapGLFWCursor("CT_COPY",         getOle32GLFWCursor  (3)));
            cursorMap.put(CT_PROGRESS,     wrapGLFWCursor("CT_PROGRESS",     getWindowsGLFWCursor(IDC_APPSTARTING)));
            cursorMap.put(CT_NODROP,       wrapGLFWCursor("CT_NODROP",       getWindowsGLFWCursor(IDC_NO)));
            cursorMap.put(CT_NOTALLOWED,   wrapGLFWCursor("CT_NOTALLOWED",   getWindowsGLFWCursor(IDC_NO)));

            // Cursor from Libcef
            cursorMap.put(CT_GRAB,        wrapGLFWCursor("CT_GRAB",        getLibcefGLFWCursor(49894)));
            cursorMap.put(CT_GRABBING,    wrapGLFWCursor("CT_GRABBING",    getLibcefGLFWCursor(49895)));
            cursorMap.put(CT_ZOOMIN,      wrapGLFWCursor("CT_ZOOMIN",      getLibcefGLFWCursor(49909)));
            cursorMap.put(CT_ZOOMOUT,     wrapGLFWCursor("CT_ZOOMOUT",     getLibcefGLFWCursor(49910)));
            cursorMap.put(CT_CELL,        wrapGLFWCursor("CT_CELL",        getLibcefGLFWCursor(49891)));

            cursorMap.put(CT_COLUMNRESIZE, wrapGLFWCursor("CT_COLUMNRESIZE", getLibcefGLFWCursor(49892)));
            cursorMap.put(CT_ROWRESIZE,    wrapGLFWCursor("CT_ROWRESIZE",    getLibcefGLFWCursor(49907)));
        }
    }

    @Override
    public @Nullable CursorType convert(int cursorId) {
        if (cursorId == CT_NONE) return null;
        return cursorMap.getOrDefault(cursorId, CursorType.DEFAULT);
    }
}
