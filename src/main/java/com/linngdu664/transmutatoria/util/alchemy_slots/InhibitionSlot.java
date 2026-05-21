package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.item.ItemEssenceMetal;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.AlchemyReactResult;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SlotType;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class InhibitionSlot extends AbstractAlchemySlot {
    public InhibitionSlot(EssenceMetal essenceMetal, int x, int y) {
        super(essenceMetal, x, y);
    }

    @Override
    protected AlchemyReactResult internalReact(ItemStack scroll, ItemEssenceMetal inputEssence, List<ItemStack> outputs, boolean[] inhibitionStates, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks, int magicNumber) {
        AlchemyReactResult result = super.internalReact(scroll, inputEssence, outputs, inhibitionStates, posToOutputSlot, deferredTasks, magicNumber);
        if (!result.isTriggerDamage()) {
            for (int i = 0; i < 6; i++) {
                int slot = posToOutputSlot.getOrDefault(getAdjacentPackedXY(i), -1);
                if (slot >= 0 && outputs.get(slot).isEmpty()) {
                    inhibitionStates[slot] = true;
                }
            }
        }
        return result;
    }

    @Override
    protected SlotType getType() {
        return SlotType.INHIBITION;
    }
}
