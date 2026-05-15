package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.block.entity.BlockEntityTransmutationCrucible;
import com.linngdu664.transmutatoria.block.BlockTransmutationCrucible;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

import static com.linngdu664.transmutatoria.ArsTransmutatoria.MODID;

public class InitBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredBlock<Block> TRANSMUTATION_CRUCIBLE = BLOCKS.register("transmutation_crucible", BlockTransmutationCrucible::new);
    public static final DeferredBlock<Block> ALCHEMICAL_DROSS_BLOCK = BLOCKS.registerSimpleBlock("alchemical_dross_block");

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityTransmutationCrucible>> TRANSMUTATION_CRUCIBLE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("transmutation_crucible",
                    () -> new BlockEntityType<>(BlockEntityTransmutationCrucible::new, Set.of(TRANSMUTATION_CRUCIBLE.get())));
}
