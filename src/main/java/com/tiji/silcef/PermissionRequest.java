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

@SuppressWarnings("ClassCanBeRecord") // Breaks encapsulation
public final class PermissionRequest {
    private final long promptId;
    private final URI requestingOrigin;
    private final EnumSet<CefPermissionRequestType> requestedPermissions;
    private final CefPermissionPromptCallback callback;

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

    public String getAsSingularSentence() {
        return PermissionSentenceUtils.getText(getOrigin(), requestedPermissions);
    }

    public Component getRequestHeading() {
        return Component.translatable("slicef.permission.prompt_heading", getOrigin());
    }

    public Component[] getRequestingPermissionTexts() {
        Component[] texts = new Component[requestedPermissions.size()];

        int i = 0;
        for (CefPermissionRequestType type : requestedPermissions) {
            texts[i++] = Component.translatable("slicef.permission.type.%s".formatted(type.name()));
        }

        return texts;
    }

    public String getOrigin() {
        return requestingOrigin.getHost();
    }

    public void resolve(CefPermissionRequestResult result) {
        callback.Continue(result);
    }

    public long getPromptId() {
        return promptId;
    }

    public Set<CefPermissionRequestType> getRequestedPermissions() {
        return Collections.unmodifiableSet(requestedPermissions);
    }
}
