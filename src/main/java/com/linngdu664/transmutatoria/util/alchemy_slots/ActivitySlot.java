package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.AlchemyReactResult;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SlotType;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ActivitySlot extends AbstractAlchemySlot {
    public ActivitySlot(EssenceMetal essenceMetal, int x, int y) {
        super(essenceMetal, x, y);
    }

    @Override
    protected AlchemyReactResult internalReact(ItemStack scroll, EssenceMetalItem inputEssence, List<ItemStack> outputs, boolean[] inhibitionStates, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks, int magicNumber) {
        AlchemyReactResult result = super.internalReact(scroll, inputEssence, outputs, inhibitionStates, posToOutputSlot, deferredTasks, magicNumber);
        // 如果基础反应已经湮灭则不触发高级反应
        if (!result.isClearItemStack()) {
            int slot = posToOutputSlot.getOrDefault(getAdjacentPackedXY(magicNumber % 6), -1);
            if (slot >= 0 && outputs.get(slot).getItem() instanceof EssenceMetalItem outEssenceMetal) {
                EssenceMetal.Relation relation = inputEssence.getRelation(outEssenceMetal.getEssenceMetal());
                if (relation == EssenceMetal.Relation.SAME) {
                    // 湮灭
                    result.setClearItemStack(true);
                    outputs.set(slot, ItemStack.EMPTY);
                } else {
                    result.setEssenceStateIncrease(result.getEssenceStateIncrease() + relation.self);
                    if (!inhibitionStates[slot]) {
                        outputs.set(slot, outEssenceMetal.change(relation.other));
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected SlotType getType() {
        return SlotType.ACTIVITY;
    }
}
