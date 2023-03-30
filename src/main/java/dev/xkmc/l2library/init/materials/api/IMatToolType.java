package dev.xkmc.l2library.init.materials.api;

import dev.xkmc.l2library.init.materials.generic.ExtraToolConfig;
import net.minecraft.world.item.Tier;

public interface IMatToolType {

	Tier getTier();

	IToolStats getToolStats();

	ToolConfig getToolConfig();

	ExtraToolConfig getExtraToolConfig();

}
