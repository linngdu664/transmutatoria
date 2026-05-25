package com.linngdu664.transmutatoria.client.gui.util;

import net.minecraft.world.phys.Vec2;

public record V2I(int x, int y) {
    public Vec2 toVec2f() {
        return new Vec2(this.x, this.y);
    }
}
