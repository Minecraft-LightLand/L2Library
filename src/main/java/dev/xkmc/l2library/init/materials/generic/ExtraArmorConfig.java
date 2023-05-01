package dev.xkmc.l2library.init.materials.generic;

import com.google.common.collect.Multimap;
import dev.xkmc.l2library.init.LangData;
import dev.xkmc.l2library.init.data.ArmorEffectConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.TreeMap;

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

	@SuppressWarnings("ConstantConditions")
	public void onArmorTick(ItemStack stack, Level world, Player player) {
		String name = ((ArmorItem) stack.getItem()).getMaterial().getName();
		var config = ArmorEffectConfig.get();
		if (config == null || !config.immune.containsKey(name)) return;
		var set = ArmorEffectConfig.get().immune.get(name);
		for (MobEffect e : set) {
			if (player.hasEffect(e)) {
				player.removeEffect(e);
			}
		}
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

	@SuppressWarnings("ConstantConditions")
	public void addTooltip(ItemStack stack, List<Component> list) {
		String name = ((ArmorItem) stack.getItem()).getMaterial().getName();
		var config = ArmorEffectConfig.get();
		if (config == null || !config.immune.containsKey(name)) return;
		var set = ArmorEffectConfig.get().immune.get(name);
		TreeMap<ResourceLocation, MobEffect> map = new TreeMap<>();
		for (var e : set) {
			map.put(ForgeRegistries.MOB_EFFECTS.getKey(e), e);
		}
		MutableComponent comp = LangData.ARMOR_IMMUNE.get();
		boolean comma = false;
		for (var e : map.values()) {
			if (comma) comp = comp.append(", ");
			comma = true;
			comp = comp.append(Component.translatable(e.getDescriptionId()).withStyle(e.getCategory().getTooltipFormatting()));
		}
		list.add(comp.withStyle(ChatFormatting.LIGHT_PURPLE));
	}

	public boolean hideWithEffect() {
		return false;
	}

	public boolean dampenVibration() {
		return false;
	}

	@SuppressWarnings("ConstantConditions")
	public boolean immuneToEffect(ItemStack stack, GenericArmorItem armor, MobEffectInstance effectInstance) {
		String name = armor.getMaterial().getName();
		var config = ArmorEffectConfig.get();
		if (config == null || !config.immune.containsKey(name)) return false;
		return ArmorEffectConfig.get().immune.get(name).contains(effectInstance.getEffect());
	}
}
