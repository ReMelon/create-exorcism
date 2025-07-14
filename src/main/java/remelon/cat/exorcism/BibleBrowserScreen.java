package remelon.cat.exorcism;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

// BibleBrowserScreen.java
@Environment(EnvType.CLIENT)
public class BibleBrowserScreen extends Screen {

	private ScreenState currentState = ScreenState.BOOK_LIST;
	private String currentBook = "";
	private int currentChapter = 0;

	protected BibleBrowserScreen() {
		super(Text.of("Bible"));
	}

	@Override
	protected void init() {
		clearChildren();

		if (currentState == ScreenState.BOOK_LIST) {
			List<JsonElement> books = BibleDataLoader.bibleJson.getAsJsonArray("books").asList();
			for (int i = 0; i < books.size(); i++) {
				JsonObject book = books.get(i).getAsJsonObject();
				addDrawableChild(ButtonWidget.builder(Text.of(book.get("name").getAsString()), btn -> {
					currentBook = book.get("name").getAsString();
					currentState = ScreenState.CHAPTER_VIEW;
					init();
				}).dimensions(10, 20 + i * 25, 120, 20).build());
			}
		} else if (currentState == ScreenState.CHAPTER_VIEW) {
			JsonArray books = BibleDataLoader.bibleJson.getAsJsonArray("books");
			JsonObject book = null;
			for (JsonElement e : books) {
				JsonObject obj = e.getAsJsonObject();
				if (obj.get("name").getAsString().equals(currentBook)) {
					book = obj;
					break;
				}
			}

			if (book != null) {
				JsonArray chapters = book.getAsJsonArray("chapters");
				for (int i = 0; i < chapters.size(); i++) {
					final int chapterIndex = i;
					addDrawableChild(ButtonWidget.builder(Text.of("Chapter " + (i + 1)), btn -> {
						currentChapter = chapterIndex;
						currentState = ScreenState.VERSE_VIEW;
						init();
					}).dimensions(10, 20 + i * 25, 120, 20).build());
				}
			}

			addDrawableChild(ButtonWidget.builder(Text.of("← Back"), btn -> {
				currentState = ScreenState.BOOK_LIST;
				init();
			}).dimensions(140, height - 30, 60, 20).build());
		} else if (currentState == ScreenState.VERSE_VIEW) {
			JsonObject book = BibleDataLoader.bibleJson.getAsJsonArray("books").asList().stream()
					.map(JsonElement::getAsJsonObject)
					.filter(obj -> obj.get("name").getAsString().equals(currentBook))
					.findFirst().orElse(null);

			if (book != null) {
				JsonArray verses = book.getAsJsonArray("chapters")
						.get(currentChapter).getAsJsonObject()
						.getAsJsonArray("verses");

				for (int i = 0; i < verses.size(); i++) {
					String text = verses.get(i).getAsJsonObject().get("text").getAsString();
					addDrawableChild(ButtonWidget.builder(Text.of((i + 1) + ": " + text), btn -> {})
							.dimensions(10, 20 + i * 25, width - 20, 20).build());
				}
			}

			addDrawableChild(ButtonWidget.builder(Text.of("← Back"), btn -> {
				currentState = ScreenState.CHAPTER_VIEW;
				init();
			}).dimensions(10, height - 30, 60, 20).build());
		}
	}

	private enum ScreenState {
		BOOK_LIST, CHAPTER_VIEW, VERSE_VIEW
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
