package remelon.cat.exorcism;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import remelon.cat.exorcism.Exorcism;

import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BibleDataLoader implements IdentifiableResourceReloadListener {
	public static JsonObject bibleJson;

	public static final Identifier ID = new Identifier("exorcism", "bible_loader");

	public Identifier getId() {
		return ID;
	}

	@Override
	public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager,
										  Profiler profiler1, Profiler profiler2,
										  Executor executor1, Executor executor2) {
		return CompletableFuture.runAsync(() -> {
			try {
				Resource resource = manager.getResource(new Identifier("exorcism", "bible.json")).orElseThrow();
				try (var stream = resource.getInputStream()) {
					bibleJson = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
					Exorcism.LOGGER.info("Bible JSON loaded successfully");
				}
			} catch (Exception e) {
				Exorcism.LOGGER.error("Failed to load Bible JSON", e);
			}
		}, executor2).thenCompose(synchronizer::whenPrepared);
	}

	@Override
	public Identifier getFabricId() {
		return getId();
	}

}
