package com.tiji.silcef;

import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public class DisplayHandlerImpl extends CefDisplayHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger("Slicef Browser");
    private static final HashSet<SlicefBrowser> loggedBrowsers = new HashSet<>();

    @Override
    public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
        //noinspection SuspiciousMethodCalls
        if (!loggedBrowsers.contains(browser)) return true;

        switch (level) {
            case LOGSEVERITY_VERBOSE:
                LOGGER.debug("{} ({}@{})", message, source, line);
                break;

            case LOGSEVERITY_DEFAULT:
            case LOGSEVERITY_INFO:
                LOGGER.info("{} ({}@{})", message, source, line);
                break;

            case LOGSEVERITY_WARNING:
                LOGGER.warn("{} ({}@{})", message, source, line);
                break;

            case LOGSEVERITY_ERROR:
            case LOGSEVERITY_FATAL:
                LOGGER.error("{}{} ({}@{})", level == CefSettings.LogSeverity.LOGSEVERITY_FATAL ? "(FATAL) " : "", message, source, line);
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
}
