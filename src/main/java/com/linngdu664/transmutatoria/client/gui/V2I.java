package com.linngdu664.transmutatoria.client.gui;

import net.minecraft.world.phys.Vec2;

public record V2I(int x, int y) {
    public Vec2 getVec2() {
        return new Vec2(this.x, this.y);
    }
}
