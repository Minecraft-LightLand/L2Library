package dev.xkmc.l2library.base.datapack;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public interface EntryHolder<T> {

	Holder.Reference<T> holder();

	default ResourceLocation getID() {
		return holder().key().location();
	}

	default T get() {
		return holder().get();
	}

	default MutableComponent getDesc() {
		return Component.translatable(holder().key().registry().getPath() + "." + getID().getNamespace() + "." + getID().getPath());
	}

}
