package remelon.cat.exorcism;

import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.util.Rarity;
import remelon.cat.exorcism.bible.BibleItem;

public class ExorcismItems {
	public static final ItemEntry<BibleItem> BIBLE = Exorcism.EXO_REGISTRATE
			.item("bible", BibleItem::new)
			.properties(p -> p.maxCount(1))
			.properties(p -> p.maxCount(1).rarity(Rarity.UNCOMMON))
			.tab(CreativeTab.EXORCISM_TAB.key())
			.register();

	public static void register() {
	}
}
