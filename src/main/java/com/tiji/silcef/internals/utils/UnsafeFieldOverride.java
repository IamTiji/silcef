package com.tiji.silcef.internals.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;

// Only if JCEF didn't depend on internal fields...
@SuppressWarnings("removal")
public class UnsafeFieldOverride {
    private static final Unsafe unsafe;
    private static final HashMap<String, Long> fieldOffset = new HashMap<>();

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void overrideLongField(String fieldName, Object target, long value) {
        if (!fieldOffset.containsKey(fieldName)) {
            try {
                Field field = target.getClass().getDeclaredField(fieldName);
                fieldOffset.put(fieldName, unsafe.objectFieldOffset(field));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        long offset = fieldOffset.get(fieldName);
        if (offset != -1) {
            unsafe.putLong(target, offset, value);
        } else {
            throw new RuntimeException("Field not found: " + fieldName);
        }
    }
}
