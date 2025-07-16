package remelon.cat.exorcism;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import remelon.cat.exorcism.fan.EXFanProcessingTypes;
import remelon.cat.exorcism.recipe.ExorcisingRecipeRegistry;

public class Exorcism implements ModInitializer {
	public static final String ID = "exorcism";
	public static final String NAME = "Create: Exorcism";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static final CreateRegistrate EXO_REGISTRATE = CreateRegistrate.create("exorcism");

	static {
		EXO_REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
				.andThen(TooltipModifier.mapNull(KineticStats.create(item))));
	}

	public static Identifier GenID(String name) {
		return new Identifier(ID, name);
	}


	@Override
	public void onInitialize() {
		LOGGER.info("Create addon mod [{}] is loading alongside Create [{}]!", NAME, Create.VERSION);
		LOGGER.info(EnvExecutor.unsafeRunForDist(
				() -> () -> "{} is accessing Porting Lib from the client!",
				() -> () -> "{} is accessing Porting Lib from the server!"
		), NAME);
		ExorcismItems.register();
		HolyFluid.register();
		ExorcisingRecipeRegistry.register();
		CreativeTab.registerItemGroups();
		EXFanProcessingTypes.register();
		EXO_REGISTRATE.register();
	}

}
