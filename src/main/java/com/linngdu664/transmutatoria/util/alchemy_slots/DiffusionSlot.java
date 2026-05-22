package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.AlchemyReactResult;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SlotType;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DiffusionSlot extends AbstractAlchemySlot {
    public DiffusionSlot(EssenceMetal essenceMetal, int x, int y) {
        super(essenceMetal, x, y);
    }

    @Override
    protected AlchemyReactResult internalReact(ItemStack scroll, EssenceMetalItem inputEssence, List<ItemStack> outputs, boolean[] inhibitionStates, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks, int magicNumber) {
        AlchemyReactResult result = super.internalReact(scroll, inputEssence, outputs, inhibitionStates, posToOutputSlot, deferredTasks, magicNumber);
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
    protected SlotType getType() {
        return SlotType.DIFFUSION;
    }
}
