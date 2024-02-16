package dev.xkmc.l2library.init.reg.datapack;

import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public record DatapackRegistryInstance<T>(ResourceKey<Registry<T>> key, Class<T> cls) {

	public void onRegister(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(key, new CodecAdaptor<>(cls));
	}

	public T getEntry(ResourceLocation id) {
		return Proxy.getRegistryAccess().registry(key).get().get(id);
	}

}
