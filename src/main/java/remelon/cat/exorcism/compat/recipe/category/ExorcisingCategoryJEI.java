package remelon.cat.exorcism.compat.recipe.category;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import remelon.cat.exorcism.HolyFluid;
import remelon.cat.exorcism.recipe.ExorcisingRecipe;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ExorcisingCategoryJEI extends ProcessingViaFanCategory.MultiOutput<ExorcisingRecipe>{

	public ExorcisingCategoryJEI(Info<ExorcisingRecipe> info) {
		super(info);
	}


	@Override
	protected AllGuiTextures getBlockShadow() {
		return AllGuiTextures.JEI_LIGHT;
	}

	@Override
	protected void renderAttachedBlock(DrawContext graphics) {
		GuiGameElement.of(HolyFluid.HOLY_WATER.getUnchecked())
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
