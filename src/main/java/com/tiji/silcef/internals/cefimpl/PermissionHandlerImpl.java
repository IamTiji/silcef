package com.tiji.silcef.internals.cefimpl;

import com.tiji.silcef.PermissionRequest;
import com.tiji.silcef.SilcefBrowser;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefPermissionPromptCallback;
import org.cef.handler.CefPermissionHandlerAdapter;
import org.cef.handler.CefPermissionRequestResult;
import org.cef.handler.CefPermissionRequestType;

import java.util.*;

public class PermissionHandlerImpl extends CefPermissionHandlerAdapter {
    private static final Map<SilcefBrowser, Queue<PermissionRequest>> pendingRequests = new HashMap<>();
    private static final Map<SilcefBrowser, Boolean> isAvailable = new HashMap<>();

    @Override
    public boolean onShowPermissionPrompt(CefBrowser browser,
                                          long promptId,
                                          String requestingOrigin,
                                          EnumSet<CefPermissionRequestType> requestedPermissions,
                                          CefPermissionPromptCallback callback) {
        if (browser instanceof SilcefBrowser silcefBrowser) {
            isAvailable.putIfAbsent(silcefBrowser, true);

            CefPermissionPromptCallback wrappedCallback = cefPermissionRequestResult -> {
                isAvailable.put(silcefBrowser, true);
                pollRequests(silcefBrowser);

                callback.Continue(cefPermissionRequestResult);
            };

            PermissionRequest permissionRequest =
                new PermissionRequest(promptId, requestingOrigin, requestedPermissions, wrappedCallback);

            if (pendingRequests.containsKey(silcefBrowser)) {
                pendingRequests.get(silcefBrowser)
                    .add(permissionRequest);
            } else {
                Queue<PermissionRequest> queue = new LinkedList<>();
                queue.add(permissionRequest);
                pendingRequests.put(silcefBrowser, queue);
            }
            pollRequests(silcefBrowser);

            return true;
        }

        return false;
    }

    private static void pollRequests(SilcefBrowser silcefBrowser) {
        if (!isAvailable.get(silcefBrowser)) return;

        Queue<PermissionRequest> queue = pendingRequests.get(silcefBrowser);
        PermissionRequest req = queue.poll();
        if (req != null) {
            isAvailable.put(silcefBrowser, false);
            silcefBrowser.onPermissionRequest(req);
        } else {
            isAvailable.put(silcefBrowser, true);
        }
    }

    @Override
    public void onDismissPermissionPrompt(CefBrowser browser,
                                          long promptId,
                                          CefPermissionRequestResult result) {
        super.onDismissPermissionPrompt(browser, promptId, result);
    }
}
