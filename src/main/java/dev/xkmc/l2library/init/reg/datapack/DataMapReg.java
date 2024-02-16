package dev.xkmc.l2library.init.reg.datapack;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2library.util.Proxy;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public record DataMapReg<K, V>(DataMapType<K, V> reg) implements ValSet<K, V> {

	public void register(final RegisterDataMapTypesEvent event) {
		event.register(reg);
	}

	@Nullable
	public V get(K key) {
		var registry = Proxy.getRegistryAccess().registry(reg.registryKey()).get();
		return registry.getData(reg, registry.getResourceKey(key).get());
	}

	public Stream<Pair<K, V>> getAll() {
		var registry = Proxy.getRegistryAccess().registry(reg.registryKey()).get();
		return registry.getDataMap(reg).entrySet().stream()
				.map(e -> Pair.of(registry.get(e.getKey()), e.getValue()));
	}

}
