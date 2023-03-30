package dev.xkmc.l2library.init.materials.api;

import net.minecraft.world.item.Item;

@FunctionalInterface
public interface ToolFactory {

	Item get(IMatToolType mat, ITool tool, Item.Properties props);

}
