package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.block.BlockTransmutationCrucible;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.linngdu664.transmutatoria.ArsTransmutatoria.MODID;

public class InitBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static DeferredBlock<Block> TRANSMUTATION_CRUCIBLE = BLOCKS.register("transmutation_crucible", BlockTransmutationCrucible::new);
//    public static DeferredBlock<Block> TRANSMUTATION_CRUCIBLE = BLOCKS.registerSimpleBlock("transmutation_crucible", p -> p.mapColor(MapColor.STONE));

}
