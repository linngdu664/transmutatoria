package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.block.AlchemistStorageBoxBlock;
import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.block.entity.AlchemistStorageBoxBlockEntity;
import com.linngdu664.transmutatoria.block.TransmutationCrucibleBlock;
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

    public static final DeferredBlock<Block> TRANSMUTATION_CRUCIBLE = BLOCKS.register("transmutation_crucible", TransmutationCrucibleBlock::new);
    public static final DeferredBlock<Block> ALCHEMICAL_DROSS_BLOCK = BLOCKS.registerSimpleBlock("alchemical_dross_block");
    public static final DeferredBlock<AlchemistStorageBoxBlock> ALCHEMIST_STORAGE_BOX =
            BLOCKS.register("alchemist_storage_box", id -> new AlchemistStorageBoxBlock(id, 0));
    public static final DeferredBlock<AlchemistStorageBoxBlock> NIGREDO_ALCHEMIST_STORAGE_BOX =
            BLOCKS.register("nigredo_alchemist_storage_box", id -> new AlchemistStorageBoxBlock(id, -1));
    public static final DeferredBlock<AlchemistStorageBoxBlock> ALBEDO_ALCHEMIST_STORAGE_BOX =
            BLOCKS.register("albedo_alchemist_storage_box", id -> new AlchemistStorageBoxBlock(id, 1));
    public static final DeferredBlock<AlchemistStorageBoxBlock> CITRINITAS_ALCHEMIST_STORAGE_BOX =
            BLOCKS.register("citrinitas_alchemist_storage_box", id -> new AlchemistStorageBoxBlock(id, 2));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransmutationCrucibleBlockEntity>> TRANSMUTATION_CRUCIBLE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("transmutation_crucible",
                    () -> new BlockEntityType<>(TransmutationCrucibleBlockEntity::new, Set.of(TRANSMUTATION_CRUCIBLE.get())));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemistStorageBoxBlockEntity>> ALCHEMIST_STORAGE_BOX_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("alchemist_storage_box",
                    () -> new BlockEntityType<>(
                            AlchemistStorageBoxBlockEntity::new,
                            Set.of(
                                    ALCHEMIST_STORAGE_BOX.get(),
                                    NIGREDO_ALCHEMIST_STORAGE_BOX.get(),
                                    ALBEDO_ALCHEMIST_STORAGE_BOX.get(),
                                    CITRINITAS_ALCHEMIST_STORAGE_BOX.get())));
}
