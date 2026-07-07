package com.tiji.silcef.internals;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.launch.knot.Knot;

import java.nio.file.Path;

@SuppressWarnings("unused")
public class JcefLoader implements PreLaunchEntrypoint {
    public static final String NATIVE_PATH =
            Path.of("./../jcef") // Hardcoded, but will be replaced with actual downloader
                    .toAbsolutePath()
                    .normalize()
                    .toString();

    @Override
    public void onPreLaunch() {
        Knot.getLauncher().addToClassPath(Path.of(NATIVE_PATH, "jcef.jar"));
    }
}
