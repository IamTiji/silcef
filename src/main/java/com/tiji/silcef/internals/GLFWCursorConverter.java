package com.tiji.silcef.internals;

import com.mojang.blaze3d.platform.cursor.CursorType;

import java.util.Map;

import static org.cef.handler.CefRenderHandler.CursorTypes.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Fallback implementation of the CursorConverter interface that maps GLFW cursor IDs to CEF cursor types.
 */
public class GLFWCursorConverter implements CursorConverter {
    private static final Map<Integer, CursorType> cursorMap = Map.ofEntries(
            Map.entry(CT_POINTER,            CursorType.createStandardCursor(GLFW_ARROW_CURSOR,         "CT_POINTER",          CursorType.DEFAULT)),
            Map.entry(CT_CROSS,              CursorType.createStandardCursor(GLFW_CROSSHAIR_CURSOR,     "CT_CROSS",            CursorType.DEFAULT)),
            Map.entry(CT_HAND,               CursorType.createStandardCursor(GLFW_POINTING_HAND_CURSOR, "CT_HAND",             CursorType.DEFAULT)),
            Map.entry(CT_IBEAM,              CursorType.createStandardCursor(GLFW_IBEAM_CURSOR,         "CT_IBEAM",            CursorType.DEFAULT)),
            Map.entry(CT_WAIT,               CursorType.createStandardCursor(GLFW_ARROW_CURSOR,         "CT_WAIT",             CursorType.DEFAULT)),
            Map.entry(CT_HELP,               CursorType.createStandardCursor(GLFW_ARROW_CURSOR,         "CT_HELP",             CursorType.DEFAULT)),

            // Directional Resizing
            Map.entry(CT_EASTRESIZE,         CursorType.createStandardCursor(GLFW_RESIZE_EW_CURSOR,     "CT_EASTRESIZE",       CursorType.DEFAULT)),
            Map.entry(CT_NORTHRESIZE,        CursorType.createStandardCursor(GLFW_RESIZE_NS_CURSOR,     "CT_NORTHRESIZE",      CursorType.DEFAULT)),
            Map.entry(CT_NORTHEASTRESIZE,    CursorType.createStandardCursor(GLFW_RESIZE_NESW_CURSOR,   "CT_NORTHEASTRESIZE",  CursorType.DEFAULT)),
            Map.entry(CT_NORTHWESTRESIZE,    CursorType.createStandardCursor(GLFW_RESIZE_NWSE_CURSOR,   "CT_NORTHWESTRESIZE",  CursorType.DEFAULT)),
            Map.entry(CT_SOUTHRESIZE,        CursorType.createStandardCursor(GLFW_RESIZE_NS_CURSOR,     "CT_SOUTHRESIZE",      CursorType.DEFAULT)),
            Map.entry(CT_SOUTHEASTRESIZE,    CursorType.createStandardCursor(GLFW_RESIZE_NWSE_CURSOR,   "CT_SOUTHEASTRESIZE",  CursorType.DEFAULT)),
            Map.entry(CT_SOUTHWESTRESIZE,    CursorType.createStandardCursor(GLFW_RESIZE_NESW_CURSOR,   "CT_SOUTHWESTRESIZE",  CursorType.DEFAULT)),
            Map.entry(CT_WESTRESIZE,         CursorType.createStandardCursor(GLFW_RESIZE_EW_CURSOR,     "CT_WESTRESIZE",       CursorType.DEFAULT)),

            // Double-ended Resizing
            Map.entry(CT_NORTHSOUTHRESIZE,   CursorType.createStandardCursor(GLFW_RESIZE_NS_CURSOR,     "CT_NORTHSOUTHRESIZE", CursorType.DEFAULT)),
            Map.entry(CT_EASTWESTRESIZE,     CursorType.createStandardCursor(GLFW_RESIZE_EW_CURSOR,     "CT_EASTWESTRESIZE",   CursorType.DEFAULT)),
            Map.entry(CT_COLUMNRESIZE,       CursorType.createStandardCursor(GLFW_RESIZE_EW_CURSOR,     "CT_COLUMNRESIZE",     CursorType.DEFAULT)),
            Map.entry(CT_ROWRESIZE,          CursorType.createStandardCursor(GLFW_RESIZE_NS_CURSOR,     "CT_ROWRESIZE",        CursorType.DEFAULT)),

            // Movement and Status
            Map.entry(CT_MOVE,               CursorType.createStandardCursor(GLFW_RESIZE_ALL_CURSOR,    "CT_MOVE",             CursorType.DEFAULT)),
            Map.entry(CT_NOTALLOWED,         CursorType.createStandardCursor(GLFW_NOT_ALLOWED_CURSOR,   "CT_NOTALLOWED",       CursorType.DEFAULT)),

            // Grabbing
            Map.entry(CT_GRAB,               CursorType.createStandardCursor(GLFW_POINTING_HAND_CURSOR, "CT_GRAB",             CursorType.DEFAULT)),
            Map.entry(CT_GRABBING,           CursorType.createStandardCursor(GLFW_POINTING_HAND_CURSOR, "CT_GRABBING",         CursorType.DEFAULT))
    );

    @Override
    public CursorType convert(int cursorId) {
        if (cursorId == CT_NONE) return null;

        return cursorMap.getOrDefault(cursorId, CursorType.DEFAULT);
    }
}
