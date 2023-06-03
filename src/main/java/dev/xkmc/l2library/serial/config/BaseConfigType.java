package dev.xkmc.l2library.serial.config;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BaseConfigType<T extends BaseConfig> {

	public final Class<T> cls;
	public final String id;
	public final PacketHandlerWithConfig parent;

	final Map<ResourceLocation, T> configs = new HashMap<>();

	protected BaseConfigType(PacketHandlerWithConfig parent, String id, Class<T> cls) {
		this.parent = parent;
		this.id = id;
		this.cls = cls;
	}

	public void beforeReload() {
		configs.clear();
	}

	public void afterReload() {
	}

}
