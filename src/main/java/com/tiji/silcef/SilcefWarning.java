package com.tiji.silcef;

import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/// Container class that contains warnings that browser may raise.
///
/// @since 1.0
/// @author Tiji
public class SilcefWarning {
    /// Record class, representing singular type of warning.
    ///
    /// @since 1.0
    /// @author Tiji
    public record Warning(String id, Component message, Component details) {
        public static final Warning WARN_SOFTWARE_FALLBACK = new Warning(
                "silcef_software_fallback",
                Component.translatable("silcef.warnings.software_fallback"),
                Component.translatable("silcef.warnings.software_fallback.details"));
        public static final Warning WARN_UNSUPPORTED_PLATFORM = new Warning(
                "silcef_unsupported_platform",
                Component.translatable("silcef.warnings.unsupported_platform"),
                Component.translatable("silcef.warnings.unsupported_platform.details"));
        public static final Warning WARN_DEVELOPMENT = new Warning(
                "silcef_development",
                Component.translatable("silcef.warnings.development"),
                Component.translatable("silcef.warnings.development.details"));
        public static final Warning WARN_FALLBACK_LANGUAGE = new Warning(
                "silcef_fallback_language",
                Component.translatable("silcef.warnings.unsupported_language"),
                Component.translatable("silcef.warnings.unsupported_language.details"));
        public static final Warning WARN_ACCELERATED_PAINT_NO_SUPPORT = new Warning(
                "silcef_accelerated_paint_no_support",
                Component.translatable("silcef.warnings.accelerated_paint_no_support"),
                Component.translatable("silcef.warnings.accelerated_paint_no_support.details"));

        /// Simple "Warning" text, that is localized to user's language.
        /// You may use this as prefix to warnings when displaying these
        /// to user.
        public static final Component PREFIX = Component.translatable("silcef.warnings.prefix");

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

    public SilcefWarning() {
        addConditionalWarning(SilcefWarning.Warning.WARN_DEVELOPMENT                   , Silcef.INDEV                       );
        addConditionalWarning(SilcefWarning.Warning.WARN_FALLBACK_LANGUAGE             , Silcef.isFallbackLang              );
        addConditionalWarning(SilcefWarning.Warning.WARN_ACCELERATED_PAINT_NO_SUPPORT  , !Silcef.isAcceleratedPaintAllowed  );
    }

    /// Adds warning to warnings list. You may add warnings that
    /// already existed, but that won't change state of anything.
    ///
    /// @since 1.0
    /// @author Tiji
    public void addWarning(Warning warning) {
        warnings.add(warning);
    }

    /// Conditionally adds warning to warnings list, when `when`
    /// is true. You may add warnings that already existed, but
    /// that won't change state of anything.
    ///
    /// @since 1.0
    /// @author Tiji
    public void addConditionalWarning(Warning warning, boolean when) {
        if (when) {
            addWarning(warning);
        }
    }

    /// Remove warning from warnings list. You may remove warning
    /// that didn't exist, but that won't change state of anything.
    ///
    /// @since 1.0
    /// @author Tiji
    public void removeWarning(Warning warning) {
        warnings.remove(warning);
    }

    /// Returns all warnings that were raised.
    ///
    /// @since 1.0
    /// @author Tiji
    public Set<Warning> allWarnings() {
        return Collections.unmodifiableSet(warnings);
    }
}
