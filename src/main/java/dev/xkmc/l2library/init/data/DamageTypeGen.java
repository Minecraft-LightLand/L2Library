package dev.xkmc.l2library.init.data;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.events.damage.DamageTypeRoot;
import dev.xkmc.l2library.init.events.damage.DamageTypeWrapper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DamageTypeGen extends DatapackBuiltinEntriesProvider {

	public DamageTypeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, new RegistrySetBuilder()
				.add(Registries.DAMAGE_TYPE, ctx -> {
					DamageTypeRoot.generateAll();
					for (DamageTypeWrapper wrapper : MaterialDamageTypeMultiplex.LIST) {
						ctx.register(wrapper.type(), wrapper.getObject());
					}
				}), Set.of(L2Library.MODID));
	}
}
