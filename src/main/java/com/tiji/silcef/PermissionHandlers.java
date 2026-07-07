package com.tiji.silcef;

import org.cef.handler.CefPermissionRequestResult;
import org.cef.handler.CefPermissionRequestType;

import java.util.Set;
import java.util.function.Consumer;

public class PermissionHandlers {
    private PermissionHandlers() {
        throw new UnsupportedOperationException();
    }

    public static Consumer<PermissionRequest> denyAll() {
        return permissionRequest -> permissionRequest.resolve(CefPermissionRequestResult.DENY);
    }

    /// **WARNING!**
    ///
    /// This handler should not be used anywhere unless you have
    /// very specific use case. Unless you know what you are doing,
    /// don't use this handler. It is a security flaw if this is
    /// used on website that comes from internet.
    ///
    /// @return permission handler that accepts all requests
    public static Consumer<PermissionRequest> acceptAll() {
        return permissionRequest -> permissionRequest.resolve(CefPermissionRequestResult.ACCEPT);
    }

    public static Consumer<PermissionRequest> ignoreAll() {
        return permissionRequest -> permissionRequest.resolve(CefPermissionRequestResult.IGNORE);
    }

    private static final Set<CefPermissionRequestType> acceptedPermissions = Set.of(
            CefPermissionRequestType.KEYBOARD_LOCK,
            CefPermissionRequestType.POINTER_LOCK,
            CefPermissionRequestType.DISK_QUOTA,
            CefPermissionRequestType.PROTECTED_MEDIA_IDENTIFIER,
            CefPermissionRequestType.LOCAL_NETWORK,
            CefPermissionRequestType.LOCAL_NETWORK_ACCESS,
            CefPermissionRequestType.LOOPBACK_NETWORK
    );
    public static Consumer<PermissionRequest> acceptBasic(Consumer<PermissionRequest> fallback) {
        return permissionRequest -> {
            for (CefPermissionRequestType type : permissionRequest.getRequestedPermissions()) {
                if (!acceptedPermissions.contains(type)) {
                    fallback.accept(permissionRequest);
                }
            }
            permissionRequest.resolve(CefPermissionRequestResult.ACCEPT);
        };
    }

    public static Consumer<PermissionRequest> acceptBasic() {
        return acceptBasic(denyAll());
    }
}
