package com.linngdu664.transmutatoria.util;

import net.minecraft.client.Minecraft;

public class SafeInstance {
    public static Minecraft getMC() {
        return Minecraft.getInstance();
    }
}
