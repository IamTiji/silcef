package com.tiji.silcef.internals;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.tiji.silcef.internals.win.WindowsCursorConverter;
import org.jetbrains.annotations.Nullable;

public interface CursorConverter {
    @Nullable CursorType convert(int cursorId);

    /**
     * Creates a new instance of the cursor converter.
     */
    static CursorConverter getInstance() {
        if (Platform.isWindows) {
            return new WindowsCursorConverter();
        } else {
            return new GLFWCursorConverter();
        }
    }
}
