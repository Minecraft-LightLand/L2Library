package dev.xkmc.l2library.init.reg.simple;

import com.mojang.serialization.Codec;
import dev.xkmc.l2library.init.reg.datapack.DataMapReg;
import dev.xkmc.l2library.init.reg.datapack.DatapackReg;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class Reg {

	private final String modid;

	private final Map<Registry<?>, DeferredRegister<?>> map = new LinkedHashMap<>();
	private final List<Consumer<IEventBus>> list = new ArrayList<>();

	public Reg(String modid) {
		this.modid = modid;
	}

	public <T> DeferredRegister<T> make(Registry<T> reg) {
		return Wrappers.cast(map.computeIfAbsent(reg, r -> DeferredRegister.create(r, modid)));
	}

	public <T> DatapackReg<T> dataReg(String id, Codec<T> codec) {
		var ans = new DatapackReg<>(ResourceKey.createRegistryKey(id(id)), codec);
		list.add(bus -> bus.addListener(ans::onRegister));
		return ans;
	}

	public <T> DatapackReg<T> dataReg(String id, Class<T> cls) {
		return dataReg(id, new CodecAdaptor<>(cls));
	}

	public <K, V> DataMapReg<K, V> dataMap(DataMapType<K, V> type) {
		var ans = new DataMapReg<>(type);
		list.add(bus -> bus.addListener(ans::register));
		return ans;
	}

	public <K, V> DataMapReg<K, V> dataMap(String id, ResourceKey<Registry<K>> k, Codec<V> codec, Codec<V> network) {
		return dataMap(DataMapType.builder(id(id), k, codec).synced(network, true).build());
	}

	public <K, V> DataMapReg<K, V> dataMap(String id, ResourceKey<Registry<K>> k, Class<V> cls) {
		Codec<V> codec = new CodecAdaptor<>(cls);
		return dataMap(id, k, codec, codec);
	}

	public void register(IEventBus bus) {
		for (var e : map.values()) e.register(bus);
		for (var e : list) e.accept(bus);
	}

	public ResourceLocation id(String id) {
		return new ResourceLocation(modid, id);
	}

}
