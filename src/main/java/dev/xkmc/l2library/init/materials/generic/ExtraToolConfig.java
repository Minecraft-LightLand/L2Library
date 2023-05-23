package dev.xkmc.l2library.init.materials.generic;

import com.google.common.collect.Multimap;
import dev.xkmc.l2library.base.effects.EffectUtil;
import dev.xkmc.l2library.init.events.attack.AttackCache;
import dev.xkmc.l2library.init.materials.api.IMatVanillaType;
import dev.xkmc.l2library.init.materials.vanilla.GenItemVanillaType;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ExtraToolConfig {

	public int tool_hit = 2, tool_mine = 1, sword_hit = 1, sword_mine = 2;
	public double repair_chance = 0, damage_chance = 1;
	public boolean canBeDepleted = true, bypassArmor, bypassMagic;
	public List<MobEffectInstance> effects = new ArrayList<>();
	public Function<IMatVanillaType, Item> stick = e -> Items.STICK;
	public boolean reversed = false;
	public Function<Integer, TagKey<Block>> tier = GenItemVanillaType::getBlockTag;

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

	public void onHit(ItemStack stack, LivingEntity target, LivingEntity user) {
		for (MobEffectInstance ins : effects) {
			EffectUtil.addEffect(target, ins, EffectUtil.AddReason.NONE, user);
		}
	}

	public ExtraToolConfig repairChance(double chance) {
		this.repair_chance = chance;
		return this;
	}

	public ExtraToolConfig damageChance(double chance) {
		this.damage_chance = chance;
		return this;
	}

	public ExtraToolConfig setUnDepletable() {
		canBeDepleted = true;
		return this;
	}

	public ExtraToolConfig addEffects(MobEffectInstance... ins) {
		effects.addAll(List.of(ins));
		return this;
	}

	public ExtraToolConfig setBypassArmor() {
		bypassArmor = true;
		return this;
	}

	public ExtraToolConfig setBypassMagic() {
		bypassMagic = true;
		return this;
	}

	public ExtraToolConfig setStick(Function<IMatVanillaType, Item> sup, boolean reverse) {
		this.stick = sup;
		this.reversed = reverse;
		return this;
	}

	public ExtraToolConfig setTier(Function<Integer, TagKey<Block>> tag) {
		tier = tag;
		return this;
	}

	public Multimap<Attribute, AttributeModifier> modify(Multimap<Attribute, AttributeModifier> map, EquipmentSlot slot, ItemStack stack) {
		return map;
	}

	public float getDestroySpeed(ItemStack stack, BlockState state, float old) {
		return old;
	}

	public TagKey<Block> getTier(int level) {
		return tier.apply(level);
	}

	public void addTooltip(ItemStack stack, List<Component> list) {
	}

	public boolean hideWithEffect() {
		return false;
	}

	public void onDamage(AttackCache cache, ItemStack stack) {

	}

}
