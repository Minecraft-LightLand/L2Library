package dev.xkmc.l2library.init.data;

import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import dev.xkmc.l2library.init.L2Library;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class L2TagGen {

	public static final TagKey<Item> SELECTABLE = ItemTags.create(new ResourceLocation(L2Library.MODID, "selectable"));

	public static void genItemTags(RegistrateItemTagsProvider pvd) {
	}

}
