package dev.xkmc.l2library.init.materials.generic;

import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExtraArmorConfig {

	public double repair_chance = 0, damage_chance = 1;

	public int magic_immune = 0;

	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity) {
		double raw = amount * damage_chance;
		int floor = (int) Math.floor(raw);
		double rem = raw - floor;
		return floor + (entity.level.random.nextDouble() < rem ? 1 : 0);
	}

	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (stack.isDamaged() && repair_chance > level.getRandom().nextDouble()) {
			stack.setDamageValue(stack.getDamageValue() - 1);
		}
	}

	public int getMagicImmune() {
		return magic_immune;
	}

	public Multimap<Attribute, AttributeModifier> modify(Multimap<Attribute, AttributeModifier> map, EquipmentSlot slot, ItemStack stack) {
		return map;
	}

	public void onArmorTick(ItemStack stack, Level world, Player player) {
	}

	public ExtraArmorConfig repairChance(double chance) {
		this.repair_chance = chance;
		return this;
	}

	public ExtraArmorConfig damageChance(double chance) {
		this.damage_chance = chance;
		return this;
	}

	public ExtraArmorConfig setMagicImmune(int percent) {
		magic_immune = percent;
		return this;
	}

	public void addTooltip(ItemStack stack, List<Component> list) {
	}

	public boolean hideWithEffect() {
		return false;
	}

	public boolean dampenVibration() {
		return false;
	}

}
