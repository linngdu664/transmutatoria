package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.item.EssenceMetal;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class InitDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ArsTransmutatoria.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<EssenceMetal>>> ESSENCES =
            DATA_COMPONENTS.registerComponentType(
                    "essences",
                    builder -> builder.persistent(EssenceMetal.LIST_CODEC).networkSynchronized(EssenceMetal.LIST_STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENTROPY =
            DATA_COMPONENTS.registerComponentType(
                    "entropy",
                    builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
            );
}
