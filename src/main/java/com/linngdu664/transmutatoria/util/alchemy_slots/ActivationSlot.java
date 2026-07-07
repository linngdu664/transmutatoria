package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.AlchemyReactResult;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ActivationSlot extends AbstractAlchemySlot {
    public ActivationSlot(EssenceMetal essenceMetal, int x, int y, boolean hideType, boolean showEssence) {
        super(essenceMetal, x, y, hideType, showEssence);
    }

    @Override
    protected AlchemyReactResult internalReact(ItemStack scroll, EssenceMetalItem inputEssence, List<ItemStack> outputs, boolean[] inhibitionStates, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks) {
        AlchemyReactResult result = super.internalReact(scroll, inputEssence, outputs, inhibitionStates, posToOutputSlot, deferredTasks);
        result.setEssenceStateIncrease(result.getEssenceStateIncrease() + 1);
        result.setPolarityIncrease(result.getPolarityIncrease() - 1);
        return result;
    }

    @Override
    public SlotType getType() {
        return SlotType.ACTIVATION;
    }

    @Override
    public TextureRenderable getRealTexture() {
        return Textures.ACTIVATION_SLOT;
    }
}
