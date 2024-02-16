package dev.xkmc.l2library.init.reg.simple;

import dev.xkmc.l2library.init.reg.datapack.DatapackRegistryInstance;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

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

	public <T> DatapackRegistryInstance<T> datapack(String id, Class<T> cls) {
		var ans = new DatapackRegistryInstance<>(ResourceKey.createRegistryKey(id(id)), cls);
		list.add(bus -> bus.addListener(ans::onRegister));
		return ans;
	}


	public void register(IEventBus bus) {
		for (var e : map.values()) e.register(bus);
		for (var e : list) e.accept(bus);
	}

	public ResourceLocation id(String id) {
		return new ResourceLocation(modid, id);
	}

}
