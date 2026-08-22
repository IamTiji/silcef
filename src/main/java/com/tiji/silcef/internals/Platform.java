package com.tiji.silcef.internals;

// This bit of code is written by AI because I am too lazy
public class Platform {
    public static final boolean isWindows;
    public static final boolean isLinux;
    public static final boolean isMacOs;

    public static final boolean isWayland;

    public static final boolean is64bit;
    public static final boolean is32bit;
    public static final boolean isArm64;

    static {
        String osName = System.getProperty("os.name", "").toLowerCase();
        isWindows = osName.contains("win");
        isLinux = osName.contains("linux");
        isMacOs = osName.contains("mac") || osName.contains("darwin");

        // Wayland detection only makes sense on Linux; env vars are the
        // conventional signal a session is running under Wayland.
        if (isLinux) {
            String sessionType = System.getenv("XDG_SESSION_TYPE");
            String waylandDisplay = System.getenv("WAYLAND_DISPLAY");
            isWayland = (sessionType != null && sessionType.equalsIgnoreCase("wayland"))
                || (waylandDisplay != null && !waylandDisplay.isEmpty());
        } else {
            isWayland = false;
        }

        String arch = System.getProperty("os.arch", "").toLowerCase();
        isArm64 = arch.equals("aarch64") || arch.equals("arm64");

        is64bit = arch.contains("64");
        is32bit = !is64bit;
    }
}