package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.AlchemyReactResult;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SlotType;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ExchangeSlot extends AbstractAlchemySlot {
    public ExchangeSlot(EssenceMetal essenceMetal, int x, int y) {
        super(essenceMetal, x, y);
    }

    @Override
    protected AlchemyReactResult internalReact(ItemStack scroll, EssenceMetalItem inputEssence, List<ItemStack> outputs, boolean[] inhibitionStates, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks, int magicNumber) {
        AlchemyReactResult result = super.internalReact(scroll, inputEssence, outputs, inhibitionStates, posToOutputSlot, deferredTasks, magicNumber);
        int slot = posToOutputSlot.getOrDefault(getAdjacentPackedXY(magicNumber % 6), -1);
        if (slot >= 0 && !outputs.get(slot).isEmpty()) {
            int thisSlot = posToOutputSlot.get(getPackedXY(x, y));
            ItemStack itemStack = outputs.get(thisSlot);
            outputs.set(thisSlot, outputs.get(slot));
            outputs.set(slot, itemStack);
        }
        return result;
    }

    @Override
    protected SlotType getType() {
        return SlotType.EXCHANGE;
    }
}
