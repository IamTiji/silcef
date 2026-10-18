package cc.tijiisreal.silcef.internals;

import static org.lwjgl.glfw.GLFW.GLFW_PLATFORM_WAYLAND;
import static org.lwjgl.glfw.GLFW.glfwGetPlatform;
import static org.lwjgl.opengl.GL11C.GL_VENDOR;
import static org.lwjgl.opengl.GL11C.glGetString;

// Only code in first static block was AI generated, so I rewrote it
public class Platform {
    public static final boolean isWindows;
    public static final boolean isLinux;
    public static final boolean isMacOs;

    public static final boolean isWayland;

    public static final boolean is64bit;
    public static final boolean is32bit;
    public static final boolean isArm64;

    public static boolean isGPUIntel;
    public static boolean isGPUAmd;
    public static boolean isGPUNvidia;

    static {
        String os = System.getProperty("os.name").toLowerCase();

        isWindows = os.contains("win");
        isLinux = os.contains("linux");
        isMacOs = os.contains("mac") || os.contains("darwin");

        isWayland = glfwGetPlatform() == GLFW_PLATFORM_WAYLAND;

        String arch = System.getProperty("os.arch").toLowerCase();

        isArm64 = arch.equals("aarch64") || arch.equals("arm");
        is32bit = arch.equals("x86") || arch.equals("i386");
        is64bit = arch.equals("amd64") || arch.equals("x86_64");
    }

    public static void checkGPU() {
        String gpuVendor = glGetString(GL_VENDOR);
        if (gpuVendor == null) return;
        gpuVendor = gpuVendor.toLowerCase();

        if (gpuVendor.contains("intel")) {
            isGPUIntel = true;
        } else if (gpuVendor.contains("amd")) {
            isGPUAmd = true;
        } else if (gpuVendor.contains("nvidia")) {
            isGPUNvidia = true;
        }
    }
}