package dev.xkmc.l2library.init.materials.vanilla;

import dev.xkmc.l2library.init.materials.generic.ExtraToolConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

@FunctionalInterface
public interface RawToolFactory {

	Item get(Tier tier, int dmg, float speed, Item.Properties props, ExtraToolConfig config);
}
