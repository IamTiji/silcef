package com.tiji.silcef.internals;

import com.tiji.silcef.SlicefBrowser;
import com.tiji.silcef.TooltipStatus;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Vector;

public class DisplayHandlerImpl extends CefDisplayHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger("Slicef Browser");
    private static final HashSet<SlicefBrowser> loggedBrowsers = new HashSet<>();

    @Override
    public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
        //noinspection SuspiciousMethodCalls
        if (!loggedBrowsers.contains(browser)) return true;

        String linePostfix = line != 0 ? "@" + line : "";

        switch (level) {
            case LOGSEVERITY_VERBOSE:
                LOGGER.debug("{} ({}{})", message, source, linePostfix);
                break;

            case LOGSEVERITY_DEFAULT:
            case LOGSEVERITY_INFO:
                LOGGER.info("{} ({}{})", message, source, linePostfix);
                break;

            case LOGSEVERITY_WARNING:
                LOGGER.warn("{} ({}{})", message, source, linePostfix);
                break;

            case LOGSEVERITY_ERROR:
            case LOGSEVERITY_FATAL:
                LOGGER.error("{}{} ({}{})", level == CefSettings.LogSeverity.LOGSEVERITY_FATAL ? "(FATAL) " : "", message, source, linePostfix);
                break;
        }
        return true;
    }

    public static void logBrowser(SlicefBrowser browser) {
        loggedBrowsers.add(browser);
    }

    public static void unlogBrowser(SlicefBrowser browser) {
        loggedBrowsers.remove(browser);
    }

    @Override
    public void onTitleChange(CefBrowser browser, String title) {
        if (browser instanceof SlicefBrowser slicefBrowser) {
            slicefBrowser.currentTitle = title;
        }
    }

    @Override
    public boolean onTooltip(CefBrowser browser, String text) {
        if (browser instanceof SlicefBrowser slicefBrowser) {
            if (text.isBlank()) {
                slicefBrowser.currentTooltip = TooltipStatus.ofInvisible();
            } else {
                slicefBrowser.currentTooltip = TooltipStatus.ofVisible(text);
            }
        }
        return true;
    }

    @Override
    public void onStatusMessage(CefBrowser browser, String value) {
        if (browser instanceof SlicefBrowser slicefBrowser) {
            if (value.isBlank()) {
                slicefBrowser.statusText = null;
            } else {
                slicefBrowser.statusText = value;
            }
        }
    }

    @Override
    public void onLoadProgressChange(CefBrowser browser, double progress) {
        if (browser instanceof SlicefBrowser slicefBrowser) {
            slicefBrowser.loadProgress = progress;
        }
    }

    @Override
    public void onFaviconURLChange(CefBrowser browser, Vector<String> urls) {
        if (browser instanceof SlicefBrowser slicefBrowser) {
            slicefBrowser.faviconUrl = urls.getFirst();
        }
    }
}
