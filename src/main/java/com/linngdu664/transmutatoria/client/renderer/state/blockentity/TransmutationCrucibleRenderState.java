package com.linngdu664.transmutatoria.client.renderer.state.blockentity;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class TransmutationCrucibleRenderState extends BlockEntityRenderState {
    public int waterAmount;
    public int waterColor;
    public final ItemStackRenderState[] floatingItems = new ItemStackRenderState[8];
    public int floatingItemCount;
    public float gameTime;

    public TransmutationCrucibleRenderState() {
        for (int i = 0; i < 8; i++) {
            floatingItems[i] = new ItemStackRenderState();
        }
    }
}
