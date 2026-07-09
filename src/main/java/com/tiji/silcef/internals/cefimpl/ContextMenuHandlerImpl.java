package com.tiji.silcef.internals.cefimpl;

import com.tiji.silcef.ContextMenuItem;
import com.tiji.silcef.SlicefBrowser;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.callback.CefRunContextMenuCallback;
import org.cef.handler.CefContextMenuHandlerAdapter;

// TODO: Implement way to add custom element? Is passing control over enough?
public class ContextMenuHandlerImpl extends CefContextMenuHandlerAdapter {
    private static ContextMenuItem[] wrapModel(CefMenuModel model) {
        ContextMenuItem[] result = new ContextMenuItem[model.getCount()];
        for (int i = 0; i < result.length; i++) {
            result[i] = wrapItem(model, i);
        }
        return result;
    }

    private static ContextMenuItem wrapItem(CefMenuModel model, int i) {
        CefMenuModel.MenuItemType type = model.getTypeAt(i);

        ContextMenuItem[] submenu = null;
        if (type == CefMenuModel.MenuItemType.MENUITEMTYPE_SUBMENU) {
            submenu = wrapModel(model.getSubMenuAt(i));
        }

        return new ContextMenuItem(
                model.getLabelAt(i),
                model.getCommandIdAt(i),
                model.isCheckedAt(i),
                type,
                submenu
        );
    }

    // TODO: Pass context of the context menu?
    @Override
    public boolean runContextMenu(CefBrowser browser, CefFrame frame, CefContextMenuParams params, CefMenuModel model, CefRunContextMenuCallback callback) {
        if (browser instanceof SlicefBrowser slicefBrowser) {
            slicefBrowser.onContextMenu(wrapModel(model), callback);
            return true;
        }

        return false;
    }

    @Override
    public void onContextMenuDismissed(CefBrowser browser, CefFrame frame) {
        if (browser instanceof SlicefBrowser slicefBrowser) {
            slicefBrowser.onDismiss();
        }
    }
}
