package dev.xkmc.l2library.init.materials.api;

import dev.xkmc.l2library.init.materials.generic.ExtraArmorConfig;
import net.minecraft.world.item.ArmorMaterial;

public interface IMatArmorType {

	ArmorMaterial getArmorMaterial();

	ArmorConfig getArmorConfig();

	ExtraArmorConfig getExtraArmorConfig();

}
