package remelon.cat.exorcism;

import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;

import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;


public class ExorcismDamageTypes {
	public static final RegistryKey<DamageType>
			EXORCISING = key("exorcism");

	private static RegistryKey<DamageType> key(String name) {
		return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Exorcism.GenID(name));
	}

	public static void bootstrap(Registerable<DamageType> ctx) {
		new DamageTypeBuilder(EXORCISING).effects(DamageEffects.BURNING).register(ctx);
	}

	public static DamageSource Exorcising(World world) {
		return source(EXORCISING, world);
	}

	private static DamageSource source(RegistryKey<DamageType> key, WorldView level) {
		Registry<DamageType> registry = level.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE);
		return new DamageSource(registry.entryOf(key));
	}

}
