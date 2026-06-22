package com.linngdu664.transmutatoria.client.gui;

public final class ScreenPos {
    private ScreenPos() {
    }

    public static long pack(int x, int y) {
        return ((long) y << 32) | (x & 0xffffffffL);
    }

    public static int unpackX(long packed) {
        return (int) (packed & 0xffffffffL);
    }

    public static int unpackY(long packed) {
        return (int) (packed >> 32);
    }
}
