package dev.xkmc.l2library.init.data;

import dev.xkmc.l2library.init.events.damage.DamageWrapperTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.DataPackRegistriesHooks;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class DamageTypeAndTagsGen {

	public record DamageTypeTagGroup(TagKey<DamageType>... tags) {

		@SafeVarargs
		public DamageTypeTagGroup {
		}

	}

	public class DamageTypeHolder {

		private final ResourceKey<DamageType> key;
		private final DamageType value;
		private final Set<TagKey<DamageType>> tags = new HashSet<>();

		public DamageTypeHolder(ResourceKey<DamageType> key, DamageType value) {
			this.key = key;
			this.value = value;
			holders.add(this);
		}

		@SafeVarargs
		public final DamageTypeHolder add(TagKey<DamageType>... tags) {
			this.tags.addAll(Arrays.asList(tags));
			return this;
		}

		public final DamageTypeHolder add(DamageTypeTagGroup group) {
			add(group.tags());
			return this;
		}

	}

	private final PackOutput output;
	private final CompletableFuture<HolderLookup.Provider> pvd;
	private final ExistingFileHelper helper;
	private final String modid;
	private final List<DamageTypeHolder> holders = new ArrayList<>();

	public DamageTypeAndTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> pvd, ExistingFileHelper helper, String modid) {
		this.output = output;
		this.pvd = pvd;
		this.helper = helper;
		this.modid = modid;
	}

	public void generate(boolean gen, DataGenerator generator) {
		generator.addProvider(gen, new DamageTypeGen());
		generator.addProvider(gen, new DamageTypeTagsGen());
	}

	protected void addDamageTypes(BootstapContext<DamageType> ctx) {
		for (var e : holders) {
			ctx.register(e.key, e.value);
		}
	}

	protected void addDamageTypeTags(DamageWrapperTagProvider pvd, HolderLookup.Provider lookup) {
		for (var e : holders) {
			for (var t : e.tags) {
				pvd.tag(t).add(e.key);
			}
		}
	}

	private class DamageTypeGen extends DatapackBuiltinEntriesProvider {

		public DamageTypeGen() {
			super(output, pvd, new RegistrySetBuilder().add(Registries.DAMAGE_TYPE,
					DamageTypeAndTagsGen.this::addDamageTypes), Set.of(modid));
		}

	}

	private HolderLookup.Provider constructRegistries(HolderLookup.Provider original, RegistrySetBuilder datapackEntriesBuilder) {
		var builderKeys = new HashSet<>(datapackEntriesBuilder.getEntryKeys());
		DataPackRegistriesHooks.getDataPackRegistriesWithDimensions()
				.filter(data -> !builderKeys.contains(data.key()))
				.forEach(data -> datapackEntriesBuilder.add(data.key(), context -> {
				}));
		return datapackEntriesBuilder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), original);
	}

	private class DamageTypeTagsGen extends TagsProvider<DamageType> {

		public DamageTypeTagsGen() {
			super(output, Registries.DAMAGE_TYPE, pvd.thenApply(e -> constructRegistries(e,
					new RegistrySetBuilder().add(Registries.DAMAGE_TYPE,
							ctx -> {
								DamageTypes.bootstrap(ctx);
								addDamageTypes(ctx);
							}))), modid, helper);
		}

		@Override
		protected void addTags(HolderLookup.Provider lookup) {
			addDamageTypeTags(this::tag, lookup);
		}

	}


}
