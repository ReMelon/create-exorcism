package remelon.cat.exorcism.bible.gui;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import remelon.cat.exorcism.bible.BibleDataLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BibleBookScreen extends Screen {
	private static final Identifier BUTTON_TEXTURE = new Identifier("exorcism", "textures/gui/button.png");

	private final Screen parent;
	private TextFieldWidget searchBox;
	private final List<BookEntry> filteredBooks = new ArrayList<>();
	private int scrollOffset = 0;
	private int maxScrollOffset = 0;
	private boolean isDraggingScrollbar = false;
	private double dragStartOffsetY = 0.0;
	private String translationName = "";


	private record BookEntry(String name, int index) {}

	public BibleBookScreen(Screen parent) {
		super(Text.literal("Bible Browser"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		addDrawableChild(ButtonWidget.builder(Text.literal("←"), b -> client.setScreen(parent))
				.position(10, 10)
				.size(20, 20)
				.build());

		searchBox = new TextFieldWidget(textRenderer, 40, 10, this.width - 55, 20, Text.literal("Search"));
		searchBox.setChangedListener(s -> this.filterBooks());
		addDrawableChild(searchBox);

		this.filterBooks();

		if (BibleDataLoader.bibleJson.has("translation")) {
			translationName = BibleDataLoader.bibleJson.get("translation").getAsString();
		}

	}

	private void filterBooks() {
		filteredBooks.clear();
		String searchText = searchBox.getText().toLowerCase(Locale.ROOT);

		var books = BibleDataLoader.bibleJson.getAsJsonArray("books");
		for (int i = 0; i < books.size(); i++) {
			JsonObject book = books.get(i).getAsJsonObject();
			String name = book.get("name").getAsString();
			if (name.toLowerCase(Locale.ROOT).contains(searchText)) {
				filteredBooks.add(new BookEntry(name, i));
			}
		}

		int viewHeight = this.height - 40 - 10;
		int totalContentHeight = this.filteredBooks.size() * 25;
		this.maxScrollOffset = Math.max(0, totalContentHeight - viewHeight);
		this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, this.maxScrollOffset));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);

		int viewTop = 40;
		int viewBottom = this.height - 10;
		int listX = this.width / 2 - 100;

		context.enableScissor(0, viewTop, this.width, viewBottom);

		int y = viewTop - scrollOffset;
		for (BookEntry book : this.filteredBooks) {
			if (y + 20 > viewTop && y < viewBottom) {
				boolean hovered = mouseX >= listX && mouseX < listX + 200 && mouseY >= y && mouseY < y + 20;

				int vOffset = hovered ? 20 : 0;
				context.drawTexture(BUTTON_TEXTURE, listX, y, 0, vOffset, 200, 20, 200, 40);
				if (!hovered) {
					context.drawCenteredTextWithShadow(this.textRenderer, book.name(), this.width / 2, y + 6, 0xFFFFFF);
				} else {
					context.drawCenteredTextWithShadow(this.textRenderer, book.name(), this.width / 2, y + 6, 0xffd83e);
				}
			}
			y += 25;
		}

		context.disableScissor();
		renderScrollbar(context);
		super.render(context, mouseX, mouseY, delta);
		context.drawTextWithShadow(this.textRenderer, translationName, 40, 35, 0xAAAAAA);
	}

	private void renderScrollbar(DrawContext context) {
		if (this.maxScrollOffset > 0) {
			int viewTop = 40;
			int viewBottom = this.height - 10;
			int viewHeight = viewBottom - viewTop;
			int totalContentHeight = this.filteredBooks.size() * 25;
			int scrollbarHeight = Math.max(10, (int) ((float) viewHeight / totalContentHeight * viewHeight));
			int scrollbarTrackSpace = viewHeight - scrollbarHeight;
			int scrollbarY = viewTop + (int) ((float) scrollOffset / this.maxScrollOffset * scrollbarTrackSpace);
			context.fill(width - 10, viewTop, width - 6, viewBottom, 0xFF444444);
			context.fill(width - 10, scrollbarY, width - 6, scrollbarY + scrollbarHeight, 0xFFFFFFFF);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && this.maxScrollOffset > 0) {
			int viewTop = 40;
			int viewBottom = this.height - 10;
			int viewHeight = viewBottom - viewTop;
			int totalContentHeight = filteredBooks.size() * 25;
			int scrollbarHeight = Math.max(10, (int) ((float) viewHeight / totalContentHeight * viewHeight));
			int scrollbarTrackSpace = viewHeight - scrollbarHeight;
			int scrollbarY = viewTop + (int) ((float) scrollOffset / this.maxScrollOffset * scrollbarTrackSpace);

			if (mouseX >= (width - 10) && mouseX < (width - 6) && mouseY >= scrollbarY && mouseY < (scrollbarY + scrollbarHeight)) {
				this.isDraggingScrollbar = true;
				this.dragStartOffsetY = mouseY - scrollbarY;
				return true;
			}
		}
		int y = 40 - scrollOffset;
		int listX = this.width / 2 - 100;
		for (BookEntry book : this.filteredBooks) {
			if (mouseX >= listX && mouseX < listX + 200 && mouseY >= y && mouseY < y + 20) {
				client.setScreen(new BibleChapterScreen(this, book.index()));
				return true;
			}
			y += 25;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		scrollOffset -= (int) (amount * 10);
		scrollOffset = Math.max(0, Math.min(scrollOffset, this.maxScrollOffset));
		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.isDraggingScrollbar && button == 0) {
			int viewTop = 40;
			int viewBottom = this.height - 10;
			int viewHeight = viewBottom - viewTop;
			int totalContentHeight = filteredBooks.size() * 25;
			int scrollbarHeight = Math.max(10, (int) ((float) viewHeight / totalContentHeight * viewHeight));
			int scrollbarTrackSpace = viewHeight - scrollbarHeight;

			double relativeMouseY = mouseY - viewTop - this.dragStartOffsetY;
			double scrollPercentage = relativeMouseY / scrollbarTrackSpace;

			this.scrollOffset = (int) (scrollPercentage * this.maxScrollOffset);
			this.scrollOffset = Math.max(0, Math.min(scrollOffset, this.maxScrollOffset));
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			this.isDraggingScrollbar = false;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void close() {
		client.setScreen(parent);
	}
}
