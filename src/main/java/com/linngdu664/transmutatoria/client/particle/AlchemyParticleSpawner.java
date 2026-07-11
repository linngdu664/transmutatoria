package com.linngdu664.transmutatoria.client.particle;

import com.linngdu664.transmutatoria.init.InitParticles;
import com.linngdu664.transmutatoria.client.renderer.blockentity.TransmutationCrucibleRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/** Client-only creation helpers for the crucible's continuous and completion effects. */
public final class AlchemyParticleSpawner {
    private static final double GATHER_RADIUS = 2.0;
    // Matches TransmutationCrucibleBlock's collision shape: walls span 2/16 to 14/16 and the floor reaches 5/16 high.
    private static final double HOLLOW_MIN = 2.0 / 16.0;
    private static final double HOLLOW_MAX = 14.0 / 16.0;
    private static final double HOLLOW_FLOOR_Y = 5.0 / 16.0;
    private static final double HOLLOW_RIM_Y = 15.0 / 16.0;
    private static final double HOLLOW_PARTICLE_MARGIN = 0.08;
    private static final double HOLLOW_CENTER_Y = 0.5;
    private static final double HOLLOW_HALF_WIDTH = HOLLOW_MAX - 0.5 - HOLLOW_PARTICLE_MARGIN;
    private static final double GATHER_MIN_DIRECTION_Y = (HOLLOW_RIM_Y - HOLLOW_CENTER_Y)
            / Math.sqrt((HOLLOW_RIM_Y - HOLLOW_CENTER_Y) * (HOLLOW_RIM_Y - HOLLOW_CENTER_Y) + HOLLOW_HALF_WIDTH * HOLLOW_HALF_WIDTH);
    private static final double GATHER_INITIAL_SPEED = GATHER_RADIUS * (1.0 - AlchemyGatherParticle.FRICTION)
            / (1.0 - Math.pow(AlchemyGatherParticle.FRICTION, AlchemyGatherParticle.LIFETIME));
    private AlchemyParticleSpawner() {}

    public static void spawnGather(ClientLevel level, BlockPos pos, int polarity) {
        RandomSource random = level.getRandom();
        // The top spherical cap enters through the 12x12-pixel opening instead of crossing a crucible wall.
        double y = Mth.lerp(random.nextDouble(), GATHER_MIN_DIRECTION_Y, 1.0);
        double horizontalRadius = Math.sqrt(1.0 - y * y);
        double angle = random.nextDouble() * Mth.TWO_PI;
        Vec3 outward = new Vec3(Math.cos(angle) * horizontalRadius, y, Math.sin(angle) * horizontalRadius);
        Vec3 spawnPos = Vec3.atCenterOf(pos).add(outward.scale(GATHER_RADIUS));
        addTintedParticle(InitParticles.ALCHEMICAL_GATHER.get(), spawnPos, outward.scale(-GATHER_INITIAL_SPEED), polarity);
    }

    public static void spawnBurst(ClientLevel level, BlockPos pos, int polarity) {
        RandomSource random = level.getRandom();
        for (int i = 0; i < 10; i++) {
            Vec3 spawnPos = new Vec3(
                    pos.getX() + randomHollowCoordinate(random),
                    pos.getY() + HOLLOW_FLOOR_Y + 0.01,
                    pos.getZ() + randomHollowCoordinate(random));
            Vec3 velocity = new Vec3(0.0, Mth.lerp(random.nextFloat(), 0.05F, 0.20F), 0.0);
            addTintedParticle(InitParticles.ALCHEMICAL_BURST.get(), spawnPos, velocity, polarity);
        }
    }

    private static double randomHollowCoordinate(RandomSource random) {
        return Mth.lerp(random.nextDouble(), HOLLOW_MIN + HOLLOW_PARTICLE_MARGIN, HOLLOW_MAX - HOLLOW_PARTICLE_MARGIN);
    }

    private static void addTintedParticle(net.minecraft.core.particles.SimpleParticleType type, Vec3 position, Vec3 velocity, int polarity) {
        Particle particle = Minecraft.getInstance().particleEngine.createParticle(
                type, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
        if (particle instanceof AlchemyGatherParticle || particle instanceof AlchemyBurstParticle) {
            int waterColor = TransmutationCrucibleRenderer.getWaterSurfaceColor(polarity);
            ((net.minecraft.client.particle.SingleQuadParticle) particle).setColor(
                    ARGB.red(waterColor) / 255.0F,
                    ARGB.green(waterColor) / 255.0F,
                    ARGB.blue(waterColor) / 255.0F);
        }
    }
}
