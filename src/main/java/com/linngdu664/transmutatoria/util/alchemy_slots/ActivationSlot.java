package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.item.ItemEssenceMetal;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.AlchemyReactResult;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SlotType;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ActivationSlot extends AbstractAlchemySlot {
    public ActivationSlot(EssenceMetal essenceMetal, int x, int y) {
        super(essenceMetal, x, y);
    }

    @Override
    protected AlchemyReactResult internalReact(ItemStack scroll, ItemEssenceMetal inputEssence, List<ItemStack> outputs, boolean[] inhibitionStates, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks, int magicNumber) {
        AlchemyReactResult result = super.internalReact(scroll, inputEssence, outputs, inhibitionStates, posToOutputSlot, deferredTasks, magicNumber);
        result.setEssenceStateIncrease(result.getEssenceStateIncrease() + 1);
        result.setPolarityIncrease(result.getPolarityIncrease() - 1);
        return result;
    }

    @Override
    protected SlotType getType() {
        return SlotType.ACTIVATION;
    }
}
