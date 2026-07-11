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

/** A non-colliding particle that slows from full speed to half speed while travelling toward a crucible. */
public class AlchemyGatherParticle extends SingleQuadParticle {
    public static final int LIFETIME = 15;
    public static final float FRICTION = (float) Math.pow(0.5, 1.0 / (LIFETIME - 1));

    protected AlchemyGatherParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
                                    TextureAtlasSprite sprite) {
        super(level, x, y, z, sprite);
        this.hasPhysics = false;
        this.gravity = 0.0F;
        this.friction = FRICTION;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.quadSize = 0.35F;
        this.lifetime = LIFETIME;
        this.setAlpha(0.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setAlpha(Mth.lerp((float) this.age / this.lifetime, 0.0F, 0.7F));
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
            return new AlchemyGatherParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites.first());
        }
    }
}
