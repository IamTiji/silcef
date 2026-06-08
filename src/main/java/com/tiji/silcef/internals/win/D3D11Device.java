package com.tiji.silcef.internals.win;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class D3D11Device extends Unknown {
    private static final int OPEN_SHARED_RESOURCE_INDEX = 48;

    public D3D11Device(Pointer pointer) {
        super(pointer);
    }

    public int openSharedResource1(WinNT.HANDLE hResource,
                                   Guid.REFIID ReturnedInterface,
                                   PointerByReference ppResource) {
            return _invokeNativeInt(
                OPEN_SHARED_RESOURCE_INDEX,
                new Object[] {
                        getPointer(),
                        hResource,
                        ReturnedInterface,
                        ppResource
                }
        );
    }
}
