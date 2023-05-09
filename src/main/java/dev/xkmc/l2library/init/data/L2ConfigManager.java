package dev.xkmc.l2library.init.data;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.serial.config.BaseConfig;
import dev.xkmc.l2library.serial.config.ConfigMerger;

import java.util.Locale;

public enum L2ConfigManager {
	ARMOR;

	public String getID() {
		return name().toLowerCase(Locale.ROOT);
	}

	public <T extends BaseConfig> T getMerged() {
		return L2Library.PACKET_HANDLER.getCachedConfig(getID());
	}

	public static void register() {
		L2Library.PACKET_HANDLER.addCachedConfig(ARMOR.getID(), new ConfigMerger<>(ArmorEffectConfig.class));
	}

}
