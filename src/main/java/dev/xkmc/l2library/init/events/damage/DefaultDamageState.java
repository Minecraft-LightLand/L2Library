package dev.xkmc.l2library.init.events.damage;

import dev.xkmc.l2library.init.L2Library;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.Locale;
import java.util.function.Consumer;

public enum DefaultDamageState implements DamageState {
	BYPASS_ARMOR(DamageTypeTags.BYPASSES_ARMOR),
	BYPASS_MAGIC(DamageTypeTags.BYPASSES_EFFECTS, DamageTypeTags.BYPASSES_ENCHANTMENTS);

	private final TagKey<DamageType>[] tags;

	@SafeVarargs
	DefaultDamageState(TagKey<DamageType>... tags) {
		this.tags = tags;
	}

	@Override
	public void gatherTags(Consumer<TagKey<DamageType>> collector) {
		for (var tag : tags) {
			collector.accept(tag);
		}
	}

	@Override
	public ResourceLocation getId() {
		return new ResourceLocation(L2Library.MODID, name().toLowerCase(Locale.ROOT));
	}

}
