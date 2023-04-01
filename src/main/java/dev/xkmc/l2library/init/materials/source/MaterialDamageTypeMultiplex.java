package dev.xkmc.l2library.init.materials.source;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.events.damage.DamageTypeGen;
import dev.xkmc.l2library.init.events.damage.DamageTypeRoot;
import dev.xkmc.l2library.init.events.damage.DamageTypeWrapper;
import dev.xkmc.l2library.init.events.damage.DefaultDamageState;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MaterialDamageTypeMultiplex extends DamageTypeTagsProvider {

	public static final DamageTypeRoot PLAYER_ATTACK = new DamageTypeRoot(DamageTypes.PLAYER_ATTACK);
	public static final DamageTypeRoot MOB_ATTACK = new DamageTypeRoot(DamageTypes.MOB_ATTACK);

	public static final TagKey<DamageType> MATERIAL_MUX = TagKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(L2Library.MODID, "material_mux"));

	private static final List<DamageTypeWrapper> LIST = new ArrayList<>();

	public static void register() {
		PLAYER_ATTACK.add(DefaultDamageState.BYPASS_ARMOR);
		PLAYER_ATTACK.add(DefaultDamageState.BYPASS_MAGIC);

		MOB_ATTACK.add(DefaultDamageState.BYPASS_ARMOR);
		MOB_ATTACK.add(DefaultDamageState.BYPASS_MAGIC);

		DamageTypeRoot.configureGeneration(Set.of(L2Library.MODID), L2Library.MODID, LIST);
	}

	public static void gatherData(GatherDataEvent event) {
		boolean gen = event.includeServer();
		PackOutput output = event.getGenerator().getPackOutput();
		var pvd = event.getLookupProvider();
		event.getGenerator().addProvider(gen, new DamageTypeGen(output, pvd, L2Library.MODID, LIST));
		event.getGenerator().addProvider(gen, new MaterialDamageTypeMultiplex(output, pvd));
	}

	public MaterialDamageTypeMultiplex(PackOutput output, CompletableFuture<HolderLookup.Provider> pvd) {
		super(output, pvd);
	}

	@Override
	protected void addTags(HolderLookup.Provider pvd) {
		DamageTypeRoot.generateAll();
		for (DamageTypeWrapper wrapper : LIST) {
			wrapper.gen(this::tag, pvd);
		}
		tag(MATERIAL_MUX).add(DamageTypes.PLAYER_ATTACK, DamageTypes.MOB_ATTACK);
	}

}
