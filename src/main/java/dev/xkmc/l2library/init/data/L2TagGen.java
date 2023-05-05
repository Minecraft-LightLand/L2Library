package dev.xkmc.l2library.init.data;

import dev.xkmc.l2library.init.L2Library;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class L2TagGen {

	public static final TagKey<Item> SELECTABLE = ItemTags.create(new ResourceLocation(L2Library.MODID, "selectable"));

}
