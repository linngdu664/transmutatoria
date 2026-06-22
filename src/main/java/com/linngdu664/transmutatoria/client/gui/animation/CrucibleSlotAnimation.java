package com.linngdu664.transmutatoria.client.gui.animation;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.tool.Easing;
import net.minecraft.client.DeltaTracker;
import net.minecraft.util.Mth;

public final class CrucibleSlotAnimation {
    static final float SLOT_REVEAL_TICKS = 8.0f;

    private long blockPos;
    private boolean initialized;
    private boolean wasRunning;
    private int targetTimer;
    private int lastSyncedProcessTimer;
    private float processTicks;
    private float revealTicks = SLOT_REVEAL_TICKS;

    public void update(TransmutationCrucibleBlockEntity crucible, DeltaTracker delta) {
        long currentBlockPos = crucible.getBlockPos().asLong();
        int syncedTargetTimer = crucible.getTargetTimer();
        int syncedProcessTimer = crucible.getProcessTimer();
        boolean running = syncedTargetTimer > 0;
        float gameDeltaTicks = Mth.clamp(delta.getGameTimeDeltaTicks(), 0.0f, 3.0f);
        float realtimeDeltaTicks = Mth.clamp(delta.getRealtimeDeltaTicks(), 0.0f, 3.0f);

        if (!initialized || blockPos != currentBlockPos) {
            blockPos = currentBlockPos;
            initialized = true;
            wasRunning = running;
            targetTimer = syncedTargetTimer;
            lastSyncedProcessTimer = syncedProcessTimer;
            processTicks = syncedProcessTimer;
            revealTicks = SLOT_REVEAL_TICKS;
            return;
        }

        if (running) {
            if (!wasRunning || targetTimer != syncedTargetTimer || syncedProcessTimer < lastSyncedProcessTimer) {
                processTicks = syncedProcessTimer;
            } else {
                processTicks = Math.max(processTicks, syncedProcessTimer);
                processTicks += gameDeltaTicks;
            }
            targetTimer = syncedTargetTimer;
            processTicks = Mth.clamp(processTicks, 0.0f, targetTimer);
            revealTicks = SLOT_REVEAL_TICKS;
        } else if (wasRunning) {
            targetTimer = 0;
            processTicks = 0.0f;
            revealTicks = 0.0f;
        } else if (revealTicks < SLOT_REVEAL_TICKS) {
            revealTicks = Mth.clamp(revealTicks + realtimeDeltaTicks, 0.0f, SLOT_REVEAL_TICKS);
        }

        lastSyncedProcessTimer = syncedProcessTimer;
        wasRunning = running;
    }

    public boolean isRunningFor(TransmutationCrucibleBlockEntity crucible) {
        return initialized && blockPos == crucible.getBlockPos().asLong() && targetTimer > 0;
    }

    public float processProgress() {
        if (targetTimer <= 0) {
            return 0.0f;
        }
        return Mth.clamp(processTicks / targetTimer, 0.0f, 1.0f);
    }

    public float revealScale() {
        if (revealTicks >= SLOT_REVEAL_TICKS) {
            return 1.0f;
        }
        return Easing.CUBIC_OUT.ease(revealTicks, 0.0f, 1.0f, SLOT_REVEAL_TICKS);
    }
}
