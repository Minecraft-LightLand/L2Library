package dev.xkmc.l2library.init.events.damage;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.Comparator;
import java.util.TreeSet;

public final class DamageTypeVariant implements DamageTypeWrapper {

	private final ResourceKey<DamageType> type;
	private final DamageTypeRoot root;
	private final int key;

	public DamageTypeVariant(String modid, DamageTypeRoot root, int key, TreeSet<DamageState> set) {
		this.root = root;
		this.key = key;
		StringBuilder name = new StringBuilder(root.type().location().getPath());
		for (DamageState state : set) {
			name.append("-").append(state.getId().getPath());
		}
		this.type = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(modid, name.toString()));
	}

	public ResourceKey<DamageType> type() {
		return type;
	}

	@Override
	public boolean validState(DamageState state) {
		return root.validState(state);
	}

	@Override
	public boolean isEnabled(DamageState state) {
		return root.isEnabled(key, state);
	}

	@Override
	public DamageTypeWrapper enable(DamageState state) {
		return root.get(key, state);
	}

	public DamageTypeRoot root() {
		return root;
	}

	@Override
	public void gen(DamageWrapperTagProvider gen, HolderLookup.Provider pvd) {
		TreeSet<TagKey<DamageType>> tags = new TreeSet<>(Comparator.comparing(TagKey::location));
		for (DamageState state : root.states) {
			if (isEnabled(state)) {
				state.gatherTags(tags::add);
			}
		}
		for (TagKey<DamageType> tag : tags) {
			gen.tag(tag).add(type);
		}
	}

	@Override
	public DamageType getObject() {
		return root().sup.apply(this);
	}
}
