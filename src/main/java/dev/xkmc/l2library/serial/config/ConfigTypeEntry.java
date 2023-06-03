package dev.xkmc.l2library.serial.config;

import dev.xkmc.l2library.init.events.select.item.SimpleItemSelectConfig;
import net.minecraft.resources.ResourceLocation;

public record ConfigTypeEntry<T extends BaseConfig>(PacketHandlerWithConfig channel, String name, Class<T> cls) {

	public ConfigTypeEntry(PacketHandlerWithConfig channel, String name, Class<T> cls) {
		this.channel = channel;
		this.name = name;
		this.cls = cls;
		channel.addCachedConfig(name, SimpleItemSelectConfig.class);
	}

	public String asPath(ResourceLocation rl) {
		return "data/" + rl.getNamespace() + "/" + channel.config_path + "/" + name + "/" + rl.getPath();
	}

	public T getMerged() {
		return channel.getCachedConfig(name);
	}

}
