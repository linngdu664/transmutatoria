package com.linngdu664.transmutatoria.util.alchemy_slots;

import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SlotType;

public class PurgeSlot extends AbstractAlchemySlot {
    public PurgeSlot(EssenceMetal essenceMetal, int x, int y) {
        super(essenceMetal, x, y);
    }

    @Override
    protected SlotType getType() {
        return SlotType.PURGE;
    }
}
