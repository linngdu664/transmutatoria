package com.linngdu664.transmutatoria.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PhilosophersStoneItem extends Item {
    public PhilosophersStoneItem(Identifier id) {
        super(new Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand type) {
        if (target instanceof ZombieVillager zombieVillager) {
            if (player.level() instanceof ServerLevel serverLevel) {
                zombieVillager.finishConversion(serverLevel);
            }
            return player.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }
}
