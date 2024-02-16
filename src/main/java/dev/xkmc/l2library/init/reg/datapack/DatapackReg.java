package dev.xkmc.l2library.init.reg.datapack;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.List;
import java.util.stream.Stream;

public record DatapackReg<T>(ResourceKey<Registry<T>> key, Codec<T> codec) implements ValSet<ResourceLocation, T> {

	public void onRegister(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(key, codec);
	}

	public T get(ResourceLocation id) {
		return Proxy.getRegistryAccess().registry(key).get().get(id);
	}

	public Stream<Pair<ResourceLocation, T>> getAll() {
		return Proxy.getRegistryAccess().registry(key).get().entrySet()
				.stream().map(e -> Pair.of(e.getKey().location(), e.getValue()));
	}

}
