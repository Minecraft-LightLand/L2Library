package dev.xkmc.l2library.init.materials.vanilla;

import dev.xkmc.l2library.init.materials.api.ITool;
import dev.xkmc.l2library.init.materials.api.IToolStats;

public record ToolStats(int durability, int speed, int base_damage, float base_speed, int enchant)
		implements IToolStats {

	public int getDamage(ITool tool) {
		return tool.getDamage(base_damage);
	}

	public float getSpeed(ITool tool) {
		return tool.getSpeed(base_speed);
	}

}
