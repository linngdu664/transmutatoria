package com.linngdu664.transmutatoria.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidCheckRateTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitBrains;
import com.google.common.collect.ImmutableMap;
import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.pathfinder.Path;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class MaidAlchemyMoveBehavior extends MaidCheckRateTask {
    private final float speed;

    MaidAlchemyMoveBehavior(float speed) {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                InitBrains.TARGET_POS.get(), MemoryStatus.VALUE_ABSENT
        ));
        this.speed = speed;
        this.setMaxCheckRate(20);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        return super.checkExtraStartConditions(level, maid) && maid.canBrainMoving();
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long gameTime) {
        BlockPos target = findNearestCrucible(level, maid);
        if (target == null) {
            return;
        }
        BehaviorUtils.setWalkAndLookTargetMemories(maid, target, speed, 2);
        maid.getBrain().setMemory(InitBrains.TARGET_POS.get(), new BlockPosTracker(target));
        this.setNextCheckTickCount(5);
    }

    private BlockPos findNearestCrucible(ServerLevel level, EntityMaid maid) {
        BlockPos center = maid.getBrainSearchPos();
        int radius = Math.max(1, maid.getHomeRadius());
        int minChunkX = SectionPos.blockToSectionCoord(center.getX() - radius);
        int maxChunkX = SectionPos.blockToSectionCoord(center.getX() + radius);
        int minChunkZ = SectionPos.blockToSectionCoord(center.getZ() - radius);
        int maxChunkZ = SectionPos.blockToSectionCoord(center.getZ() + radius);
        List<Candidate> candidates = new ArrayList<>();

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof TransmutationCrucibleBlockEntity crucible)) {
                        continue;
                    }
                    BlockPos pos = crucible.getBlockPos();
                    if (!isInsideSearchArea(maid, center, pos, radius)) {
                        continue;
                    }
                    Candidate candidate = new Candidate(
                            pos.immutable(),
                            pos.distToCenterSqr(maid.position())
                    );
                    if (crucible.meetsMaidAlchemyRequirements()) {
                        candidates.add(candidate);
                    }
                }
            }
        }

        Comparator<Candidate> nearest = Comparator.comparingDouble(Candidate::distance);
        candidates.sort(nearest);
        return firstReachable(maid, candidates);
    }

    private boolean isInsideSearchArea(EntityMaid maid, BlockPos center, BlockPos pos, int radius) {
        if (!maid.isWithinHome(pos)
                || Math.abs(pos.getY() - center.getY()) > IMaidTask.VERTICAL_SEARCH_RANGE) {
            return false;
        }
        long dx = pos.getX() - center.getX();
        long dz = pos.getZ() - center.getZ();
        return dx * dx + dz * dz <= (long) radius * radius;
    }

    private BlockPos firstReachable(EntityMaid maid, List<Candidate> candidates) {
        for (Candidate candidate : candidates) {
            Path path = maid.getNavigation().createPath(candidate.pos, 2);
            if (path != null && path.canReach()) {
                return candidate.pos;
            }
        }
        return null;
    }

    private record Candidate(BlockPos pos, double distance) {
    }
}
