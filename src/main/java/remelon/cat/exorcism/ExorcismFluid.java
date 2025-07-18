package remelon.cat.exorcism;

import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import com.tterrag.registrate.util.entry.FluidEntry;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import static net.minecraft.item.Items.BUCKET;
import static remelon.cat.exorcism.Exorcism.EXO_REGISTRATE;

public class ExorcismFluid {
	public static final Identifier HOLY_STILL_ID = Exorcism.GenID("fluid/holy_water_still");
	public static final Identifier HOLY_FLOW_ID = Exorcism.GenID("fluid/holy_water_flow");

	public static final FluidEntry<SimpleFlowableFluid.Flowing> HOLY_WATER = EXO_REGISTRATE
			.fluid("holy_water", HOLY_STILL_ID, HOLY_FLOW_ID)
			.lang("Holy Water")
			.fluidAttributes(()->new FluidVariantAttributeHandler(){
				@Override
				public Text getName(FluidVariant fluidVariant) {
					return Text.translatable("fluid.exorcism.holy_water");
				}
				@Override
				public boolean isLighterThanAir(FluidVariant variant) {
					return false;
				}

			})
			.tag(FluidTags.WATER)
			.source(SimpleFlowableFluid.Source::new)
			.renderType(() -> () -> RenderLayer.getTranslucent())
			.block()
			.properties(p -> p.liquid().mapColor(DyeColor.LIGHT_BLUE))
			.properties(p -> p.luminance(value -> 10))
			.build()
			.bucket()
			.tab(ExorcismItemGroup.EXORCISM_TAB.key())
			.lang("Bucket of Holy Water")
			.build()
			.onRegisterAfter(RegistryKeys.ITEM, holy -> {
				Fluid source = holy.getStill();
				FluidStorage.combinedItemApiProvider(source.getBucketItem()).register(context ->
						new FullItemFluidStorage(context, bucket -> ItemVariant.of(BUCKET), FluidVariant.of(source), FluidConstants.BUCKET));

				FluidStorage.combinedItemApiProvider(BUCKET).register(context ->
						new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(source.getBucketItem()), source, FluidConstants.BUCKET));
			})

			.register();

	public static void register() {

	}
}
