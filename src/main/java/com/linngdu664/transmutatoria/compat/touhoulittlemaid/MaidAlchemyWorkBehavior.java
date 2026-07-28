package com.linngdu664.transmutatoria.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitBrains;
import com.google.common.collect.ImmutableMap;
import com.linngdu664.transmutatoria.Config;
import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;

final class MaidAlchemyWorkBehavior extends Behavior<EntityMaid> {
    private static final double WORK_DISTANCE_SQR = 9.0;
    private long nextActionTime;

    MaidAlchemyWorkBehavior() {
        super(ImmutableMap.of(InitBrains.TARGET_POS.get(), MemoryStatus.VALUE_PRESENT), 1200);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        return getTarget(maid) != null;
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long gameTime) {
        act(level, maid, gameTime);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, EntityMaid maid, long gameTime) {
        return getTarget(maid) != null;
    }

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long gameTime) {
        if (gameTime >= nextActionTime) {
            act(level, maid, gameTime);
        }
    }

    private void act(ServerLevel level, EntityMaid maid, long gameTime) {
        nextActionTime = gameTime + Config.MAID_ALCHEMY_ACTION_INTERVAL.getAsInt();
        BlockPos target = getTarget(maid);
        if (target == null) {
            return;
        }
        if (!(level.getBlockEntity(target) instanceof TransmutationCrucibleBlockEntity crucible)) {
            clearTarget(maid);
            return;
        }
        if (target.distToCenterSqr(maid.position()) > WORK_DISTANCE_SQR) {
            if (maid.canBrainMoving()) {
                BehaviorUtils.setWalkAndLookTargetMemories(maid, target, 0.6F, 2);
            }
            return;
        }
        maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);

        // 反应过程中只观察，不对锅内物品做任何修改。
        if (crucible.getTargetTimer() != 0) {
            return;
        }
        if (crucible.hasAnyOutput()) {
            MaidAlchemyInventory.collectOutputs(maid, crucible);
            maid.swing(InteractionHand.MAIN_HAND);
            if (!crucible.meetsMaidAlchemyRequirements()) {
                clearTarget(maid);
            }
            return;
        }
        if (!crucible.meetsMaidAlchemyRequirements()) {
            clearTarget(maid);
            return;
        }

        // 输入先于源质投放；投下物品后自然等待到下一个行动周期。
        if (crucible.requiresTransformationInput() && !crucible.hasInput()) {
            ItemStack input = MaidAlchemyInventory.takeInput(maid, crucible.getMaidAlchemyRequiredInput());
            if (!input.isEmpty()) {
                MaidAlchemyInventory.dropIntoCrucible(level, target, maid, input, false);
                maid.swing(InteractionHand.MAIN_HAND);
            }
            return;
        }

        EssenceMetal required = crucible.getMaidSelectedRequiredEssence();
        if (required == null) {
            return;
        }
        ItemStack current = crucible.getMaidSelectedInputEssence();
        if (current.isEmpty()) {
            putCorrectEssence(level, target, maid, required);
            return;
        }
        if (current.getItem() instanceof EssenceMetalItem metal
                && metal.getEssenceMetal() == required) {
            // 等同于玩家 Shift + 滚轮下滚：确认正确后再切换到下一槽。
            crucible.serverScrollSelectedSlot(true);
            maid.swing(InteractionHand.MAIN_HAND);
            return;
        }

        // 没有正确源质时保持锅原样；有材料时才替换错误投料。
        ItemStack correct = MaidAlchemyInventory.takeEssence(maid, required);
        if (correct.isEmpty()) {
            return;
        }
        ItemStack wrong = crucible.extractMaidSelectedInputEssence();
        if (wrong.isEmpty()) {
            MaidAlchemyInventory.giveToMaid(maid, correct);
            return;
        }
        MaidAlchemyInventory.giveToMaid(maid, wrong);
        MaidAlchemyInventory.dropIntoCrucible(level, target, maid, correct, true);
        maid.swing(InteractionHand.MAIN_HAND);
    }

    private void putCorrectEssence(
            ServerLevel level,
            BlockPos target,
            EntityMaid maid,
            EssenceMetal required
    ) {
        ItemStack essence = MaidAlchemyInventory.takeEssence(maid, required);
        if (!essence.isEmpty()) {
            // 使用掉落物进入锅，行为与玩家使用炼金术士储物盒投料一致。
            MaidAlchemyInventory.dropIntoCrucible(level, target, maid, essence, true);
            maid.swing(InteractionHand.MAIN_HAND);
        }
    }

    private BlockPos getTarget(EntityMaid maid) {
        return maid.getBrain().getMemory(InitBrains.TARGET_POS.get())
                .map(tracker -> tracker.currentBlockPosition().immutable())
                .orElse(null);
    }

    private void clearTarget(EntityMaid maid) {
        maid.getBrain().eraseMemory(InitBrains.TARGET_POS.get());
        maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        maid.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
    }
}
