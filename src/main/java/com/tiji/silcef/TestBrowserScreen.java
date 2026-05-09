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
    }

    @Override
    protected void init() {
        super.init();

        widget = new SlicefWidget(browser, 20, 20);
        super.addRenderableWidget(widget);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawString(font, browser.currentTitle, 340, 20, 0xFFFFFFFF);

        SlicefWarning warnings = browser.getWarnings();
        int y = 60;
        for (SlicefWarning.Warning warning : warnings.allWarnings()) {
            guiGraphics.drawString(font, warning.message(), 340, y, 0xFFFFFF00);
            y += 20;
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        browser.setCloseAllowed();
        browser.close(true);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        widget.mouseMoved(mouseX, mouseY); // Mouse move event isn't handled by default screen class
    }
}
