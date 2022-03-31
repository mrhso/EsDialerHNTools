package com.cndatacom.campus.netcore;

public class DaMod {
    private native String aid(long j);

    private native byte[] dec(long j, byte[] bArr);

    private native byte[] enc(long j, byte[] bArr);

    private native void free(long j);

    private native String[] inspect(long j, boolean z);

    private native String key(long j);

    private static native long load(byte[] bArr);

    private native String prev(long j);
}