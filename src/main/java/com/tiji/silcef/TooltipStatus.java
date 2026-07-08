package com.tiji.silcef;

public interface TooltipStatus {
    /// Whether if tooltip is visible or not
    boolean isVisible();
    /// Text of the tooltip. This may contain newline;
    /// you may need to handle that properly. This is
    /// never null.
    String getTooltipText();

    /// Constructs empty tooltip that isn't visible.
    static TooltipStatus ofInvisible() {
        return new TooltipStatus() {
            @Override
            public boolean isVisible() {
                return false;
            }

            @Override
            public String getTooltipText() {
                return "";
            }
        };
    }

    /// Constructs tooltip with `tooltipText`.
    static TooltipStatus ofVisible(String tooltipText) {
        return new TooltipStatus() {
            @Override
            public boolean isVisible() {
                return true;
            }

            @Override
            public String getTooltipText() {
                return tooltipText;
            }
        };
    }
}
