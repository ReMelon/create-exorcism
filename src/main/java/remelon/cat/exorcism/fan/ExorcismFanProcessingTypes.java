package remelon.cat.exorcism.fan;

import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingTypeRegistry;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.VecHelper;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import remelon.cat.exorcism.Exorcism;
import remelon.cat.exorcism.ExorcismDamageTypes;
import remelon.cat.exorcism.ExorcismFluid;
import remelon.cat.exorcism.recipe.ExorcisingRecipe;
import remelon.cat.exorcism.recipe.ExorcisingRecipe.ExorcisingWrapper;
import remelon.cat.exorcism.recipe.ExorcisingRecipeTypes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
public class ExorcismFanProcessingTypes extends AllFanProcessingTypes {
	public static final ExorcisingType EXORCISING = register("exorcising", new ExorcisingType());
	private static final Map<String, FanProcessingType> LEGACY_NAME_MAP;

	static {
		Object2ReferenceOpenHashMap<String, FanProcessingType> map = new Object2ReferenceOpenHashMap<>();
		map.put("EXORCISING", EXORCISING);
		map.trim();
		LEGACY_NAME_MAP = map;
	}

	private static <T extends FanProcessingType> T register(String id, T type) {
		FanProcessingTypeRegistry.register(Exorcism.GenID(id), type);
		return type;
	}

	@Nullable
	public static FanProcessingType ofLegacyName(String name) {
		return LEGACY_NAME_MAP.get(name);
	}

	public static void register() {
		EXORCISING.toString();
	}

	public static class ExorcisingType implements FanProcessingType {
		private static final ExorcisingWrapper WRAPPER = new ExorcisingWrapper();

		@Override
		public boolean isValidAt(World world, BlockPos pos) {
			BlockState state = world.getBlockState(pos);
			return state.getFluidState().getFluid() == ExorcismFluid.HOLY_WATER.get().getStill();
		}

		@Override
		public int getPriority() {
			return 301;
		}

		@Override
		public boolean canProcess(ItemStack stack, World world) {
			WRAPPER.setStack(0, stack);
			return ExorcisingRecipeTypes.EXORCISING.find(WRAPPER, world).isPresent();
		}

		@Override
		public List<ItemStack> process(ItemStack stack, World world) {
			WRAPPER.setStack(0, stack);
			Optional<ExorcisingRecipe> recipe = ExorcisingRecipeTypes.EXORCISING.find(WRAPPER, world);
			return recipe.map(r -> RecipeApplier.applyRecipeOn(world, stack, r)).orElse(null);
		}

		@Override
		public void spawnProcessingParticles(World world, Vec3d pos) {
			if (world.random.nextInt(8) != 0) return;
			Vec3d offset = VecHelper.offsetRandomly(Vec3d.ZERO, world.random, 1)
					.multiply(1, 0.05f, 1)
					.normalize()
					.multiply(0.15f);
			Vec3d newPos = pos.add(offset);
			world.addParticle(ParticleTypes.ENCHANT, newPos.x, newPos.y + .45, newPos.z, 0, 0, 0);
			if (world.random.nextInt(2) == 0)
				world.addParticle(Exorcism.HOLY_CROSS_PARTICLE, newPos.x, newPos.y + .25, newPos.z, 0, 0, 0);
		}

		@Override
		public void morphAirFlow(AirFlowParticleAccess access, Random random) {
			access.setColor(Color.mixColors(0x97b3c7, 0x6890b3, random.nextFloat()));
			access.setAlpha(1f);
		}

		@Override
		public void affectEntity(Entity entity, World world) {
			if(entity instanceof LivingEntity living && living.isUndead()) {
				entity.damage(ExorcismDamageTypes.Exorcising(world), 0.5F);

			} else if (entity instanceof LivingEntity living && living.isPlayer()) {
				((LivingEntity) entity).heal(0.1F);
			}
		}
	}
}
