package cc.tijiisreal.silcef.mixins;

import cc.tijiisreal.silcef.internals.DownloadPrompt;
import cc.tijiisreal.silcef.internals.JcefLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

@Mixin(Minecraft.class)
public class StartupMixin {
    @Inject(method = "addInitialScreens", at = @At("RETURN"), cancellable = true)
    private void inject(List<Function<Runnable, Screen>> output, CallbackInfoReturnable<Boolean> cir) {
        if (!JcefLoader.isJcefLoaded) {
            output.add(DownloadPrompt::new);
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(cir.getReturnValueZ());
        }
    }
}
