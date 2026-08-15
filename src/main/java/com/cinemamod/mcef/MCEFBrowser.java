package com.cinemamod.mcef;

import com.tiji.silcef.SilcefBrowser;
import com.tiji.silcef.SilcefEventHandler;
import com.tiji.silcef.internals.cefimpl.RenderHandlerImpl;
import net.minecraft.resources.Identifier;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefDevToolsClient;
import org.cef.browser.CefFrame;
import org.cef.browser.CefRequestContext;
import org.cef.callback.CefPdfPrintCallback;
import org.cef.callback.CefRunFileDialogCallback;
import org.cef.callback.CefStringVisitor;
import org.cef.handler.CefDialogHandler;
import org.cef.handler.CefRenderHandler;
import org.cef.handler.CefWindowHandler;
import org.cef.misc.CefPdfPrintSettings;
import org.cef.network.CefRequest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;

public class MCEFBrowser implements CefBrowser {
    private final SilcefBrowser parent;
    private final SilcefEventHandler eventHandler;

    public MCEFBrowser(SilcefBrowser parent) {
        this.parent = parent;
        this.eventHandler = new SilcefEventHandler(parent);
    }

    @Override
    public void createImmediately() {
        parent.createImmediately();
    }

    @Override
    public Component getUIComponent() {
        return parent.getUIComponent();
    }

    @Override
    public CefClient getClient() {
        return parent.getClient();
    }

    @Override
    public CefRenderHandler getRenderHandler() {
        return parent.getRenderHandler();
    }

    @Override
    public CefRequestContext getRequestContext() {
        return parent.getRequestContext();
    }

    @Override
    public CefWindowHandler getWindowHandler() {
        return parent.getWindowHandler();
    }

    @Override
    public boolean canGoBack() {
        return parent.canGoBack();
    }

    @Override
    public void goBack() {
        parent.goBack();
    }

    @Override
    public boolean canGoForward() {
        return parent.canGoForward();
    }

    @Override
    public void goForward() {
        parent.goForward();
    }

    @Override
    public boolean isLoading() {
        return parent.isLoading();
    }

    @Override
    public void reload() {
        parent.reload();
    }

    @Override
    public void reloadIgnoreCache() {
        parent.reloadIgnoreCache();
    }

    @Override
    public void stopLoad() {
        parent.stopLoad();
    }

    @Override
    public int getIdentifier() {
        return parent.getIdentifier();
    }

    @Override
    public CefFrame getMainFrame() {
        return parent.getMainFrame();
    }

    @Override
    public CefFrame getFocusedFrame() {
        return parent.getFocusedFrame();
    }

    @Override
    public CefFrame getFrameByIdentifier(String s) {
        return parent.getFrameByIdentifier(s);
    }

    @Override
    public CefFrame getFrameByName(String s) {
        return parent.getFrameByName(s);
    }

    @Override
    public Vector<String> getFrameIdentifiers() {
        return parent.getFrameIdentifiers();
    }

    @Override
    public Vector<String> getFrameNames() {
        return parent.getFrameNames();
    }

    @Override
    public int getFrameCount() {
        return parent.getFrameCount();
    }

    @Override
    public boolean isPopup() {
        return parent.isPopup();
    }

    @Override
    public boolean hasDocument() {
        return parent.hasDocument();
    }

    @Override
    public void viewSource() {
        parent.viewSource();
    }

    @Override
    public void getSource(CefStringVisitor cefStringVisitor) {
        parent.getSource(cefStringVisitor);
    }

    @Override
    public void getText(CefStringVisitor cefStringVisitor) {
        parent.getText(cefStringVisitor);
    }

    @Override
    public void loadRequest(CefRequest cefRequest) {
        parent.loadRequest(cefRequest);
    }

    @Override
    public void loadURL(String s) {
        parent.loadURL(s);
    }

    @Override
    public void executeJavaScript(String s, String s1, int i) {
        parent.executeJavaScript(s, s1, i);
    }

    public String getURL() {
        return parent.getURL();
    }

