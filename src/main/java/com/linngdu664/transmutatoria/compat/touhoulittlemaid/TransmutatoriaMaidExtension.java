package com.linngdu664.transmutatoria.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.item.bauble.BaubleManager;
import com.linngdu664.transmutatoria.init.InitItems;

@LittleMaidExtension
public final class TransmutatoriaMaidExtension implements ILittleMaid {
    public TransmutatoriaMaidExtension() {
    }

    @Override
    public void bindMaidBauble(BaubleManager manager) {
        manager.bind(InitItems.PHILOSOPHERS_STONE.get(), new PhilosophersStoneMaidBauble());
    }
}
