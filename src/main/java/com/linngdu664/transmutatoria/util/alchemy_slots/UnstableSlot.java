package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.AlchemyReactResult;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SlotType;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class UnstableSlot extends AbstractAlchemySlot {
    public UnstableSlot(EssenceMetal essenceMetal, int x, int y, boolean hideType, boolean showEssence) {
        super(essenceMetal, x, y, hideType, showEssence);
    }

    @Override
    protected AlchemyReactResult internalReact(ItemStack scroll, EssenceMetalItem inputEssence, List<ItemStack> outputs, boolean[] inhibitionStates, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks) {
        AlchemyReactResult result = super.internalReact(scroll, inputEssence, outputs, inhibitionStates, posToOutputSlot, deferredTasks);
        if (result.getEssenceStateIncrease() != 0) {
            deferredTasks.add(() -> {
                List<AbstractAlchemySlot> slotsInScroll = scroll.get(InitDataComponents.ALCHEMY_SLOTS);
                // 通常来说不可能为 null
                if (slotsInScroll != null) {
                    int thisSlot = posToOutputSlot.get(getPackedXY(x, y));
                    int targetSlot = Math.floorMod(getSlotMagicNumber(scroll.getOrDefault(InitDataComponents.MAGIC_NUMBER, 0), posToOutputSlot.get(getPackedXY())), slotsInScroll.size() - 1);
                    if (targetSlot >= thisSlot) {
                        targetSlot++;
                    }
                    // 交换槽位类型先这样写
                    AbstractAlchemySlot thisRealSlot = slotsInScroll.get(thisSlot);
                    AbstractAlchemySlot targetRealSlot = slotsInScroll.get(targetSlot);
                    Collections.swap(slotsInScroll, thisSlot, targetSlot);
                    thisRealSlot.swapPropertyExceptForType(targetRealSlot);
                    scroll.set(InitDataComponents.ALCHEMY_SLOTS, slotsInScroll);
                }
            });
        }
        return result;
    }

    @Override
    public SlotType getType() {
        return SlotType.UNSTABLE;
    }

    @Override
    public TextureRenderable getRealTexture() {
        return Textures.UNSTABLE_SLOT;
    }
}
