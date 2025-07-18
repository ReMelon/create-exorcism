package remelon.cat.exorcism.compat.recipe;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.compat.emi.BlueprintTransferHandler;
import com.simibubi.create.compat.emi.DoubleItemIcon;
import com.simibubi.create.compat.emi.GhostIngredientHandler;
import com.simibubi.create.compat.emi.recipes.CreateEmiRecipe;
import com.simibubi.create.compat.emi.recipes.fan.FanEmiRecipe;
import com.simibubi.create.compat.rei.ToolboxColoringRecipeMaker;
import com.simibubi.create.content.equipment.blueprint.BlueprintScreen;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.logistics.filter.AttributeFilterScreen;
import com.simibubi.create.content.logistics.filter.FilterScreen;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerScreen;
import com.simibubi.create.content.trains.schedule.ScheduleScreen;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.NotNull;

import remelon.cat.exorcism.Exorcism;
import remelon.cat.exorcism.ExorcismFluid;
import remelon.cat.exorcism.ExorcismItems;
import remelon.cat.exorcism.compat.recipe.category.ExorcisingCategoryEMI;
import remelon.cat.exorcism.recipe.ExorcisingRecipeTypes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public class EMI implements EmiPlugin {
	public static final Map<Identifier, EmiRecipeCategory> ALL = new LinkedHashMap<>();

	public static final EmiRecipeCategory
			FAN_EXORCISING = register("fan_exorcising", DoubleItemIcon.of(AllItems.PROPELLER.get(), ExorcismFluid.HOLY_WATER.get().getBucketItem()));


	@Override
	public void register(EmiRegistry registry) {
		registry.removeEmiStacks(s -> {
			Object key = s.getKey();
			if (key instanceof TagDependentIngredientItem tagDependent && tagDependent.shouldHide())
				return true;
			return key instanceof VirtualFluid;
		});

		registry.addGenericExclusionArea((screen, consumer) -> {
			if (screen instanceof AbstractSimiContainerScreen<?> simi) {
				simi.getExtraAreas().forEach(r -> consumer.accept(new Bounds(r.getX(), r.getY(), r.getWidth(), r.getHeight())));
			}
		});

		registry.addRecipeHandler(AllMenuTypes.CRAFTING_BLUEPRINT.get(), new BlueprintTransferHandler());

		registry.addDragDropHandler(FilterScreen.class, new GhostIngredientHandler());
		registry.addDragDropHandler(AttributeFilterScreen.class, new GhostIngredientHandler());
		registry.addDragDropHandler(BlueprintScreen.class, new GhostIngredientHandler());
		registry.addDragDropHandler(LinkedControllerScreen.class, new GhostIngredientHandler());
		registry.addDragDropHandler(ScheduleScreen.class, new GhostIngredientHandler());

		registerGeneratedRecipes(registry);

		registry.setDefaultComparison(AllFluids.POTION.get().getStill(), c -> Comparison.compareNbt());

		ALL.forEach((id, category) -> registry.addCategory(category));

		registry.addWorkstation(FAN_EXORCISING, FanEmiRecipe.getFan("fan_exorcising"));

		RecipeManager manager = registry.getRecipeManager();

		addAll(registry, ExorcisingRecipeTypes.EXORCISING, ExorcisingCategoryEMI::new);


		// Introspective recipes based on present stacks need to make sure
		// all stacks are populated by other plugins
		registry.addDeferredRecipes(this::addDeferredRecipes);
	}

	@SuppressWarnings("unchecked")
	private <T extends Recipe<?>> void addAll(EmiRegistry registry, ExorcisingRecipeTypes type, Function<T, EmiRecipe> constructor) {
		for (T recipe : (List<T>) registry.getRecipeManager().listAllOfType(type.getType())) {
			registry.addRecipe(constructor.apply(recipe));
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends Recipe<?>> void addAll(EmiRegistry registry, AllRecipeTypes type, EmiRecipeCategory category,
											  BiFunction<EmiRecipeCategory, T, EmiRecipe> constructor) {
		for (T recipe : (List<T>) registry.getRecipeManager().listAllOfType(type.getType())) {
			registry.addRecipe(constructor.apply(category, recipe));
		}
	}

	/**
	 * Register an In World Interaction recipe
	 *
	 * @param registry EmiRegistry
	 * @param outputId The block being outputted in the form of `modid/block` an example for stone would be `minecraft/stone`
	 * @param left The stack that will be shown in the left input
	 * @param right The stack that will be shown in the right input
	 * @param output The stack that will be outputted from this interaction recipe
	 */
	private void addFluidInteractionRecipe(@NotNull EmiRegistry registry, String outputId, Fluid left, Fluid right, Block output) {
		// EmiStack doesn't accept flowing fluids, must always be a source
		if (left instanceof SimpleFlowableFluid.Flowing flowing)
			left = flowing.getStill();
		if (right instanceof SimpleFlowableFluid.Flowing flowing)
			right = flowing.getStill();

		// fabric: 81000 droplets = 1000 mb
		registry.addRecipe(EmiWorldInteractionRecipe.builder()
				.id(Exorcism.GenID("/world/fluid_interaction/" + outputId))
				.leftInput(EmiStack.of(left, 81000))
				.rightInput(EmiStack.of(right, 81000), false)
				.output(EmiStack.of(output))
				.build()
		);
	}

	private void addDeferredRecipes(Consumer<EmiRecipe> consumer) {
		List<Fluid> fluids = EmiApi.getIndexStacks().stream()
				.filter(s -> s.getKey() instanceof Fluid)
				.map(s -> (Fluid) s.getKey())
				.distinct()
				.toList();
		for (EmiStack stack : EmiApi.getIndexStacks()) {
			if (stack.getKey() instanceof Item i) {
				ItemStack is = stack.getItemStack();
				if (i instanceof PotionItem) {
					FluidStack potion = PotionFluidHandler.getFluidFromPotionItem(is);
					Ingredient bottle = Ingredient.ofItems(Items.GLASS_BOTTLE);
					Identifier iid = Registries.ITEM.getId(i);
					Identifier pid = Registries.POTION.getId(PotionUtil.getPotion(is));
				}
			}
		}
	}

	public void registerGeneratedRecipes(EmiRegistry registry) {
		ToolboxColoringRecipeMaker.createRecipes().forEach(r -> {
			ItemStack toolbox = null;
			ItemStack dye = null;
			for (Ingredient ingredient : r.getIngredients()) {
				for (ItemStack stack : ingredient.getMatchingStacks()) {
					if (toolbox == null && stack.getItem() instanceof BlockItem block && block.getBlock() instanceof ToolboxBlock) {
						toolbox = stack;
					} else if (dye == null && stack.getItem() instanceof DyeItem) {
						dye = stack;
					}
					if (toolbox != null && dye != null) break;
				}
			}
			if (toolbox == null || dye == null) return;
			Identifier toolboxId = Registries.ITEM.getId(toolbox.getItem());
			Identifier dyeId = Registries.ITEM.getId(dye.getItem());
			String recipeName = "create/toolboxes/%s/%s/%s/%s"
					.formatted(toolboxId.getNamespace(), toolboxId.getPath(), dyeId.getNamespace(), dyeId.getPath());
			registry.addRecipe(new EmiCraftingRecipe(
					r.getIngredients().stream().map(EmiIngredient::of).toList(),
					CreateEmiRecipe.getResultEmi(r), new Identifier("emi", recipeName)));
		});
		// for EMI we don't do this since it already has a category, World Interaction
	}

	public static boolean doInputsMatch(Recipe<?> a, Recipe<?> b) {
		if (!a.getIngredients().isEmpty() && !b.getIngredients().isEmpty()) {
			ItemStack[] matchingStacks = a.getIngredients().get(0).getMatchingStacks();
			if (matchingStacks.length != 0) {
				return b.getIngredients().get(0).test(matchingStacks[0]);
			}
		}
		return false;
	}

	private static EmiRecipeCategory register(String name, EmiRenderable icon) {
		Identifier id = Exorcism.GenID(name);
		EmiRecipeCategory category = new EmiRecipeCategory(id, icon);
		ALL.put(id, category);
		return category;
	}
}
