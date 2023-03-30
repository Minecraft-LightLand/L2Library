package dev.xkmc.l2library.init.materials.api;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

@FunctionalInterface
public interface ArmorFactory {

	ArmorItem get(IMatArmorType mat, EquipmentSlot slot, Item.Properties props);

}
