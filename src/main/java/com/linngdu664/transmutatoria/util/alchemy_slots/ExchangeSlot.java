package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.AlchemyReactResult;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ExchangeSlot extends AbstractAlchemySlot {
    public ExchangeSlot(EssenceMetal essenceMetal, int x, int y, boolean hideType, boolean showEssence) {
        super(essenceMetal, x, y, hideType, showEssence);
    }

    @Override
    public boolean hasDirection() {
        return true;
    }

    @Override
    protected AlchemyReactResult internalReact(ItemStack scroll, EssenceMetalItem inputEssence, List<ItemStack> outputs, boolean[] inhibitionStates, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks) {
        AlchemyReactResult result = super.internalReact(scroll, inputEssence, outputs, inhibitionStates, posToOutputSlot, deferredTasks);
        int slot = posToOutputSlot.getOrDefault(getAdjacentPackedXY(getSlotDirection(scroll, posToOutputSlot)), -1);
        if (slot >= 0 && !outputs.get(slot).isEmpty()) {
            int thisSlot = posToOutputSlot.get(getPackedXY(x, y));
            ItemStack itemStack = outputs.get(thisSlot);
            outputs.set(thisSlot, outputs.get(slot));
            outputs.set(slot, itemStack);
        }
        return result;
    }

    @Override
    public SlotType getType() {
        return SlotType.EXCHANGE;
    }

    @Override
    public TextureRenderable getRealTexture() {
        return Textures.EXCHANGE_SLOT;
    }
}
