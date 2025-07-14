// BibleItem.java
package remelon.cat.exorcism;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import remelon.cat.exorcism.BibleScreenHandler;

public class BibleItem extends Item {
	public BibleItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (world.isClient) {
			MinecraftClient.getInstance().setScreen(new BibleBookScreen(null));
		}
		return TypedActionResult.success(user.getStackInHand(hand));
	}

}
