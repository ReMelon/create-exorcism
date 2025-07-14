package remelon.cat.exorcism;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class BibleVerseScreen extends Screen {
	private final Screen parent;
	private final int bookIndex;
	private final int chapterIndex;
	private final String bookName; // ⭐ Added field to store the book name
	private final List<OrderedText> wrappedText = new ArrayList<>();
	private int scrollOffset = 0;
	private int maxScrollOffset = 0;
	private boolean isDraggingScrollbar = false;
	private double dragStartOffsetY = 0.0;

	public BibleVerseScreen(Screen parent, int bookIndex, int chapterIndex) {
		super(Text.literal("Chapter View"));
		this.parent = parent;
		this.bookIndex = bookIndex;
		this.chapterIndex = chapterIndex;
		// ⭐ Fetched the book name in the constructor
		this.bookName = BibleDataLoader.bibleJson.getAsJsonArray("books")
				.get(bookIndex).getAsJsonObject()
				.get("name").getAsString();
	}

	@Override
	protected void init() {
		this.clearChildren();

		var books = BibleDataLoader.bibleJson.getAsJsonArray("books");
		if (bookIndex >= books.size()) return;

		var book = books.get(bookIndex).getAsJsonObject();
		var chapters = book.getAsJsonArray("chapters");
		if (chapterIndex >= chapters.size()) return;

		var chapter = chapters.get(chapterIndex).getAsJsonObject();
		var verses = chapter.getAsJsonArray("verses");
		if (verses == null || verses.size() == 0) return;

		wrappedText.clear();
		for (int i = 0; i < verses.size(); i++) {
			var verseObj = verses.get(i).getAsJsonObject();
			int num = verseObj.get("verse").getAsInt();
			String text = verseObj.get("text").getAsString();
			String line = num + ": " + text;

			var lines = textRenderer.wrapLines(Text.literal(line), width - 40);
			wrappedText.addAll(lines);

			// Extra space between verses
			wrappedText.add(textRenderer.wrapLines(Text.literal(" "), width - 40).get(0));
		}

		// ⭐ Adjusted layout for the new title (view starts at 60 instead of 40)
		int totalTextHeight = this.wrappedText.size() * 14;
		int viewHeight = this.height - 60 - 10; // Top and bottom margins
		this.maxScrollOffset = Math.max(0, totalTextHeight - viewHeight);

		addDrawableChild(ButtonWidget.builder(Text.literal("←"), b -> client.setScreen(parent))
				.position(10, 10)
				.size(20, 20)
				.build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);

		// ⭐ Render the title text
		String title = this.bookName + " " + (this.chapterIndex + 1);
		context.drawCenteredTextWithShadow(this.textRenderer, title, this.width / 2, 20, 0xFFFFFF);

		// ⭐ Adjusted view top to make space for the title
		int viewTop = 60;
		int viewBottom = this.height - 10;
		int viewHeight = viewBottom - viewTop;

		context.enableScissor(0, viewTop, this.width, viewBottom);

		int y = viewTop - scrollOffset;
		for (OrderedText line : wrappedText) {
			context.drawText(textRenderer, line, 20, y, 0xFFFFFF, false);
			y += 14;
		}

		context.disableScissor();

		if (this.maxScrollOffset > 0) {
			int totalTextHeight = wrappedText.size() * 14;
			int scrollbarHeight = Math.max(10, (int) ((float) viewHeight / totalTextHeight * viewHeight));
			int scrollbarTrackSpace = viewHeight - scrollbarHeight;
			int scrollbarY = viewTop + (int) ((float) scrollOffset / this.maxScrollOffset * scrollbarTrackSpace);
			context.fill(width - 10, viewTop, width - 6, viewBottom, 0xFF444444);
			context.fill(width - 10, scrollbarY, width - 6, scrollbarY + scrollbarHeight, 0xFFFFFFFF);
		}

		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && this.maxScrollOffset > 0) {
			int viewTop = 60; // ⭐ Adjusted coordinate
			int viewBottom = this.height - 10;
			int viewHeight = viewBottom - viewTop;
			int totalTextHeight = wrappedText.size() * 14;
			int scrollbarHeight = Math.max(10, (int) ((float) viewHeight / totalTextHeight * viewHeight));
			int scrollbarTrackSpace = viewHeight - scrollbarHeight;
			int scrollbarY = viewTop + (int) ((float) scrollOffset / this.maxScrollOffset * scrollbarTrackSpace);

			if (mouseX >= (width - 10) && mouseX < (width - 6) &&
					mouseY >= scrollbarY && mouseY < (scrollbarY + scrollbarHeight)) {
				this.isDraggingScrollbar = true;
				this.dragStartOffsetY = mouseY - scrollbarY;
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.isDraggingScrollbar && button == 0) {
			int viewTop = 60; // ⭐ Adjusted coordinate
			int viewBottom = this.height - 10;
			int viewHeight = viewBottom - viewTop;
			int totalTextHeight = wrappedText.size() * 14;
			int scrollbarHeight = Math.max(10, (int) ((float) viewHeight / totalTextHeight * viewHeight));
			int scrollbarTrackSpace = viewHeight - scrollbarHeight;
			double relativeMouseY = mouseY - viewTop - this.dragStartOffsetY;
			double scrollPercentage = relativeMouseY / scrollbarTrackSpace;
			this.scrollOffset = (int) (scrollPercentage * this.maxScrollOffset);
			this.scrollOffset = Math.max(0, Math.min(scrollOffset, this.maxScrollOffset));
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	// Unchanged methods
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			this.isDraggingScrollbar = false;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		scrollOffset -= (int) (amount * 10);
		scrollOffset = Math.max(0, Math.min(scrollOffset, this.maxScrollOffset));
		return true;
	}
}
