package remelon.cat.exorcism;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import remelon.cat.exorcism.datagen.ProcessingRecipeGen;
import remelon.cat.exorcism.datagen.StandardRecipeGen;

import java.util.Map;
import java.util.function.BiConsumer;

import static remelon.cat.exorcism.Exorcism.EXO_REGISTRATE;

public class ExorcismDataGen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		ExistingFileHelper helper = ExistingFileHelper.withResourcesFromArg();
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		EXO_REGISTRATE.setupDatagen(pack, helper);
		gatherData(pack, helper);
	}

	public static void gatherData(FabricDataGenerator.Pack pack, ExistingFileHelper existingFileHelper) {
		addExtraRegistrateData();

		pack.addProvider(ProcessingRecipeGen::registerAll);
		pack.addProvider(StandardRecipeGen::new);
	}

	private static void addExtraRegistrateData() {
		Exorcism.EXO_REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
			BiConsumer<String, String> langConsumer = provider::add;

			provideDefaultLang("interface", langConsumer);
			// provideDefaultLang("tooltips", langConsumer);
		});
	}

	private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
		String path = "assets/exorcism/lang/default/" + fileName + ".json";
		JsonElement jsonElement = FilesHelper.loadJsonResource(path);
		if (jsonElement == null) {
			throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
		}
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().getAsString();
			consumer.accept(key, value);
		}
	}
}
