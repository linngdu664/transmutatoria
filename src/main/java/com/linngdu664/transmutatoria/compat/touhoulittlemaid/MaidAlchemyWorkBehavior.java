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
    private static final int NEARBY_RESTOCK_RETRY_INTERVAL = 20;
    private long nextActionTime;
    private long nextNearbyRestockTime;

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
        if (crucible.requiresTransformationInput()) {
            if (crucible.hasInput()) {
                // 防御性检查：错误输入的锅不会继续消耗源质。
                if (!crucible.hasCorrectMaidAlchemyInput()) {
                    clearTarget(maid);
                    return;
                }
            } else {
                ItemStack input = takeInput(maid, crucible.getMaidAlchemyRequiredInput(), gameTime);
                if (!input.isEmpty()) {
                    MaidAlchemyInventory.dropIntoCrucible(level, target, maid, input, false);
                    maid.swing(InteractionHand.MAIN_HAND);
                }
                return;
            }
        }

        EssenceMetal required = crucible.getMaidSelectedRequiredEssence();
        if (required == null) {
            return;
        }
        ItemStack current = crucible.getMaidSelectedInputEssence();
        if (current.isEmpty()) {
            putCorrectEssence(level, target, maid, required, gameTime);
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
        ItemStack correct = takeEssence(maid, required, gameTime);
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
            EssenceMetal required,
            long gameTime
    ) {
        ItemStack essence = takeEssence(maid, required, gameTime);
        if (!essence.isEmpty()) {
            // 使用掉落物进入锅，行为与玩家使用炼金术士储物盒投料一致。
            MaidAlchemyInventory.dropIntoCrucible(level, target, maid, essence, true);
            maid.swing(InteractionHand.MAIN_HAND);
        }
    }

    private ItemStack takeInput(EntityMaid maid, ItemStack required, long gameTime) {
        boolean searchNearby = gameTime >= nextNearbyRestockTime;
        ItemStack result = MaidAlchemyInventory.takeInput(maid, required, searchNearby);
        delayNearbySearchAfterMiss(result, searchNearby, gameTime);
        return result;
    }

    private ItemStack takeEssence(EntityMaid maid, EssenceMetal required, long gameTime) {
        boolean searchNearby = gameTime >= nextNearbyRestockTime;
        ItemStack result = MaidAlchemyInventory.takeEssence(maid, required, searchNearby);
        delayNearbySearchAfterMiss(result, searchNearby, gameTime);
        return result;
    }

    private void delayNearbySearchAfterMiss(ItemStack result, boolean searchedNearby, long gameTime) {
        if (result.isEmpty() && searchedNearby) {
            nextNearbyRestockTime = gameTime + NEARBY_RESTOCK_RETRY_INTERVAL;
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
