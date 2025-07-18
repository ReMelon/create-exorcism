package remelon.cat.exorcism.compat.recipe.category;

import com.simibubi.create.compat.emi.CreateEmiAnimations;
import com.simibubi.create.compat.emi.recipes.fan.FanEmiRecipe;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import remelon.cat.exorcism.ExorcismFluid;
import remelon.cat.exorcism.compat.recipe.EMI;
import remelon.cat.exorcism.recipe.ExorcisingRecipe;

public class ExorcisingCategoryEMI extends FanEmiRecipe.MultiOutput<ExorcisingRecipe>{
	public ExorcisingCategoryEMI(ExorcisingRecipe recipe) {
		super(EMI.FAN_EXORCISING, recipe);
	}

	@Override
	protected void renderAttachedBlock(DrawContext graphics) {
		GuiGameElement.of(ExorcismFluid.HOLY_WATER.getUnchecked())
				.scale(SCALE)
				.atLocal(0, 0, 2)
				.lighting(CreateEmiAnimations.DEFAULT_LIGHTING)
				.render(graphics);
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return super.getCategory();
	}

	public Text getTitle() {
		return Text.translatable("exorcism.fan.recipe");
	}
}
