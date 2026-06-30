package com.linngdu664.transmutatoria.client.tool;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.renderer.state.blockentity.CrucibleRSlotPose;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import static com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity.*;

public class CrucibleItemAnimator {
    private record AnimLayer(int start, int end, float omega, float spinOmega) {}

    private static final AnimLayer[] LAYERS = {
            new AnimLayer(0, 9, 0.059f, 0.0182f),
            new AnimLayer(9, 18, 0.05f, 0.02f),
            new AnimLayer(18, 27, 0.041f, 0.0218f)
    };

    private CrucibleRSlotState[] states0;
    private CrucibleRSlotState[] states1;
    private float inputScale0;
    private float inputScale1;
    private int stopTimer;
    private boolean suppressInputSlotScale;

    public CrucibleItemAnimator(RandomSource random) {
        float[] baseXZ = new float[]{-0.2f, 0.0f, 0.2f};
        float[] baseY = new float[]{0.34f, 0.39f, 0.44f};
        float[] baseR = new float[]{0.28f, 0.25f, 0.28f, 0.25f, 0.05f, 0.25f, 0.28f, 0.25f, 0.28f};
        states0 = new CrucibleRSlotState[RENDERER_SLOT_COUNT];
        for (int i = 0; i < RENDERER_SLOT_COUNT; i++) {
            states0[i] = new CrucibleRSlotState(
                    baseXZ[i % 3] - 0.075f + random.nextFloat() * 0.15f,
                    baseY[i / 9],
                    baseXZ[i / 3 % 3] - 0.075f + random.nextFloat() * 0.15f,
                    baseR[i % 9],
                    -0.4f + random.nextFloat() * 0.8f,
                    -Mth.PI + random.nextFloat() * Mth.TWO_PI
            );
        }
        states1 = states0;
    }

    public void tick(int processTimer, int targetTimer) {
        states0 = states1;
        inputScale0 = inputScale1;

        if (stopTimer != 0) {
            stopTimer--;
            if (stopTimer == 0) {
                suppressInputSlotScale = false;
            }
        } else if (targetTimer != 0 && processTimer >= targetTimer) {
            stopTimer = TIME_PER_ESSENCE;
        }

        int timer = Math.max(Math.max(Math.min(processTimer, TIME_PER_ESSENCE), Math.min(stopTimer, TIME_PER_ESSENCE)), 0);
        if (timer > 0) {
            float percent = timer * (1f / TIME_PER_ESSENCE);
            CrucibleRSlotState[] newStates = new CrucibleRSlotState[RENDERER_SLOT_COUNT];
            for (AnimLayer layer : LAYERS) {
                for (int i = layer.start; i < layer.end; i++) {
                    newStates[i] = states0[i].tick(layer.omega, layer.spinOmega, percent);
                }
            }
            states1 = newStates;
        }

        int timeRemaining = targetTimer - processTimer;
        if (targetTimer <= 0) {
            inputScale1 = 1f;
        } else if (targetTimer <= 2 * TIME_PER_ESSENCE) {
            inputScale1 = Mth.clamp((float) timeRemaining / targetTimer, 0f, 1f);
        } else if (targetTimer <= 3 * TIME_PER_ESSENCE) {
            int zeroScaleTime = targetTimer - 2 * TIME_PER_ESSENCE;
            if (timeRemaining <= zeroScaleTime) {
                inputScale1 = 0f;
            } else {
                inputScale1 = Math.min((timeRemaining - zeroScaleTime) * (1f / (2 * TIME_PER_ESSENCE)), 1f);
            }
        } else {
            if (timeRemaining <= TIME_PER_ESSENCE) {
                inputScale1 = 0f;
            } else if (timeRemaining <= 3 * TIME_PER_ESSENCE) {
                inputScale1 = (timeRemaining - TIME_PER_ESSENCE) * (1f / (2 * TIME_PER_ESSENCE));
            } else {
                inputScale1 = 1f;
            }
        }
    }

    public CrucibleRSlotPose extractPose(int rendererSlot, int realSlot, float partialTicks) {
        CrucibleRSlotState s0 = states0[rendererSlot];
        CrucibleRSlotState s1 = states1[rendererSlot];
        return s0.lerp(s1, partialTicks, getScaleForSlot(realSlot, partialTicks));
    }

    public void onInputSlotFilled() {
        if (stopTimer > 0) {
            suppressInputSlotScale = true;
        }
    }

    private float getScaleForSlot(int realSlot, float partialTicks) {
        float scale = 0.25f;
        if (realSlot < TransmutationCrucibleBlockEntity.ESSENCE_OUTPUT_SLOT_BEGIN) {
            scale *= Mth.lerp(partialTicks, inputScale0, inputScale1);
        } else if (realSlot < CATALYST_SLOT || realSlot == OUTPUT_SLOT) {
            scale *= calcOutputScale(partialTicks);
        } else if (realSlot == INPUT_SLOT) {
            if (suppressInputSlotScale) {
                // todo 输入物品还是有bug，反应后立刻进入锅的输入物品还是放缩小的动画
                scale *= Mth.lerp(partialTicks, inputScale0, inputScale1);
            } else {
                float outputScale = calcOutputScale(partialTicks);
                scale *= outputScale >= 1f ? Mth.lerp(partialTicks, inputScale0, inputScale1) : outputScale;
            }
        }
        return scale;
    }

    private float calcOutputScale(float partialTicks) {
        return Mth.clamp((TIME_PER_ESSENCE - stopTimer + partialTicks) * (1f / TIME_PER_ESSENCE), 0f, 1f);
    }
}
