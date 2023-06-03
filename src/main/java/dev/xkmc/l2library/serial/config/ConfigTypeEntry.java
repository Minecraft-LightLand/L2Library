package dev.xkmc.l2library.serial.config;

import net.minecraft.resources.ResourceLocation;

public record ConfigTypeEntry<T extends BaseConfig>(PacketHandlerWithConfig channel, String name, Class<T> cls) {

	public ConfigTypeEntry(PacketHandlerWithConfig channel, String name, Class<T> cls) {
		this.channel = channel;
		this.name = name;
		this.cls = cls;
		channel.addCachedConfig(name, cls);
	}

	public String asPath(ResourceLocation rl) {
		return "data/" + rl.getNamespace() + "/" + channel.config_path + "/" + name + "/" + rl.getPath();
	}

	public T getMerged() {
		return channel.getCachedConfig(name);
	}

}