    @Override
    public void close(boolean b) {
        parent.close(b);
    }

    @Override
    public void setCloseAllowed() {
        parent.setCloseAllowed();
    }

    @Override
    public boolean doClose() {
        return parent.doClose();
    }

    @Override
    public void onBeforeClose() {
        parent.onBeforeClose();
    }

    @Override
    public void setFocus(boolean b) {
        parent.setFocus(b);
    }

    @Override
    public void setWindowVisibility(boolean b) {
        parent.setWindowVisibility(b);
    }

    @Override
    public double getZoomLevel() {
        return parent.getZoomLevel();
    }

    @Override
    public void setZoomLevel(double v) {
        parent.setZoomLevel(v);
    }

    @Override
    public void runFileDialog(CefDialogHandler.FileDialogMode fileDialogMode, String s, String s1, Vector<String> vector, int i, CefRunFileDialogCallback cefRunFileDialogCallback) {
        parent.runFileDialog(fileDialogMode, s, s1, vector, i, cefRunFileDialogCallback);
    }

    @Override
    public void startDownload(String s) {
        parent.startDownload(s);
    }

    @Override
    public void print() {
        parent.print();
    }

    @Override
    public void printToPDF(String s, CefPdfPrintSettings cefPdfPrintSettings, CefPdfPrintCallback cefPdfPrintCallback) {
        parent.printToPDF(s, cefPdfPrintSettings, cefPdfPrintCallback);
    }

    @Override
    public void find(String s, boolean b, boolean b1, boolean b2) {
        parent.find(s, b, b1, b2);
    }

    @Override
    public void stopFinding(boolean b) {
        parent.stopFinding(b);
    }

    @Override
    public void openDevTools() {
        parent.openDevTools();
    }

    @Override
    public void openDevTools(Point point) {
        parent.openDevTools(point);
    }

    @Override
    public void closeDevTools() {
        parent.closeDevTools();
    }

    @Override
    public CefDevToolsClient getDevToolsClient() {
        return parent.getDevToolsClient();
    }

    @Override
    public void replaceMisspelling(String s) {
        parent.replaceMisspelling(s);
    }

    @Override
    public CompletableFuture<BufferedImage> createScreenshot(boolean b) {
        return parent.createScreenshot(b);
    }

    @Override
    public void setWindowlessFrameRate(int i) {
        parent.setWindowlessFrameRate(i);
    }

    @Override
    public CompletableFuture<Integer> getWindowlessFrameRate() {
        return parent.getWindowlessFrameRate();
    }

    @Override
    public boolean isAcceleratedPaintEnabled() {
        return parent.isAcceleratedPaintEnabled();
    }

    public void resize(int width, int height) {
        parent.resize(width, height, true);
    }


    public boolean isTextureReady() {
        return true;
    }

    public Identifier getTextureIdentifier() {
        return parent.getTexture().getId();
    }


    public void sendKeyPress(int keyCode, long scanCode, int modifiers) {
        eventHandler.keyPressed(keyCode, (int) scanCode);
    }

    public void sendKeyRelease(int keyCode, long scanCode, int modifiers) {
        eventHandler.keyReleased(keyCode, (int) scanCode);
    }

    public void sendKeyTyped(char c, int modifiers) {
        eventHandler.charTyped(c);
    }

    public void sendMouseMove(int mouseX, int mouseY) {
        eventHandler.mouseMoved(mouseX, mouseY);
    }

    public void sendMousePress(int mouseX, int mouseY, int button) {
        eventHandler.mousePressed(mouseX, mouseY, button);
    }

    public void sendMouseRelease(int mouseX, int mouseY, int button) {
        eventHandler.mouseReleased(mouseX, mouseY, button);
    }

    public void sendMouseWheel(int mouseX, int mouseY, double amount, int modifiers) {
        eventHandler.mouseScrolled(mouseX, mouseY, amount);
    }


    public void close() {
        parent.setCloseAllowed();
        parent.close(true);
        ((RenderHandlerImpl) parent.getRenderHandler()).destroy();
    }
}
