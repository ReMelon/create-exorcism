package remelon.cat.exorcism.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import remelon.cat.exorcism.Exorcism;

import java.util.Optional;
import java.util.function.Supplier;

public enum ExorcisingRecipeTypes implements IRecipeTypeInfo {
	EXORCISING(ExorcisingRecipe::new);

	private final Identifier id;
	private final RecipeSerializer<?> serializerObject;
	@Nullable
	private final RecipeType<?> typeObject;
	private final Supplier<RecipeType<?>> type;

	ExorcisingRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
		String name = Lang.asId(name());
		id = Exorcism.GenID(name);
		serializerObject = Registry.register(Registries.RECIPE_SERIALIZER, id, serializerSupplier.get());
		typeObject = simpleType(id);
		Registry.register(Registries.RECIPE_TYPE, id, typeObject);
		type = () -> typeObject;
	}

	ExorcisingRecipeTypes(ProcessingRecipeBuilder.ProcessingRecipeFactory<?> factory) {
		this(() -> new ProcessingRecipeSerializer<>(factory));
	}

	public static <T extends Recipe<?>> RecipeType<T> simpleType(Identifier id) {
		String stringId = id.toString();
		return new RecipeType<>() {
			@Override
			public String toString() {
				return stringId;
			}
		};
	}

	public static void register() {
		values();
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public <T extends RecipeSerializer<?>> T getSerializer() {
		return (T) serializerObject;
	}

	@Override
	public <T extends RecipeType<?>> T getType() {
		return (T) type.get();
	}

	public <C extends Inventory, T extends Recipe<C>> Optional<T> find(C inv, World world) {
		return world.getRecipeManager().getFirstMatch(getType(), inv, world);
	}
}
