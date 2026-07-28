package com.linngdu664.transmutatoria.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.github.tartaricacid.touhoulittlemaid.item.bauble.BaubleManager;
import com.linngdu664.transmutatoria.init.InitItems;
import net.neoforged.neoforge.common.NeoForge;

@LittleMaidExtension
public final class TransmutatoriaMaidExtension implements ILittleMaid {
    public TransmutatoriaMaidExtension() {
        NeoForge.EVENT_BUS.register(new MaidStorageBoxPickupHandler());
    }

    @Override
    public void bindMaidBauble(BaubleManager manager) {
        manager.bind(InitItems.PHILOSOPHERS_STONE.get(), new PhilosophersStoneMaidBauble());
    }

    @Override
    public void addMaidTask(TaskManager manager) {
        manager.add(new AlchemyMaidTask());
    }
}
