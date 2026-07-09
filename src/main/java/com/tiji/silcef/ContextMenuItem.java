package com.tiji.silcef;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.cef.callback.CefMenuModel;

/// A class representing an entry in context menu.
///
/// @since 1.0
/// @author Tiji
public class ContextMenuItem {
    /// Text of this entry
    public final String label;
    /// Styled text of this entry; the hotkey is underlined
    public final Component styledLabel;
    /// Hotkey for this entry. If this key is pressed in context menu, this should run
    public final char hotkey;
    /// Command ID of this entry. Used when calling back what happened
    public final int commandId;
    /// If this entry is checked or not. This value can be anything with type that cannot be checked
    public boolean checked;
    /// Type of this entry
    public CefMenuModel.MenuItemType type;
    /// Submenus attached to this entry; null if none
    public final ContextMenuItem[] submenu;

    public ContextMenuItem(String label, int commandId, boolean checked,
                           CefMenuModel.MenuItemType type, ContextMenuItem[] submenu) {
        this.commandId = commandId;

        int lastIndex = 0;
        int hotkeyIndex = 0;
        while (true) {
            int index = label.substring(lastIndex).indexOf("&") + lastIndex;
            if (index != -1) {
                char chr = label.charAt(index + 1);
                if (chr != '&') {
                    this.hotkey = chr;
                    hotkeyIndex += index;
                    break;
                } else {
                    lastIndex = index + 2;
                    hotkeyIndex -= 1; // we skipped one character in &&
                }
            } else {
                this.hotkey = '\0';
                break;
            }
        }
        this.label = label.replaceAll("&(?!&)", "");

        MutableComponent text;
        if (hotkeyIndex <= 0) {
            text = Component.literal(this.label);
        } else {
            text = Component.literal(this.label.substring(0, hotkeyIndex));
            text.append(String.valueOf(hotkey)).withStyle(ChatFormatting.UNDERLINE);

            if (hotkeyIndex != this.label.length())
                text.append(this.label.substring(hotkeyIndex + 1));
        }

        this.styledLabel = text;

        this.checked = checked;
        this.type = type;
        this.submenu = submenu;
    }
}
