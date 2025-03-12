package dev.xkmc.l2library.init.data;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.l2library.init.L2Library;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

public class L2TagGen {

	public static final ProviderType<RegistrateTagsProvider.IntrinsicImpl<Enchantment>> ENCH_TAGS =
			ProviderType.register("tags/enchantment",
					type -> (p, e) -> new RegistrateTagsProvider.IntrinsicImpl<>(p, type, "enchantments",
							e.getGenerator().getPackOutput(), Registries.ENCHANTMENT, e.getLookupProvider(),
							ench -> ResourceKey.create(ForgeRegistries.ENCHANTMENTS.getRegistryKey(),
									ForgeRegistries.ENCHANTMENTS.getKey(ench)),
							e.getExistingFileHelper()));

	public static final ProviderType<RegistrateTagsProvider.IntrinsicImpl<MobEffect>> EFF_TAGS =
			ProviderType.register("tags/mob_effect",
					type -> (p, e) -> new RegistrateTagsProvider.IntrinsicImpl<>(p, type, "mob_effects",
							e.getGenerator().getPackOutput(), Registries.MOB_EFFECT, e.getLookupProvider(),
							ench -> ResourceKey.create(ForgeRegistries.MOB_EFFECTS.getRegistryKey(),
									ForgeRegistries.MOB_EFFECTS.getKey(ench)),
							e.getExistingFileHelper()));


	public static final TagKey<Item> SMITHING_TEMPLATE = ItemTags.create(new ResourceLocation(L2Library.MODID, "dummy_smithing_template"));
	public static final TagKey<MobEffect> TRACKED_EFFECTS = effectTag(new ResourceLocation(L2Library.MODID, "tracked_effects"));

	public static void onItemTagGen(RegistrateItemTagsProvider pvd) {
		pvd.addTag(SMITHING_TEMPLATE).add(Items.PAPER);
	}

	public static void onEffectTagGen(RegistrateTagsProvider.IntrinsicImpl<MobEffect> pvd) {
		pvd.addTag(TRACKED_EFFECTS);
	}

	public static TagKey<MobEffect> effectTag(ResourceLocation id) {
		return TagKey.create(ForgeRegistries.MOB_EFFECTS.getRegistryKey(), id);
	}

}
