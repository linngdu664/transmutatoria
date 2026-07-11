package com.linngdu664.transmutatoria.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.linngdu664.transmutatoria.ArsTransmutatoria.MODID;

/** Particle types used by the transmutation crucible's client-side effects. */
public final class InitParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ALCHEMICAL_GATHER =
            PARTICLES.register("alchemical_gather", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ALCHEMICAL_BURST =
            PARTICLES.register("alchemical_burst", () -> new SimpleParticleType(false));

    private InitParticles() {}
}
