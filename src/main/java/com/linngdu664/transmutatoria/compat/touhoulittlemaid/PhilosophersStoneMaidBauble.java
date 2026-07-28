package com.linngdu664.transmutatoria.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.item.ItemStack;

public final class PhilosophersStoneMaidBauble implements IMaidBauble {
    private static final int DEATH_PREVENTION_COOLDOWN = 20 * 60;
    private static final int REGENERATION_DURATION = 20 * 11;
    private static final int SATURATION_INTERVAL = 20;

    @Override
    public void onTick(EntityMaid maid, ItemStack baubleItem) {
        if (maid.level().isClientSide()) {
            return;
        }

        maid.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGENERATION_DURATION, 0, true, false, true));
        if (maid.tickCount % SATURATION_INTERVAL == 0) {
            maid.addEffect(new MobEffectInstance(MobEffects.SATURATION, 1, 0, true, false, true));
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
