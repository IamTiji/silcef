package com.tiji.silcef.internals.utils;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NameUtils {
    public static @NotNull String getUniqueName(String type) {
        return "slicef_%s_%s".formatted(type, UUID.randomUUID());
    }
}
