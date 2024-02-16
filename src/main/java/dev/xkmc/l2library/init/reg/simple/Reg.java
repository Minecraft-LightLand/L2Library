package dev.xkmc.l2library.init.reg.simple;

import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public final class Reg {

	private final String modid;

	private final List<DeferredRegister<?>> list = new ArrayList<>();

	public Reg(String modid) {
		this.modid = modid;
	}

	public <T> DeferredRegister<T> make(Registry<T> reg) {
		var ans = DeferredRegister.create(reg, modid);
		list.add(ans);
		return ans;
	}

	public void register(IEventBus bus) {
		for (var e : list) e.register(bus);
	}

}
