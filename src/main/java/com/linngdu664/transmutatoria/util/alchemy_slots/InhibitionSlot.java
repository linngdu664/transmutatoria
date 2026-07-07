package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.AlchemyReactResult;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class InhibitionSlot extends AbstractAlchemySlot {
    public InhibitionSlot(EssenceMetal essenceMetal, int x, int y, boolean hideType, boolean showEssence) {
        super(essenceMetal, x, y, hideType, showEssence);
    }

    @Override
    protected AlchemyReactResult internalReact(ItemStack scroll, EssenceMetalItem inputEssence, List<ItemStack> outputs, boolean[] inhibitionStates, Int2IntMap posToOutputSlot, List<Runnable> deferredTasks) {
        AlchemyReactResult result = super.internalReact(scroll, inputEssence, outputs, inhibitionStates, posToOutputSlot, deferredTasks);
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
    public SlotType getType() {
        return SlotType.INHIBITION;
    }

    @Override
    public TextureRenderable getRealTexture() {
        return Textures.INHIBITION_SLOT;
    }
}
