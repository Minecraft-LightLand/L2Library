package dev.xkmc.l2library.init.data;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.events.damage.DamageTypeRoot;
import dev.xkmc.l2library.init.events.damage.DamageTypeWrapper;
import dev.xkmc.l2library.init.events.damage.DefaultDamageState;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MaterialDamageTypeMultiplex extends TagsProvider<DamageType> {

	public static final DamageTypeRoot PLAYER_ATTACK = new DamageTypeRoot(DamageTypes.PLAYER_ATTACK,
			(type) -> new DamageType("player", 0.1F));

	public static final DamageTypeRoot MOB_ATTACK = new DamageTypeRoot(DamageTypes.MOB_ATTACK,
			(type) -> new DamageType("mob", 0.1F));

	public static final TagKey<DamageType> MATERIAL_MUX = TagKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(L2Library.MODID, "material_mux"));

	public static final TagKey<DamageType> MAGIC = TagKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation("forge", "is_magic"));

	protected static final List<DamageTypeWrapper> LIST = new ArrayList<>();

	public static void register() {
		PLAYER_ATTACK.add(DefaultDamageState.BYPASS_ARMOR);
		PLAYER_ATTACK.add(DefaultDamageState.BYPASS_MAGIC);

		MOB_ATTACK.add(DefaultDamageState.BYPASS_ARMOR);
		MOB_ATTACK.add(DefaultDamageState.BYPASS_MAGIC);

		DamageTypeRoot.configureGeneration(Set.of(L2Library.MODID), L2Library.MODID, LIST);
	}

	public MaterialDamageTypeMultiplex(PackOutput output,
									   CompletableFuture<HolderLookup.Provider> pvd,
									   @Nullable ExistingFileHelper files) {
		super(output, Registries.DAMAGE_TYPE, pvd, L2Library.MODID, files);
	}

	@Override
	protected void addTags(HolderLookup.Provider pvd) {
		DamageTypeRoot.generateAll();
		for (DamageTypeWrapper wrapper : LIST) {
			wrapper.gen(this::tag, pvd);
		}
		tag(MATERIAL_MUX).add(DamageTypes.PLAYER_ATTACK, DamageTypes.MOB_ATTACK);
		tag(MAGIC).add(DamageTypes.MAGIC, DamageTypes.INDIRECT_MAGIC, DamageTypes.THORNS, DamageTypes.SONIC_BOOM);
	}

}
