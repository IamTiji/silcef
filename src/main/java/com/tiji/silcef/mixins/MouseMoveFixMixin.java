package com.tiji.silcef.mixins;

import com.tiji.silcef.SlicefWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiEventListener.class)
public interface MouseMoveFixMixin {
    @Inject(method = "mouseMoved", at = @At("HEAD"))
    private void mouseMovedFix(double mouseX, double mouseY, CallbackInfo ci) {
        if ((Object) this instanceof Screen screen) {
            for (GuiEventListener child : screen.children()) {
                if (child instanceof SlicefWidget slicefWidget) {
                    slicefWidget.mouseMoved(mouseX, mouseY);
                }
            }
        }
    }
}
