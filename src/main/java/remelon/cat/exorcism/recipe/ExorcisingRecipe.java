package remelon.cat.exorcism.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer;
import net.minecraft.world.World;

public class  ExorcisingRecipe extends ProcessingRecipe<ExorcisingRecipe.ExorcisingWrapper> {

	public ExorcisingRecipe(ProcessingRecipeParams params) {
		super(ExorcisingRecipeTypes.EXORCISING, params);
	}

	@Override
	public boolean matches(ExorcisingWrapper inv, World world) {
		if (inv.isEmpty()) return false;
		return ingredients.get(0).test(inv.getStack(0));
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 12;
	}

	public static class ExorcisingWrapper extends ItemStackHandlerContainer {
		public ExorcisingWrapper() {
			super(1);
		}
	}
}
