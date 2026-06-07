package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.AlchemyReactResult;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SlotType;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DiffusionSlot extends AbstractAlchemySlot {
    public DiffusionSlot(EssenceMetal essenceMetal, int x, int y, boolean hideType, boolean showEssence) {
        super(essenceMetal, x, y, hideType, showEssence);
    }

    @Override
    protected AlchemyReactResult internalReact(ItemStack scroll, EssenceMetalItem inputEssence, List<ItemStack> outputs, boolean[] inhibitionStates, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks) {
        AlchemyReactResult result = super.internalReact(scroll, inputEssence, outputs, inhibitionStates, posToOutputSlot, deferredTasks);
        int triggeredCnt = 0;
        for (int i = 0; i < 6; i++) {
            int slot = posToOutputSlot.getOrDefault(getAdjacentPackedXY(i), -1);
            if (slot >= 0 && outputs.get(slot).getItem() instanceof EssenceMetalItem outEssenceMetal && !inhibitionStates[slot]) {
                outEssenceMetal.change(result.getEssenceStateIncrease());
                triggeredCnt++;
            }
        }
        result.setEssenceStateIncrease(result.getEssenceStateIncrease() * (1 + triggeredCnt));
        return result;
    }

    @Override
    public SlotType getType() {
        return SlotType.DIFFUSION;
    }

    @Override
    public TextureRenderable getRealTexture() {
        return Textures.DIFFUSION_SLOT;
    }
}
