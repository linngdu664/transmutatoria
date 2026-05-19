package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SlotType;

public class SpinSlot extends AbstractAlchemySlot {
    public SpinSlot(EssenceMetal essenceMetal, int x, int y) {
        super(essenceMetal, x, y);
    }

    @Override
    protected SlotType getType() {
        return SlotType.SPIN;
    }
}
