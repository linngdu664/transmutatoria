package com.linngdu664.transmutatoria.client.renderer.state.blockentity;

import com.linngdu664.transmutatoria.client.model.AlchemistStorageBoxModel;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public class AlchemistStorageBoxRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public float openness;
    public Identifier texture = AlchemistStorageBoxModel.TEXTURE_LOCATION;
}
