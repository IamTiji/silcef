package com.tiji.silcef;

import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SlicefWarning {
    public record Warning(String id, Component message, Component details) {
        public static final Warning WARN_SOFTWARE_FALLBACK = new Warning(
                "slicef_software_fallback",
                Component.translatable("slicef.warnings.software_fallback"),
                Component.translatable("slicef.warnings.software_fallback.details"));
        public static final Warning WARN_UNSUPPORTED_PLATFORM = new Warning(
                "slicef_unsupported_platform",
                Component.translatable("slicef.warnings.unsupported_platform"),
                Component.translatable("slicef.warnings.unsupported_platform.details"));
        public static final Warning WARN_DEVELOPMENT = new Warning(
                "slicef_development",
                Component.translatable("slicef.warnings.development"),
                Component.translatable("slicef.warnings.development.details"));

        public static final Component PREFIX = Component.translatable("slicef.warnings.prefix");

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Warning other) {
                return id.equals(other.id);
            }
            return false;
        }
    }

    private final HashSet<Warning> warnings = new HashSet<>();

    public void addWarning(Warning warning) {
        warnings.add(warning);
    }

    public void removeWarning(Warning warning) {
        warnings.remove(warning);
    }

    public Set<Warning> allWarnings() {
        return warnings;
    }
}
