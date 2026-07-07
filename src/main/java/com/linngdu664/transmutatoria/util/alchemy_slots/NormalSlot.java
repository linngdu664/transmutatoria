package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.util.EssenceMetal;

public class NormalSlot extends AbstractAlchemySlot {
    public NormalSlot(EssenceMetal essenceMetal, int x, int y, boolean hideType, boolean showEssence) {
        super(essenceMetal, x, y, hideType, showEssence);
    }

    @Override
    public SlotType getType() {
        return SlotType.NORMAL;
    }

    @Override
    public TextureRenderable getRealTexture() {
        return Textures.NORMAL_SLOT;
    }
}
