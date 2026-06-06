package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.item.component.RecipeConditions;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.item.component.ExpireInfo;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class InitDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ArsTransmutatoria.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENTROPY =
            DATA_COMPONENTS.registerComponentType(
                    "entropy",
                    builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ROTATION =
            DATA_COMPONENTS.registerComponentType(
                    "rotation",
                    builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ExpireInfo>> EXPIRE_INFO =
            DATA_COMPONENTS.registerComponentType(
                    "expire_info",
                    builder -> builder.persistent(ExpireInfo.CODEC).networkSynchronized(ExpireInfo.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> NEXT_EXPIRE =
            DATA_COMPONENTS.registerComponentType(
                    "next_expire",
                    builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.LONG)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAGIC_NUMBER =
            DATA_COMPONENTS.registerComponentType(
                    "magic_number",
                    builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)     // 数可能较大，varint 可能并不能省流量
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RecipeConditions>> RECIPE_CONDITIONS =
            DATA_COMPONENTS.registerComponentType(
                    "recipe_conditions",
                    builder -> builder.persistent(RecipeConditions.CODEC).networkSynchronized(RecipeConditions.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<AbstractAlchemySlot>>> ALCHEMY_SLOTS =
            DATA_COMPONENTS.registerComponentType(
                    "alchemy_slots",
                    builder -> builder.persistent(AbstractAlchemySlot.LIST_CODEC).networkSynchronized(AbstractAlchemySlot.LIST_STREAM_CODEC)
            );
}
