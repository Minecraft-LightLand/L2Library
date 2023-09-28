package dev.xkmc.l2library.serial.config;

import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

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
		MergedConfigType<T> type = Wrappers.cast(channel.types.get(name));
		return type.load();
	}

	public Collection<T> getAll() {
		MergedConfigType<T> type = Wrappers.cast(channel.types.get(name));
		return type.configs.values();
	}

	public T getEntry(ResourceLocation id) {
		MergedConfigType<T> type = Wrappers.cast(channel.types.get(name));
		return type.configs.get(id);
	}

}
