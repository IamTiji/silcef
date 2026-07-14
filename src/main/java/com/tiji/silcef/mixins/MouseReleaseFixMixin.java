package com.tiji.silcef.mixins;

import com.tiji.silcef.SilcefWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerEventHandler.class)
public interface MouseReleaseFixMixin {
    @Inject(method = "mouseReleased", at = @At(value = "RETURN", ordinal = 1))
    private void mouseReleaseFix(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        GuiEventListener focused = this.getFocused();
        if (focused instanceof SilcefWidget widget) {
            widget.mouseReleased(event);
        }
    }

    @Shadow GuiEventListener getFocused();
}
