package remelon.cat.exorcism;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BiblePacks {
	@Environment(EnvType.CLIENT)
	public static void KJV() {
		if (FabricLoader.getInstance().isModLoaded("exorcism")) {
			ModContainer crystallized = FabricLoader.getInstance().getModContainer(Exorcism.ID)
					.orElseThrow(() -> new IllegalStateException("Exorcism's ModContainer couldn't be found!"));
			Identifier packId = Exorcism.GenID("kjv");
			ResourceManagerHelper.registerBuiltinResourcePack(packId, crystallized, Text.translatable("kjv-pack"), ResourcePackActivationType.NORMAL);
		}
	}

	public static void BSB() {
		if (FabricLoader.getInstance().isModLoaded("exorcism")) {
			ModContainer crystallized = FabricLoader.getInstance().getModContainer(Exorcism.ID)
					.orElseThrow(() -> new IllegalStateException("Exorcism's ModContainer couldn't be found!"));
			Identifier packId = Exorcism.GenID("bsb");
			ResourceManagerHelper.registerBuiltinResourcePack(packId, crystallized, Text.translatable("bsb-pack"), ResourcePackActivationType.NORMAL);
		}
	}

	public static void register() {
		BiblePacks.KJV();
		BiblePacks.BSB();
	}
}
