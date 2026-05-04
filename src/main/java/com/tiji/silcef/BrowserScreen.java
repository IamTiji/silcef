package com.tiji.silcef;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BrowserScreen extends Screen {
    private final SlicefBrowser browser;
    private SlicefWidget widget;

    public BrowserScreen(String url) {
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
