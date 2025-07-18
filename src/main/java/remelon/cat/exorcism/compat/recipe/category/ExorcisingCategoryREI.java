package remelon.cat.exorcism.compat.recipe.category;

import com.simibubi.create.compat.rei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.rei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import remelon.cat.exorcism.ExorcismFluid;
import remelon.cat.exorcism.recipe.ExorcisingRecipe;

public class ExorcisingCategoryREI extends ProcessingViaFanCategory.MultiOutput<ExorcisingRecipe> {
	public ExorcisingCategoryREI(Info<ExorcisingRecipe> info) {
		super(info);
	}

	@Override
	protected AllGuiTextures getBlockShadow() {
		return AllGuiTextures.JEI_LIGHT;
	}

	@Override
	protected void renderAttachedBlock(DrawContext graphics) {
		GuiGameElement.of(ExorcismFluid.HOLY_WATER.getUnchecked())
				.scale(SCALE)
				.atLocal(0, 0, 2)
				.lighting(AnimatedKinetics.DEFAULT_LIGHTING)
				.render(graphics);
	}

	@Override
	public Text getTitle() {
		return Text.translatable("exorcism.fan.recipe");
	}
}
