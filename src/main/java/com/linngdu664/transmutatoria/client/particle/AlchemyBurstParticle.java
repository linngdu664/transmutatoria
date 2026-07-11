package com.linngdu664.transmutatoria.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/** A non-colliding upward trail particle used when a crucible reaction completes. */
public class AlchemyBurstParticle extends SingleQuadParticle {
    public static final int LIFETIME = 40;
    /** Multiplying velocity by this every tick changes it from 100% to 20% over the lifetime. */
    private static final float FRICTION = (float) Math.pow(0.1, 1.0 / (LIFETIME - 1));

    protected AlchemyBurstParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
                                   TextureAtlasSprite sprite) {
        super(level, x, y, z, sprite);
        this.hasPhysics = false;
        this.gravity = 0.0F;
        this.friction = FRICTION;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.quadSize = 0.18F;
        this.lifetime = LIFETIME;
        this.setAlpha(1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setAlpha(Mth.lerp((float) this.age / this.lifetime, 1.0F, 0.0F));
        }
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    protected int getLightCoords(float partialTick) {
        return 15728880;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new AlchemyBurstParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites.first());
        }
    }
}
