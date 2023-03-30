package dev.xkmc.l2library.init.materials.vanilla;

import dev.xkmc.l2library.init.materials.api.IMatToolType;
import dev.xkmc.l2library.init.materials.api.ITool;
import dev.xkmc.l2library.init.materials.api.IToolStats;
import dev.xkmc.l2library.init.materials.api.ToolConfig;
import dev.xkmc.l2library.init.materials.generic.ExtraToolConfig;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public record VanillaMats(Tiers tier) implements IMatToolType, IToolStats {

	@Override
	public int durability() {
		return tier.getUses();
	}

	@Override
	public int speed() {
		return Math.round(tier.getSpeed());
	}

	@Override
	public int enchant() {
		return tier.getEnchantmentValue();
	}

	@Override
	public int getDamage(ITool tool) {
		return tool.getDamage(Math.round(tier.getAttackDamageBonus()) + 4);
	}

	@Override
	public float getSpeed(ITool tool) {
		return tool.getSpeed(1);
	}

	@Override
	public Tier getTier() {
		return tier;
	}

	@Override
	public IToolStats getToolStats() {
		return this;
	}

	@Override
	public ToolConfig getToolConfig() {
		return GenItemVanillaType.TOOL_GEN;
	}

	@Override
	public ExtraToolConfig getExtraToolConfig() {
		return new ExtraToolConfig();
	}

}
