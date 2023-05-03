package dev.xkmc.l2library.init.data;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.events.damage.DamageTypeRoot;
import dev.xkmc.l2library.init.events.damage.DamageTypeWrapper;
import dev.xkmc.l2library.init.events.damage.DamageWrapperTagProvider;
import dev.xkmc.l2library.init.events.damage.DefaultDamageState;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class L2DamageTypes extends DamageTypeAndTagsGen {

	public static final DamageTypeRoot PLAYER_ATTACK = new DamageTypeRoot(L2Library.MODID, DamageTypes.PLAYER_ATTACK,
			List.of(), (type) -> new DamageType("player", 0.1F));

	public static final DamageTypeRoot MOB_ATTACK = new DamageTypeRoot(L2Library.MODID, DamageTypes.MOB_ATTACK,
			List.of(), (type) -> new DamageType("mob", 0.1F));

	public static final TagKey<DamageType> MATERIAL_MUX = TagKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(L2Library.MODID, "material_mux"));

	public static final TagKey<DamageType> MAGIC = TagKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation("forge", "is_magic"));

	public static final DamageTypeTagGroup BYPASS_MAGIC = DamageTypeTagGroup.of(
			DamageTypeTags.BYPASSES_ENCHANTMENTS, DamageTypeTags.BYPASSES_RESISTANCE,
			DamageTypeTags.BYPASSES_EFFECTS
	);

	public static final DamageTypeTagGroup BYPASS_INVUL = DamageTypeTagGroup.of(
			DamageTypeTags.BYPASSES_ARMOR,
			DamageTypeTags.BYPASSES_ENCHANTMENTS, DamageTypeTags.BYPASSES_RESISTANCE,
			DamageTypeTags.BYPASSES_INVULNERABILITY, DamageTypeTags.BYPASSES_EFFECTS
	);

	protected static final List<DamageTypeWrapper> LIST = new ArrayList<>();

	public static void register() {
		PLAYER_ATTACK.add(DefaultDamageState.BYPASS_ARMOR);
		PLAYER_ATTACK.add(DefaultDamageState.BYPASS_MAGIC);

		MOB_ATTACK.add(DefaultDamageState.BYPASS_ARMOR);
		MOB_ATTACK.add(DefaultDamageState.BYPASS_MAGIC);

		DamageTypeRoot.configureGeneration(Set.of(L2Library.MODID), L2Library.MODID, LIST);
	}

	public L2DamageTypes(PackOutput output,
						 CompletableFuture<HolderLookup.Provider> pvd,
						 ExistingFileHelper files) {
		super(output, pvd, files, L2Library.MODID);
	}

	@Override
	protected void addDamageTypes(BootstapContext<DamageType> ctx) {
		DamageTypeRoot.generateAll();
		for (DamageTypeWrapper wrapper : L2DamageTypes.LIST) {
			ctx.register(wrapper.type(), wrapper.getObject());
		}
	}

	@Override
	protected void addDamageTypeTags(DamageWrapperTagProvider pvd, HolderLookup.Provider lookup) {
		DamageTypeRoot.generateAll();
		for (DamageTypeWrapper wrapper : LIST) {
			wrapper.gen(pvd, lookup);
		}
		pvd.tag(MATERIAL_MUX).add(DamageTypes.PLAYER_ATTACK, DamageTypes.MOB_ATTACK);
		pvd.tag(MAGIC).add(DamageTypes.MAGIC, DamageTypes.INDIRECT_MAGIC, DamageTypes.THORNS, DamageTypes.SONIC_BOOM);
	}

}
