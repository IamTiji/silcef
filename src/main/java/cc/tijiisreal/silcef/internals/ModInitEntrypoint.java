package cc.tijiisreal.silcef.internals;

import net.fabricmc.api.ModInitializer;

public class ModInitEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        if (!JcefLoader.isJcefLoaded) return;

        new SilcefInitializer().init();
    }
}
