package dev.xkmc.l2library.menu.tabs.core;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class TabToken<T extends BaseTab<T>> {

	public interface TabFactory<T extends BaseTab<T>> {

		T create(TabToken<T> token, TabManager manager, ItemStack stack, Component component);

	}

	public final int index;
	public final TabFactory<T> factory;
	public final TabType type;

	private final Supplier<Item> item;
	private final Component title;

	public TabToken(int index, TabFactory<T> factory, Supplier<Item> item, Component component) {
		this.index = index;
		this.factory = factory;
		this.type = TabType.ABOVE;

		this.item = item;
		this.title = component;
	}

	public T create(TabManager manager) {
		return factory.create(this, manager, item.get().getDefaultInstance(), title);
	}
}
