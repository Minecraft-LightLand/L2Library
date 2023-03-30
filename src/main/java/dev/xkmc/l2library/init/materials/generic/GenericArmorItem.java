package dev.xkmc.l2library.init.materials.generic;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class GenericArmorItem extends ArmorItem {

	private final ExtraArmorConfig config;

	public GenericArmorItem(ArmorMaterial material, ArmorItem.Type slot, Properties prop, ExtraArmorConfig config) {
		super(material, slot, prop);
		this.config = config;
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		return config.damageItem(stack, amount, entity);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		config.inventoryTick(stack, level, entity, slot, selected);
	}

	@Override
	public void onArmorTick(ItemStack stack, Level world, Player player) {
		config.onArmorTick(stack, world, player);
	}

	public ExtraArmorConfig getConfig() {
		return config;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		var parent = super.getAttributeModifiers(slot, stack);
		if (slot != this.type.getSlot()) return parent;
		Multimap<Attribute, AttributeModifier> cur = HashMultimap.create();
		cur.putAll(parent);
		return config.modify(cur, slot, stack);
	}

	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		config.addTooltip(pStack, pTooltipComponents);
	}

}
