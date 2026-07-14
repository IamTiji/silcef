package com.tiji.silcef;

import org.cef.callback.CefRunContextMenuCallback;

/// A callback class that Silcef will use to tell what context
/// menu is active. It is recommended to use it as anonymous
/// class, but it is really up to you.
///
/// @since 1.0
/// @author Tiji
public interface ContextMenuHandler {
    /// Called when a context menu pops up.
    ///
    /// You can dismiss it (`callback.cancel()`) or run an action
    /// (`callback.Continue(commandid, 0)`). This may be done
    /// asynchronously, so callback may be used outside this method.
    ///
    /// @param contextMenu what is inside the context menu
    /// @param callback callback when user makes a decision
    /// @since 1.0
    /// @author Tiji
    /// @see ContextMenuHandler#onDismiss
    void onContextMenu(ContextMenuItem[] contextMenu, CefRunContextMenuCallback callback);

    /// Called when a context menu is dismissed. Callback from previous
    /// [ContextMenuHandler#onContextMenu] will be invalid, and you
    /// should ignore previous context menu.
    ///
    /// @since 1.0
    /// @author Tiji
    void onDismiss();

    /// Returns handler that does nothing.
    ///
    /// @since 1.0
    /// @author Tiji
    static ContextMenuHandler noopHandler() {
        return new ContextMenuHandler() {
            @Override public void onContextMenu(ContextMenuItem[] contextMenu, CefRunContextMenuCallback callback) {}

            @Override public void onDismiss() {}
        };
    }
}
