package remelon.cat.exorcism.bible;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class BibleScreenHandler {

	public static void open() {
		MinecraftClient.getInstance().setScreen(new BibleBrowserScreen());
	}
}
