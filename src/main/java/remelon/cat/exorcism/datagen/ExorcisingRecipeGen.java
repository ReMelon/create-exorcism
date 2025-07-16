package remelon.cat.exorcism.datagen;

import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen;

import com.simibubi.create.foundation.utility.RegisteredObjects;

import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Blocks;
import net.minecraft.recipe.Ingredient;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import remelon.cat.exorcism.Exorcism;
import remelon.cat.exorcism.recipe.ExorcisingRecipeTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ExorcisingRecipeGen extends ProcessingRecipeGen {
	protected static final List<ExorcisingRecipeGen> GENERATORS = new ArrayList<>();

	GeneratedRecipe
			EXORCISE_STONE = convert(Items.INFESTED_STONE, Items.STONE),
			EXORCISE_DEEPSLATE = convert(Items.INFESTED_DEEPSLATE, Items.DEEPSLATE),
			EXORCISE_STONE_BRICKS = convert(Items.INFESTED_STONE_BRICKS, Items.STONE_BRICKS),
			EXORCISE_MOSSY_STONE_BRICKS = convert(Items.INFESTED_MOSSY_STONE_BRICKS, Items.MOSSY_STONE_BRICKS),
			EXORCISE_CRACKED_STONE_BRICKS = convert(Items.INFESTED_CRACKED_STONE_BRICKS, Items.CRACKED_STONE_BRICKS),
			EXORCISE_CHISELED_STONE_BRICKS = convert(Items.INFESTED_CHISELED_STONE_BRICKS, Items.CHISELED_STONE_BRICKS),

			TORCH = convert(Items.SOUL_TORCH, Items.TORCH),
			CAMPFIRE = convert(Items.SOUL_CAMPFIRE, Items.CAMPFIRE),
			LANTERN = convert(Items.SOUL_LANTERN, Items.LANTERN),

			POTATO = convert(Items.POISONOUS_POTATO, Items.POTATO),
			INK = convert(Items.GLOW_INK_SAC, Items.INK_SAC),
			BERRIES = convert(Items.GLOW_BERRIES, Items.SWEET_BERRIES),
			BRICK = convert(Items.NETHER_BRICK, Items.BRICK),

			SAND = convert(Blocks.SOUL_SAND, Blocks.SAND),
			DIRT = convert(() -> Ingredient.ofItems(Blocks.SOUL_SOIL), () -> Items.DIRT),
			STONE = convert(Blocks.BLACKSTONE, Blocks.COBBLESTONE),
			CRIMSON_FUNGUS = convert(Items.CRIMSON_FUNGUS, Items.RED_MUSHROOM),
			WARPED_FUNGUS = convert(Items.WARPED_FUNGUS, Items.BROWN_MUSHROOM);


	public GeneratedRecipe convert(ItemConvertible input, ItemConvertible result) {
		return convert(() -> Ingredient.ofItems(input), () -> result);
	}

	public GeneratedRecipe convert(Supplier<Ingredient> input, Supplier<ItemConvertible> result) {
		return create(
				Exorcism.GenID(RegisteredObjects.getKeyOrThrow(result.get().asItem()).getPath()),
						p -> p.withItemIngredients(input.get()).output(result.get()));
	}

	public ExorcisingRecipeGen(FabricDataOutput generator) {
		super(generator);
	}

	@Override
	protected ExorcisingRecipeTypes getRecipeType() {
		return ExorcisingRecipeTypes.EXORCISING;
	}

	@Override
	public String getName() {
		return "Create: Exorcism's Processing Recipes: " + getRecipeType().getId().getPath();
	}



}
