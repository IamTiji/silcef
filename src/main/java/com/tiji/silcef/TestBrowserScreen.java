package com.tiji.silcef;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TestBrowserScreen extends Screen {
    private final SlicefBrowser browser;
    private SlicefWidget widget;

    public TestBrowserScreen(String url) {
        super(Component.empty());

        browser = Slicef.getBrowser(url, 300, 300);
        widget = new SlicefWidget(browser, 0, 0);
    }

    @Override
    protected void init() {
        super.init();

        widget.resize(width, height - 100);

        super.addRenderableWidget(widget);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawString(font, browser.currentTitle, 20, height - 90, 0xFFFFFFFF);

        SlicefWarning warnings = browser.getWarnings();
        int y = height - 70;
        for (SlicefWarning.Warning warning : warnings.allWarnings()) {
            guiGraphics.drawString(font, warning.message(), 20, y, 0xFFFFFF00);
            y += font.lineHeight + 2;
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        browser.setCloseAllowed();
        browser.close(true);
    }
}
