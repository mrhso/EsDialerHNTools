package com.cndatacom.campus.netcore;

import java.io.UnsupportedEncodingException;

/* loaded from: assets/App_dex/_data_data_com.cndatacom.campus.cdccportalgd_.jiagu_classes.dex_2712636.dex */
public class DaMod {
    private static DaMod a;
    private long b = 0;

    private native String aid(long j);

    private native byte[] dec(long j, byte[] bArr);

    private native byte[] enc(long j, byte[] bArr);

    private native void free(long j);

    private native String[] inspect(long j, boolean z);

    private native String key(long j);

    private static native long load(byte[] bArr);

    private native String prev(long j);

    public static DaMod getInstance() {
        if (a == null) {
            a = new DaMod();
        }
        return a;
    }

    public void setData(byte[] bArr) {
        this.b = load(bArr);
    }

    public boolean isOk() {
        return this.b != 0;
    }

    public String getPrefix() {
        return this.b == 0 ? "" : prev(this.b);
    }

    public String getId() {
        return this.b == 0 ? "" : aid(this.b);
    }

    public String getKey() {
        return this.b == 0 ? "" : key(this.b);
    }

    public boolean Load(byte[] bArr) {
        Free();
        this.b = load(bArr);
        return this.b != 0;
    }

    public void Free() {
        if (this.b != 0) {
            free(this.b);
            this.b = 0L;
        }
    }

    public String Encode(String str) {
        if (this.b == 0) {
            return "";
        }
        try {
            return new String(enc(this.b, str.getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException unused) {
            return "";
        }
    }

    public byte[] Encode(byte[] bArr) {
        return this.b == 0 ? new byte[0] : enc(this.b, bArr);
    }

    public String Decode(String str) {
        if (this.b == 0) {
            return "";
        }
        try {
            return new String(dec(this.b, str.getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException unused) {
            return "";
        }
    }

    public byte[] Decode(byte[] bArr) {
        return this.b == 0 ? new byte[0] : dec(this.b, bArr);
    }

    public String[] Inspect(boolean z) {
        return this.b == 0 ? new String[]{"0", ""} : inspect(this.b, z);
    }

    protected void finalize() {
        Free();
    }

}