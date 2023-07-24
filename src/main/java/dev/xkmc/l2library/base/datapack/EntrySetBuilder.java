package dev.xkmc.l2library.base.datapack;

import dev.xkmc.l2library.base.L2Registrate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class EntrySetBuilder<T, R extends EntryHolder<T>> {

	public final L2Registrate registrate;
	public final DatapackInstance<T, R> registry;
	public final List<DatapackEntry<T, R>> list = new ArrayList<>();

	EntrySetBuilder(L2Registrate registrate, DatapackInstance<T, R> registry) {
		this.registrate = registrate;
		this.registry = registry;
	}

	public DatapackEntry<T, R> reg(ResourceLocation id, Supplier<T> sup, String name) {
		var ans = new DatapackEntry<>(this, registry.entryKey(id), sup);
		list.add(ans);
		registrate.addRawLang(registry.getEntryDescID(id), name);
		return ans;
	}

	private void createEntries(BootstapContext<T> ctx) {
		for (var e : list) {
			ctx.register(e.key(), e.sup().get());
		}
	}

	public DatapackBuiltinEntriesProvider generate(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, Set<String> ids) {
		return new DatapackBuiltinEntriesProvider(output, registries,
				new RegistrySetBuilder().add(registry.key, this::createEntries), ids);
	}

}
