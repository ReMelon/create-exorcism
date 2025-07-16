package remelon.cat.exorcism.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;

import com.simibubi.create.foundation.utility.RegisteredObjects;

import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;

import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import com.google.common.base.Supplier;
import com.simibubi.create.Create;
import com.tterrag.registrate.util.entry.ItemProviderEntry;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import remelon.cat.exorcism.Exorcism;
import remelon.cat.exorcism.ExorcismItems;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.tterrag.registrate.providers.RegistrateRecipeProvider.inventoryTrigger;


@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class StandardRecipeGen extends CreateRecipeProvider {
	GeneratedRecipe
			BIBLE = create(() -> ExorcismItems.BIBLE.get()).unlockedBy(() -> Items.BOOK)
			.viaShaped(b -> b
					.input('B', Items.BOOK)
					.input('L', Items.LEATHER)
					.input('G', Items.GOLD_BLOCK)
					.pattern("BLG")
					.pattern("LLG")
					.pattern("GGG"));

	String currentFolder = "";

	public StandardRecipeGen(FabricDataOutput output) {
		super(output);
	}

	@Override
	public String getName() {
		return "Create: Exorcism's Standard Recipes";
	}

	String enterFolder(String folder) {
		currentFolder = folder;
		return currentFolder;
	}

	GeneratedRecipeBuilder create(Supplier<ItemConvertible> result) {
		return new GeneratedRecipeBuilder(currentFolder, result);
	}

	GeneratedRecipeBuilder create(Identifier result) {
		return new GeneratedRecipeBuilder(currentFolder, result);
	}

	GeneratedRecipeBuilder create(ItemProviderEntry<? extends ItemConvertible> result) {
		return create(result::get);
	}

	protected GeneratedRecipe register(GeneratedRecipe recipe) {
		all.add(recipe);
		return recipe;
	}

	class GeneratedRecipeBuilder {

		private String path;
		private String suffix;
		private Supplier<? extends ItemConvertible> result;
		private Identifier compatDatagenOutput;
		List<ConditionJsonProvider> recipeConditions;
		private RecipeCategory category;

		private Supplier<ItemPredicate> unlockedBy;
		private int amount;

		private GeneratedRecipeBuilder(String path) {
			this.path = path;
			this.recipeConditions = new ArrayList<>();
			this.suffix = "";
			this.amount = 1;
			this.category = RecipeCategory.MISC;
		}

		public GeneratedRecipeBuilder(String path, Supplier<? extends ItemConvertible> result) {
			this(path);
			this.result = result;
		}

		public GeneratedRecipeBuilder(String path, Identifier result) {
			this(path);
			this.compatDatagenOutput = result;
		}

		GeneratedRecipeBuilder unlockedBy(Supplier<? extends ItemConvertible> item) {
			this.unlockedBy = () -> ItemPredicate.Builder.create()
					.items(item.get())
					.build();
			return this;
		}

		GeneratedRecipeBuilder withCondition(ConditionJsonProvider condition) {
			recipeConditions.add(condition);
			return this;
		}

		GeneratedRecipe viaShaped(UnaryOperator<ShapedRecipeJsonBuilder> builder) {
			return register(consumer -> {
				ShapedRecipeJsonBuilder b = builder.apply(ShapedRecipeJsonBuilder.create(category, result.get(), amount));
				if (unlockedBy != null)
					b.criterion("has_item", inventoryTrigger(unlockedBy.get()));
				b.offerTo(consumer, createSimpleLocation(path));
			});
		}
		GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeJsonBuilder> builder) {
			return register(consumer -> {
				ShapelessRecipeJsonBuilder b =
						builder.apply(ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, result.get(), amount));
				if (unlockedBy != null)
					b.criterion("has_item", inventoryTrigger(unlockedBy.get()));
				b.offerTo(consumer, createSimpleLocation(path));
			});
		}

		private Identifier createSimpleLocation(String recipeType) {
			return Exorcism.GenID(recipeType + "/" + getRegistryName().getPath() + suffix);
		}

		private Identifier createLocation(String recipeType) {
			return Exorcism.GenID(recipeType + "/" + path + "/" + getRegistryName().getPath() + suffix);
		}

		private Identifier getRegistryName() {
			return compatDatagenOutput == null ? RegisteredObjects.getKeyOrThrow(result.get()
					.asItem()) : compatDatagenOutput;
		}


	}
}
