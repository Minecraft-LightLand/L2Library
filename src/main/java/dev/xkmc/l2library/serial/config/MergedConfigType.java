package dev.xkmc.l2library.serial.config;

import net.minecraft.resources.ResourceLocation;

public class MergedConfigType<T extends BaseConfig> extends BaseConfigType<T> {

	private T result;

	MergedConfigType(PacketHandlerWithConfig parent, String id, Class<T> cls) {
		super(parent, id, cls);
	}

	T load() {
		if (result != null) {
			return result;
		}
		result = new ConfigMerger<>(cls).apply(configs.values());
		result.id = new ResourceLocation(parent.CHANNEL_NAME.getNamespace(), id);
		return result;
	}

	@Override
	public void afterReload() {
		result = null;
	}

}
