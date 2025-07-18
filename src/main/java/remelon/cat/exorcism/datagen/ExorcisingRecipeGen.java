package remelon.cat.exorcism.datagen;

import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen;

import com.simibubi.create.foundation.utility.RegisteredObjects;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Blocks;
import net.minecraft.recipe.Ingredient;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
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

	TORCH = convertWithBonus(Items.SOUL_TORCH, Items.TORCH, Items.CHARCOAL, 0.1f),
	CAMPFIRE = convertWithBonus(Items.SOUL_CAMPFIRE, Items.CAMPFIRE, Items.CHARCOAL, 0.1f),
	LANTERN = convertWithBonus(Items.SOUL_LANTERN, Items.LANTERN, Items.IRON_NUGGET, 0.1f),

	POTATO = convertWithBonus(Items.POISONOUS_POTATO, Items.POTATO, Items.POTATO, 0.1f),
	GLOW_INK = convertWithBonus(Items.GLOW_INK_SAC, Items.INK_SAC, Items.LILY_PAD, 0.1f),
	BERRIES = convertWithBonus(Items.GLOW_BERRIES, Items.SWEET_BERRIES, Items.BONE_MEAL, 0.1f),
	BRICK = convertWithBonus(Items.NETHER_BRICK, Items.BRICK, Items.CHARCOAL, 0.1f),

	SAND = convertWithBonus(Blocks.SOUL_SAND, Blocks.SAND, Items.BONE, 0.1f),
	DIRT = create(Exorcism.GenID("soul_soil_to_dirt"), b -> b.require(Ingredient.ofItems(Blocks.SOUL_SOIL)).output(Items.DIRT)),
	STONE = convertWithBonus(Blocks.BLACKSTONE, Blocks.COBBLESTONE, Items.BASALT, 0.1f),
	CRIMSON_FUNGUS = convertWithBonus(Items.CRIMSON_FUNGUS, Items.RED_MUSHROOM, Items.NETHER_WART, 0.1f),
	WARPED_FUNGUS = convertWithBonus(Items.WARPED_FUNGUS, Items.BROWN_MUSHROOM, Items.NETHER_WART, 0.1f);

	public GeneratedRecipe convert(ItemConvertible input, ItemConvertible result) {
		return convert(() -> Ingredient.ofItems(input), () -> result);
	}

	public GeneratedRecipe convert(Supplier<Ingredient> input, Supplier<ItemConvertible> result) {
		return create(
				Exorcism.GenID(RegisteredObjects.getKeyOrThrow(result.get().asItem()).getPath()),
						p -> p.withItemIngredients(input.get()).output(result.get()));
	}

	public GeneratedRecipe convertWithBonus(ItemConvertible input, ItemConvertible guaranteedOutput, ItemConvertible bonusOutput, float bonusChance) {
		return create(
				Exorcism.GenID(RegisteredObjects.getKeyOrThrow(guaranteedOutput.asItem()).getPath() + "_with_bonus"),
				b -> b
						.require(input)
						.output(guaranteedOutput)
						.output(bonusChance, bonusOutput)
						.duration(100)
		);
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
