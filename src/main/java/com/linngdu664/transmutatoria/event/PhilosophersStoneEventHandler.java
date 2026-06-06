package com.linngdu664.transmutatoria.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.init.InitItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID)
public class PhilosophersStoneEventHandler {
    private static final int DEATH_PREVENTION_COOLDOWN = 20 * 60;
    private static final int REGENERATION_DURATION = 20 * 11;
    private static final int SATURATION_INTERVAL = 20;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || !isHoldingPhilosophersStone(player)) {
            return;
        }

        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGENERATION_DURATION, 0, true, false, true));
        if (player.tickCount % SATURATION_INTERVAL == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 1, 0, true, false, true));
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        ItemStack stone = findAvailableStoneInHotbar(player);
        if (stone.isEmpty()) {
            return;
        }

        event.setCanceled(true);
        player.setHealth(1.0F);
        player.deathTime = 0;
        player.getCooldowns().addCooldown(stone, DEATH_PREVENTION_COOLDOWN);
    }

    private static boolean isHoldingPhilosophersStone(Player player) {
        return player.getMainHandItem().is(InitItems.PHILOSOPHERS_STONE) || player.getOffhandItem().is(InitItems.PHILOSOPHERS_STONE);
    }

    private static ItemStack findAvailableStoneInHotbar(Player player) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < Inventory.getSelectionSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(InitItems.PHILOSOPHERS_STONE) && !player.getCooldowns().isOnCooldown(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
