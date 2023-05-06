package dev.xkmc.l2library.init.data;

import dev.xkmc.l2library.serial.config.BaseConfig;
import dev.xkmc.l2library.serial.config.CollectType;
import dev.xkmc.l2library.serial.config.ConfigCollect;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.effect.MobEffect;

import java.util.HashMap;
import java.util.HashSet;

@SerialClass
public class ArmorEffectConfig extends BaseConfig {

	public static ArmorEffectConfig get() {
		return L2ConfigManager.ARMOR.getMerged();
	}

	@SerialClass.SerialField
	@ConfigCollect(CollectType.MAP_COLLECT)
	public HashMap<String, HashSet<MobEffect>> immune = new HashMap<>();

}
