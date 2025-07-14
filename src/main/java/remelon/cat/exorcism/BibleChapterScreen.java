package remelon.cat.exorcism;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BibleChapterScreen extends Screen {
	private static final Identifier BUTTON_TEXTURE = new Identifier("exorcism", "textures/gui/button.png");

	private final Screen parent;
	private final int bookIndex;
	private final String bookName;
	private final List<Integer> chapters = new ArrayList<>();
	private int scrollOffset = 0;
	private int maxScrollOffset = 0;
	private boolean isDraggingScrollbar = false;
	private double dragStartOffsetY = 0.0;
	private TextFieldWidget searchBox;
	private List<Integer> filteredChapters = new ArrayList<>();

	public BibleChapterScreen(Screen parent, int bookIndex) {
		super(Text.literal("Select Chapter"));
		this.parent = parent;
		this.bookIndex = bookIndex;
		this.bookName = BibleDataLoader.bibleJson.getAsJsonArray("books")
				.get(bookIndex).getAsJsonObject()
				.get("name").getAsString();
	}

	@Override
	protected void init() {
		addDrawableChild(ButtonWidget.builder(Text.literal("←"), b -> client.setScreen(parent))
				.position(10, 10)
				.size(20, 20)
				.build());

		int chapterCount = BibleDataLoader.bibleJson.getAsJsonArray("books")
				.get(bookIndex).getAsJsonObject()
				.getAsJsonArray("chapters").size();
		chapters.clear();
		for (int i = 0; i < chapterCount; i++) {
			chapters.add(i);
		}

		searchBox = new TextFieldWidget(textRenderer, 40, 10, this.width - 55, 20, Text.literal("Search"));
		this.setInitialFocus(searchBox);
		searchBox.setChangedListener(s -> updateScroll());
		addDrawableChild(searchBox);

		updateScroll();
	}

	private void updateScroll() {
		String query = searchBox.getText().toLowerCase(Locale.ROOT);
		filteredChapters.clear();
		for (int index : chapters) {
			String name = "Chapter " + (index + 1);
			if (name.toLowerCase(Locale.ROOT).contains(query)) {
				filteredChapters.add(index);
			}
		}

		int viewHeight = this.height - 70 - 30;
		int totalContentHeight = filteredChapters.size() * 25;
		this.maxScrollOffset = Math.max(0, totalContentHeight - viewHeight);
		this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, this.maxScrollOffset));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		super.render(context, mouseX, mouseY, delta);

		int viewTop = 55	 ;
		int viewBottom = this.height - 30;
		int listX = this.width / 2 - 100;

		context.enableScissor(0, viewTop, this.width, viewBottom);

		int y = viewTop + 20 - scrollOffset;
		for (Integer chapterIndex : filteredChapters) {
			if (y + 20 > viewTop && y < viewBottom) {
				String name = "Chapter " + (chapterIndex + 1);
				boolean hovered = mouseX >= listX && mouseX < listX + 200 && mouseY >= y && mouseY < y + 20;
				int vOffset = hovered ? 20 : 0;
				context.drawTexture(BUTTON_TEXTURE, listX, y, 0, vOffset, 200, 20, 200, 40);
				if (!hovered) {
					context.drawCenteredTextWithShadow(this.textRenderer, name, this.width / 2, y + 6, 0xFFFFFF);
				} else {
					context.drawCenteredTextWithShadow(this.textRenderer, name, this.width / 2, y + 6, 0xffd83e);
				}
			}
			y += 25;
		}

		context.disableScissor();
		context.drawCenteredTextWithShadow(this.textRenderer, this.bookName, this.width / 2, 40, 0xFFFFFF);
		renderScrollbar(context);
	}

	private void renderScrollbar(DrawContext context) {
		if (this.maxScrollOffset > 0) {
			int viewTop = 60;
			int viewBottom = this.height - 30;
			int viewHeight = viewBottom - viewTop;
			int totalContentHeight = filteredChapters.size() * 25;
			int scrollbarHeight = Math.max(10, (int) ((float) viewHeight / totalContentHeight * viewHeight));
			int scrollbarTrackSpace = viewHeight - scrollbarHeight;
			int scrollbarY = viewTop + (int) ((float) scrollOffset / this.maxScrollOffset * scrollbarTrackSpace);
			context.fill(width - 10, viewTop, width - 6, viewBottom, 0xFF444444);
			context.fill(width - 10, scrollbarY, width - 6, scrollbarY + scrollbarHeight, 0xFFFFFFFF);
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		scrollOffset -= (int) (amount * 10);
		scrollOffset = Math.max(0, Math.min(scrollOffset, this.maxScrollOffset));
		return true;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (searchBox.mouseClicked(mouseX, mouseY, button)) return true;

		if (button == 0 && this.maxScrollOffset > 0) {
			int viewTop = 60;
			int viewBottom = this.height - 30;
			int viewHeight = viewBottom - viewTop;
			int totalContentHeight = filteredChapters.size() * 25;
			int scrollbarHeight = Math.max(10, (int) ((float) viewHeight / totalContentHeight * viewHeight));
			int scrollbarTrackSpace = viewHeight - scrollbarHeight;
			int scrollbarY = viewTop + (int) ((float) scrollOffset / this.maxScrollOffset * scrollbarTrackSpace);

			if (mouseX >= (width - 10) && mouseX < (width - 6) && mouseY >= scrollbarY && mouseY < (scrollbarY + scrollbarHeight)) {
				this.isDraggingScrollbar = true;
				this.dragStartOffsetY = mouseY - scrollbarY;
				return true;
			}
		}
		int y = 60 + 20 - scrollOffset;
		int listX = this.width / 2 - 100;
		for (Integer chapterIndex : filteredChapters) {
			if (mouseX >= listX && mouseX < listX + 200 && mouseY >= y && mouseY < y + 20) {
				client.setScreen(new BibleVerseScreen(this, this.bookIndex, chapterIndex));
				return true;
			}
			y += 25;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.isDraggingScrollbar && button == 0) {
			int viewTop = 60;
			int viewBottom = this.height - 30;
			int viewHeight = viewBottom - viewTop;
			int totalContentHeight = filteredChapters.size() * 25;
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
}
