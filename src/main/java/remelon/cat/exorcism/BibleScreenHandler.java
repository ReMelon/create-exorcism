package remelon.cat.exorcism;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

// BibleScreenHandler.java (Client-only)
@Environment(EnvType.CLIENT)
public class BibleScreenHandler {

	public static void open() {
		MinecraftClient.getInstance().setScreen(new BibleBrowserScreen());
	}
}
