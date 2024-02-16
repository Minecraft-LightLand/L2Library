package dev.xkmc.l2library.init.reg.simple;

import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredRegister;

public record SR<T>(DeferredRegister<T> reg) {

	public static <T> SR<T> of(Reg parent, Registry<T> reg) {
		return new SR<>(parent.make(reg));
	}

}
