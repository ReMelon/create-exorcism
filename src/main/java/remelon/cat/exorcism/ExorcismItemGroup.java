package remelon.cat.exorcism;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;


public class ExorcismItemGroup {
	public static final TabInfo EXORCISM_TAB = register("Create: Exorcism",
			() -> FabricItemGroup.builder()
					.displayName(Text.translatable("exorcism.itemtab"))
					.icon(() -> new ItemStack(ExorcismItems.BIBLE))
					.entries((displayContext, entries) -> {
					})
					.build());

	private static TabInfo register(String name, Supplier<ItemGroup> supplier) {
		Identifier id = new Identifier("exorcism", "itemtab");
		RegistryKey<ItemGroup> key = RegistryKey.of(RegistryKeys.ITEM_GROUP, id);
		ItemGroup tab = supplier.get();
		Registry.register(Registries.ITEM_GROUP, key, tab);
		return new TabInfo(key, tab);
	}

	public record TabInfo(RegistryKey<ItemGroup> key, ItemGroup tab) {
	}

	public static void registerItemGroups() {
	}



}
