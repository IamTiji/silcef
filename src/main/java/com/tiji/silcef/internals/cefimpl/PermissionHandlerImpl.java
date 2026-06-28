package com.tiji.silcef.internals.cefimpl;

import com.tiji.silcef.PermissionRequest;
import com.tiji.silcef.SlicefBrowser;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefPermissionPromptCallback;
import org.cef.handler.CefPermissionHandlerAdapter;
import org.cef.handler.CefPermissionRequestResult;
import org.cef.handler.CefPermissionRequestType;

import java.util.*;

public class PermissionHandlerImpl extends CefPermissionHandlerAdapter {
    private static final Map<SlicefBrowser, Queue<PermissionRequest>> pendingRequests = new HashMap<>();
    private static final Map<SlicefBrowser, Boolean> isAvailable = new HashMap<>();

    @Override
    public boolean onShowPermissionPrompt(CefBrowser browser,
                                          long promptId,
                                          String requestingOrigin,
                                          EnumSet<CefPermissionRequestType> requestedPermissions,
                                          CefPermissionPromptCallback callback) {
        if (browser instanceof SlicefBrowser slicefBrowser) {
            isAvailable.putIfAbsent(slicefBrowser, true);

            CefPermissionPromptCallback wrappedCallback = cefPermissionRequestResult -> {
                isAvailable.put(slicefBrowser, true);
                pollRequests(slicefBrowser);

                callback.Continue(cefPermissionRequestResult);
            };

            PermissionRequest permissionRequest =
                new PermissionRequest(promptId, requestingOrigin, requestedPermissions, wrappedCallback);

            if (pendingRequests.containsKey(slicefBrowser)) {
                pendingRequests.get(slicefBrowser)
                    .add(permissionRequest);
            } else {
                Queue<PermissionRequest> queue = new LinkedList<>();
                queue.add(permissionRequest);
                pendingRequests.put(slicefBrowser, queue);
            }
            pollRequests(slicefBrowser);

            return true;
        }

        return false;
    }

    private static void pollRequests(SlicefBrowser slicefBrowser) {
        if (!isAvailable.get(slicefBrowser)) return;

        Queue<PermissionRequest> queue = pendingRequests.get(slicefBrowser);
        PermissionRequest req = queue.poll();
        if (req != null) {
            isAvailable.put(slicefBrowser, false);
            slicefBrowser.onPermissionRequest(req);
        } else {
            isAvailable.put(slicefBrowser, true);
        }
    }

    @Override
    public void onDismissPermissionPrompt(CefBrowser browser,
                                          long promptId,
                                          CefPermissionRequestResult result) {
        super.onDismissPermissionPrompt(browser, promptId, result);
    }
}
