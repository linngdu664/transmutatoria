package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SlotType;

public class UnstableSlot extends AbstractAlchemySlot {
    public UnstableSlot(EssenceMetal essenceMetal, int x, int y) {
        super(essenceMetal, x, y);
    }

    @Override
    protected SlotType getType() {
        return SlotType.UNSTABLE;
    }
}
