package com.linngdu664.transmutatoria.client.init;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.particle.AlchemyBurstParticle;
import com.linngdu664.transmutatoria.client.particle.AlchemyGatherParticle;
import com.linngdu664.transmutatoria.init.InitParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public final class ParticleRegistry {
    private ParticleRegistry() {}

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(InitParticles.ALCHEMICAL_GATHER.get(), AlchemyGatherParticle.Provider::new);
        event.registerSpriteSet(InitParticles.ALCHEMICAL_BURST.get(), AlchemyBurstParticle.Provider::new);
    }
}
