package dev.xkmc.l2library.init.materials.vanilla;

import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.init.materials.api.*;
import dev.xkmc.l2library.init.materials.generic.GenericArmorItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import java.util.Locale;
import java.util.function.BiFunction;

@SuppressWarnings({"unchecked", "rawtypes", "unsafe"})
public record GenItemVanillaType(String modid, L2Registrate registrate) {

	public static final ToolConfig TOOL_GEN = new ToolConfig(GenItemVanillaType::genGenericTool);
	public static final ArmorConfig ARMOR_GEN = new ArmorConfig((mat, slot, prop) -> new GenericArmorItem(mat.getArmorMaterial(), slot, prop, mat.getExtraArmorConfig()));

	public static Item genGenericTool(IMatToolType mat, ITool tool, Item.Properties prop) {
		int dmg = mat.getToolStats().getDamage(tool) - 1;
		float speed = mat.getToolStats().getSpeed(tool) - 4;
		return tool.create(mat.getTier(), dmg, speed, prop, mat.getExtraToolConfig());
	}

	public static TagKey<Block> getBlockTag(int level) {
		return switch (level) {
			case 0 -> Tags.Blocks.NEEDS_WOOD_TOOL;
			case 1 -> BlockTags.NEEDS_STONE_TOOL;
			case 2 -> BlockTags.NEEDS_IRON_TOOL;
			case 3 -> BlockTags.NEEDS_DIAMOND_TOOL;
			default -> Tags.Blocks.NEEDS_NETHERITE_TOOL;

		};
	}

	public ItemEntry<Item>[][] genItem(IMatVanillaType[] mats) {
		int n = mats.length;
		ItemEntry[][] ans = new ItemEntry[n][9];
		for (int i = 0; i < n; i++) {
			IMatVanillaType mat = mats[i];
			String id = mat.getID();
			BiFunction<String, ArmorItem.Type, ItemBuilder> armor_gen = (str, slot) ->
					registrate.item(id + "_" + str, p -> mat.getArmorConfig().sup().get(mat, slot, p))
							.model((ctx, pvd) -> generatedModel(ctx, pvd, id, str))
							.defaultLang();
			ans[i][3] = armor_gen.apply("helmet", ArmorItem.Type.HELMET).tag(Tags.Items.ARMORS_HELMETS).register();
			ans[i][2] = armor_gen.apply("chestplate", ArmorItem.Type.CHESTPLATE).tag(Tags.Items.ARMORS_CHESTPLATES).register();
			ans[i][1] = armor_gen.apply("leggings", ArmorItem.Type.LEGGINGS).tag(Tags.Items.ARMORS_LEGGINGS).register();
			ans[i][0] = armor_gen.apply("boots", ArmorItem.Type.BOOTS).tag(Tags.Items.ARMORS_BOOTS).register();
			BiFunction<String, Tools, ItemEntry> tool_gen = (str, tool) ->
					registrate.item(id + "_" + str, p -> mat.getToolConfig().sup().get(mat, tool, p))
							.model((ctx, pvd) -> handHeld(ctx, pvd, id, str)).tag(tool.tag)
							.defaultLang().register();
			for (int j = 0; j < Tools.values().length; j++) {
				Tools tool = Tools.values()[j];
				ans[i][4 + j] = tool_gen.apply(tool.name().toLowerCase(Locale.ROOT), tool);
			}
		}
		return ans;
	}

	public ItemEntry<Item>[] genMats(IMatVanillaType[] mats, String suffix, TagKey<Item> tag) {
		int n = mats.length;
		ItemEntry[] ans = new ItemEntry[n];
		for (int i = 0; i < n; i++) {
			String id = mats[i].getID();
			ans[i] = registrate.item(id + "_" + suffix, Item::new)
					.model((ctx, pvd) -> generatedModel(ctx, pvd, id, suffix))
					.tag(tag).defaultLang().register();
		}
		return ans;
	}

	public BlockEntry<Block>[] genBlockMats(IMatVanillaType[] mats) {
		int n = mats.length;
		BlockEntry[] ans = new BlockEntry[n];
		for (int i = 0; i < n; i++) {
			ans[i] = registrate.block(mats[i].getID() + "_block", p -> new Block(Block.Properties.copy(Blocks.IRON_BLOCK)))
					.defaultLoot().defaultBlockstate()
					.tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL, Tags.Blocks.STORAGE_BLOCKS)
					.item().tag(Tags.Items.STORAGE_BLOCKS).build().defaultLang().register();
		}
		return ans;
	}

	public <T extends Item> void generatedModel(DataGenContext<Item, T> ctx, RegistrateItemModelProvider pvd, String id, String suf) {
		pvd.generated(ctx, new ResourceLocation(modid, "item/generated/" + id + "/" + suf));
	}

	public <T extends Item> void handHeld(DataGenContext<Item, T> ctx, RegistrateItemModelProvider pvd, String id, String suf) {
		pvd.handheld(ctx, new ResourceLocation(modid, "item/generated/" + id + "/" + suf));
	}

}
