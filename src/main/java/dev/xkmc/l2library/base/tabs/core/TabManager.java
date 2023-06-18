package dev.xkmc.l2library.base.tabs.core;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2library.base.tabs.contents.BaseTextScreen;
import dev.xkmc.l2library.init.L2LibraryConfig;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TabManager {

	private final List<BaseTab<?>> list = new ArrayList<>();
	private final Screen screen;

	public int tabPage, maxPages;
	public TabToken<?> selected;

	public TabManager(Screen screen) {
		this.screen = screen;
	}

	public void init(Consumer<AbstractWidget> adder, TabToken<?> selected) {
		list.clear();
		if (!L2LibraryConfig.CLIENT.showTabs.get())
			return;
		this.selected = selected;
		int guiLeft, guiTop;
		if (screen instanceof BaseTextScreen tx) {
			guiLeft = tx.leftPos;
			guiTop = tx.topPos;
		} else if (screen instanceof AbstractContainerScreen<?> tx) {
			guiLeft = tx.getGuiLeft();
			guiTop = tx.getGuiTop();
		} else {
			guiLeft = (screen.width - 176) / 2;
			guiTop = (screen.height - 166) / 2;
		}
		guiLeft -= 56;
		for (TabToken<?> token : TabRegistry.getTabs()) {
			BaseTab<?> tab = token.create(this);
			tab.x = guiLeft + (token.index + 2) * 28;
			tab.y = guiTop - 28;
			adder.accept(tab);
			list.add(tab);
		}

		if (TabRegistry.getTabs().size() > TabType.MAX_TABS) {
			adder.accept(new Button(guiLeft, guiTop - 50, 20, 20,
					Component.literal("<"), b -> {
				tabPage = Math.max(tabPage - 1, 0);
				updateVisibility();
			}));
			adder.accept(new Button(guiLeft + 252 - 20, guiTop - 50, 20, 20,
					Component.literal(">"), b -> {
				tabPage = Math.min(tabPage + 1, maxPages);
				updateVisibility();
			}));
			maxPages = TabRegistry.getTabs().size() / TabType.MAX_TABS;
		}

		updateVisibility();
	}

	private void updateVisibility() {
		for (BaseTab<?> tab : list) {
			tab.visible = tab.token.index >= tabPage * TabType.MAX_TABS && tab.token.index < (tabPage + 1) * TabType.MAX_TABS;
			tab.active = tab.visible;
		}
	}

	public Screen getScreen() {
		return screen;
	}

	public void onToolTipRender(PoseStack stack, int mouseX, int mouseY) {
		for (BaseTab<?> tab : list) {
			if (tab.visible && tab.isHoveredOrFocused()) {
				tab.onTooltip(stack, mouseX, mouseY);
			}
		}
	}
}
