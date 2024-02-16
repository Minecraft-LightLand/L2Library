package dev.xkmc.l2library.init.reg.simple;

import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public record SR<T>(DeferredRegister<T> reg) {

	public static <T> SR<T> of(Reg parent, Registry<T> reg) {
		return new SR<>(parent.make(reg));
	}

	public Val<T> reg(String id, Supplier<T> sup) {
		return new ValImpl<>(reg.register(id, sup));
	}

	private record ValImpl<R, T extends R>(DeferredHolder<R, T> val) implements Val<T> {

		@Override
		public T get() {
			return val.get();
		}

	}

}
