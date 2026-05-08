package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.item.ItemTransmutationCrucible;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.linngdu664.transmutatoria.ArsTransmutatoria.MODID;

public class InitItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static DeferredItem<Item> TRANSMUTATION_CRUCIBLE = ITEMS.register("transmutation_crucible", ItemTransmutationCrucible::new);


    public static DeferredHolder<CreativeModeTab, CreativeModeTab> TRANSMUTATORIA_TAB = TABS.register(MODID, () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.transmutatoria"))
            .icon(() -> new ItemStack(InitBlocks.TRANSMUTATION_CRUCIBLE.get())).displayItems((parameters, output) -> {
                        output.accept(new ItemStack(TRANSMUTATION_CRUCIBLE.get()));
                    }
            ).build());

}
