package com.tiji.silcef;

import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestBrowserScreen extends Screen {
    private final SlicefBrowser browser;
    private final SlicefWidget widget;

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
        ScreenMouseEvents.beforeMouseRelease(this).register(((screen, context) -> widget.mouseReleased(context)));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawString(font, browser.currentTitle, 20, height - 90, 0xFFFFFFFF);

        if (browser.currentTooltip.isVisible()) {
            guiGraphics.renderTooltip(font,
                    List.of(ClientTooltipComponent.create(
                            FormattedCharSequence.forward(browser.currentTooltip.getTooltipText(), Style.EMPTY))), 
                    mouseX, mouseY,
                    DefaultTooltipPositioner.INSTANCE,
                    null);
        }

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
