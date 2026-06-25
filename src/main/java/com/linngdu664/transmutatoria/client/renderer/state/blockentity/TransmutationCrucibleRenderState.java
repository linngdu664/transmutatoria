package com.linngdu664.transmutatoria.client.renderer.state.blockentity;

import com.linngdu664.transmutatoria.util.CrucibleRendererSlotParas;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

import java.util.ArrayList;

public class TransmutationCrucibleRenderState extends BlockEntityRenderState {
    public int waterAmount;
    public int waterColor;
    public final ArrayList<IntObjectPair<ItemStackRenderState>> items = new ArrayList<>();
    public int[] realSlotToRendererSlot;
    public CrucibleRendererSlotParas[] rendererSlotParas0;
    public CrucibleRendererSlotParas[] rendererSlotParas1;
    public float partialTicks;
}
