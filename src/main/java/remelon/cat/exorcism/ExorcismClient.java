package remelon.cat.exorcism;

import com.simibubi.create.content.kinetics.fan.AirFlowParticle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.particle.EnchantGlyphParticle;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.resource.ResourceType;
import remelon.cat.exorcism.bible.BibleDataLoader;

public class ExorcismClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
				.registerReloadListener(new BibleDataLoader());

		// For this example, we will use the end rod particle behaviour.
		ParticleFactoryRegistry.getInstance().register(Exorcism.HOLY_CROSS_PARTICLE, EndRodParticle.Factory::new);
	}
}
