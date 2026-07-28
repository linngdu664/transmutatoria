package com.linngdu664.transmutatoria.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.item.ItemStack;

public final class PhilosophersStoneMaidBauble implements IMaidBauble {
    private static final int DEATH_PREVENTION_COOLDOWN = 20 * 60;
    private static final int REGENERATION_INTERVAL = 50;

    @Override
    public void onTick(EntityMaid maid, ItemStack baubleItem) {
        if (maid.level().isClientSide()) {
            return;
        }

        // Touhou Little Maid sends getActiveEffects() to Netty as a live collection.
        // Mutating that collection from the server tick can make its packet encoder
        // fail with ConcurrentModificationException, so emulate Regeneration I
        // without adding a MobEffectInstance. Saturation only affects Player anyway.
        if (maid.tickCount % REGENERATION_INTERVAL == 0) {
            maid.heal(1.0F);
        }
    }

    @Override
    public boolean onDeath(EntityMaid maid, ItemStack baubleItem, DamageSource source) {
        if (maid.level().isClientSide()) {
            return false;
        }

        long gameTime = maid.level().getGameTime();
        long readyTime = baubleItem.getOrDefault(InitDataComponents.MAID_DEATH_PREVENTION_READY_TIME.get(), 0L);
        if (gameTime < readyTime) {
            return false;
        }

        baubleItem.set(
                InitDataComponents.MAID_DEATH_PREVENTION_READY_TIME.get(),
                gameTime + DEATH_PREVENTION_COOLDOWN
        );
        maid.setHealth(1.0F);
        maid.deathTime = 0;
        maid.level().broadcastEntityEvent(maid, EntityEvent.PROTECTED_FROM_DEATH);
        return true;
    }
}
