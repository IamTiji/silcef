package com.tiji.silcef;

import com.tiji.silcef.internals.utils.PermissionSentenceUtils;
import net.minecraft.network.chat.Component;
import org.cef.callback.CefPermissionPromptCallback;
import org.cef.handler.CefPermissionRequestResult;
import org.cef.handler.CefPermissionRequestType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/// A class representing permission request from CEF
///
/// @since 1.0
/// @author Tiji
@SuppressWarnings("ClassCanBeRecord") // Breaks encapsulation
public final class PermissionRequest {
    private final long promptId;
    private final URI requestingOrigin;
    private final EnumSet<CefPermissionRequestType> requestedPermissions;
    private final CefPermissionPromptCallback callback;

    /// Constructs permission request
    ///
    /// @param promptId unique ID for this prompt
    /// @param requestingOrigin where this permission was requested from
    /// @param requestedPermissions permissions requested by `requestingOrigin`
    /// @param callback where to callback prompt result
    ///
    /// @since 1.0
    /// @author Tiji
    public PermissionRequest(long promptId, String requestingOrigin,
                             EnumSet<CefPermissionRequestType> requestedPermissions,
                             CefPermissionPromptCallback callback) {
        this.promptId = promptId;
        this.requestedPermissions = requestedPermissions;
        this.callback = callback;

        try {
            this.requestingOrigin = new URI(requestingOrigin);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /// Constructs this permission request as human-readable sentence,
    /// localized to Minecraft locale setting.
    ///
    /// For example, a prompt from Google requesting location in English
    /// locale will return:
    /// ```plaintext
    /// google.com wants to know your location
    /// ```
    ///
    /// @return human-readable sentence describing this request
    /// @since 1.0
    /// @author Tiji
    public String getAsSingularSentence() {
        return PermissionSentenceUtils.getText(getOrigin(), requestedPermissions);
    }

    /// Returns a heading for this request's prompt. This may be used
    /// in user prompt asking for permission
    ///
    /// @return heading for this request's prompt
    /// @since 1.0
    /// @author Tiji
    public Component getRequestHeading() {
        return Component.translatable("slicef.permission.prompt_heading", getOrigin());
    }

    /// Returns array of text, representing each permission type for this
    /// request's prompt. This may be used in user prompt asking for
    /// permission.
    ///
    /// @return array of text representing requesting permission
    /// @since 1.0
    /// @author Tiji
    public Component[] getRequestingPermissionTexts() {
        Component[] texts = new Component[requestedPermissions.size()];

        int i = 0;
        for (CefPermissionRequestType type : requestedPermissions) {
            texts[i++] = Component.translatable("slicef.permission.type.%s".formatted(type.name()));
        }

        return texts;
    }

    /// Returns where this request came from.
    ///
    /// @return origin of this request
    /// @since 1.0
    /// @author Tiji
    public String getOrigin() {
        return requestingOrigin.getHost();
    }

    /// Resolve this request with some state.
    ///
    /// | State | Meaning |
    /// | ---: | :--- |
    /// | `ACCEPT`  | This request is fulfilled and requesting website may use features relating to this permission |
    /// | `DENY`    | This request is not accepted and requesting website cannot use features it requested |
    /// | `DISMISS` | User has explicitly dismissed this request |
    /// | `IGNORE`  | The app (or mod) ignored this request, but it is not done by user |
    ///
    /// @since 1.0
    /// @author Tiji
    public void resolve(CefPermissionRequestResult result) {
        callback.Continue(result);
    }

    /// Returns ID of this request. This ID is unique to this request only.
    ///
    /// @return ID of this request
    /// @since 1.0
    /// @author Tiji
    public long getPromptId() {
        return promptId;
    }

    /// Returns set of requested permissions.
    ///
    /// @return set of requested permissions
    /// @since 1.0
    /// @author Tiji
    public Set<CefPermissionRequestType> getRequestedPermissions() {
        return Collections.unmodifiableSet(requestedPermissions);
    }
}
