package com.tiji.silcef.mixins;

import com.tiji.silcef.Slicef;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class RedrawCallbackMixin {
    //@Inject(method = "runTick", at = @At("HEAD"))
    //public void callback(boolean renderLevel, CallbackInfo ci) {
    //    Slicef.doLoopwork();
    //}
}
