package com.tiji.silcef.internals.win;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface D3D11 extends StdCallLibrary {
    D3D11[] INSTANCE = new D3D11[1];

    static void initialize() {
        INSTANCE[0] = Native.load("d3d11", D3D11.class);
    }

    static D3D11 get() {
        return INSTANCE[0];
    }

    int D3D11_SDK_VERSION = 7;
    int D3D_DRIVER_TYPE_HARDWARE = 1;

    int D3D11CreateDevice(
            Pointer pAdapter,
            int driverType,
            Pointer software,
            int flags,
            Pointer pFeatureLevels,
            int featureLevels,
            int sdkVersion,
            PointerByReference ppDevice,
            Pointer pFeatureLevel,
            PointerByReference ppImmediateContext
    );
}
