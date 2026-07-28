package com.tiji.silcef.internals.utils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class NameUtils {
    public static @NotNull String getUniqueName(String type) {
        return "silcef_%s_%016x".formatted(type, ThreadLocalRandom.current().nextLong());
    }
}
