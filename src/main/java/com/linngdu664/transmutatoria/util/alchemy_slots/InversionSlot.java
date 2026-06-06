package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.client.gui.Textures;
import com.linngdu664.transmutatoria.client.gui.util.TextureRenderable;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.AlchemyReactResult;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SlotType;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class InversionSlot extends AbstractAlchemySlot {
    public InversionSlot(EssenceMetal essenceMetal, int x, int y, boolean hideType, boolean showEssence) {
        super(essenceMetal, x, y, hideType, showEssence);
    }

    @Override
    protected AlchemyReactResult internalReact(ItemStack scroll, EssenceMetalItem inputEssence, List<ItemStack> outputs, boolean[] inhibitionStates, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks) {
        AlchemyReactResult result = super.internalReact(scroll, inputEssence, outputs, inhibitionStates, posToOutputSlot, deferredTasks);
        result.setEssenceStateIncrease(-result.getEssenceStateIncrease());
        result.setPolarityIncrease(-result.getPolarityIncrease());
        return result;
    }

    @Override
    public SlotType getType() {
        return SlotType.INVERSION;
    }

    @Override
    public TextureRenderable getRealTexture() {
        return Textures.INVERSION_SLOT;
    }
}
