package com.linngdu664.transmutatoria.client.renderer.state.blockentity;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

import java.util.ArrayList;

public class TransmutationCrucibleRenderState extends BlockEntityRenderState {
    public int waterAmount;
    public int waterColor;
    public final ArrayList<Pair<ItemStackRenderState, CrucibleRSlotPose>> itemAndPoses = new ArrayList<>();
}
