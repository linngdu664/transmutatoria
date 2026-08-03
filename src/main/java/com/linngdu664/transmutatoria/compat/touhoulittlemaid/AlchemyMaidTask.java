package com.linngdu664.transmutatoria.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.util.SoundUtil;
import com.google.common.collect.Lists;
import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.init.InitItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class AlchemyMaidTask implements IMaidTask {
    public static final Identifier UID = ArsTransmutatoria.makeMyIdentifier("alchemy");

    @Override
    public Identifier getUid() {
        return UID;
    }

    @Override
    public ItemStack getIcon() {
        return InitItems.TRANSMUTATION_CRUCIBLE.toStack();
    }

    @Override
    public SoundEvent getAmbientSound(EntityMaid maid) {
        return SoundUtil.environmentSound(maid, InitSounds.MAID_IDLE.get(), 0.5f);
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
        return Lists.newArrayList(
                Pair.of(5, new MaidAlchemyMoveBehavior(0.6F)),
                Pair.of(6, new MaidAlchemyWorkBehavior())
        );
    }

    @Override
    public boolean enableLookAndRandomWalk(EntityMaid maid) {
        return false;
    }

    @Override
    public String getMaidActionSummary() {
        return "Operate the nearest ready transmutation crucible containing a fully revealed alchemy scroll";
    }
}
