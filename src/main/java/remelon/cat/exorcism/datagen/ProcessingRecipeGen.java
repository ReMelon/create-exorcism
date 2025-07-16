package remelon.cat.exorcism.datagen;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;


import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ProcessingRecipeGen {

	protected static final List<com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen> GENERATORS = new ArrayList<>();

	public static DataProvider registerAll(FabricDataOutput output) {
		GENERATORS.add(new ExorcisingRecipeGen(output));

		return new DataProvider() {

			@Override
			public String getName() {
				return "Create: Exorcism's Processing Recipes";
			}

			@Override
			public CompletableFuture<?> run(DataWriter output) {
				return CompletableFuture.allOf(GENERATORS.stream()
						.map(gen -> gen.run(output))
						.toArray(CompletableFuture[]::new));
			}
		};
	}
}
