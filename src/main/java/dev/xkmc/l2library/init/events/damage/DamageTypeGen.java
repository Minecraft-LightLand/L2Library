package dev.xkmc.l2library.init.events.damage;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DamageTypeGen extends DatapackBuiltinEntriesProvider {

	public DamageTypeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid, List<DamageTypeWrapper> list) {
		super(output, registries, new RegistrySetBuilder().add(Registries.DAMAGE_TYPE, ctx -> {
			DamageTypeRoot.generateAll();
			for (DamageTypeWrapper wrapper : list) {
				// TODO copy stats
				ctx.register(wrapper.type(), new DamageType("msg", 0));
			}
		}), Set.of(modid));
	}
}
