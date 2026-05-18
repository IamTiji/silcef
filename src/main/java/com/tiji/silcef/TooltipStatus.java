package com.tiji.silcef;

public interface TooltipStatus {
    boolean isVisible();
    String getTooltipText();

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
