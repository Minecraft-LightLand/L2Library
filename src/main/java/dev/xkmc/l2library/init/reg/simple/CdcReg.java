package dev.xkmc.l2library.init.reg.simple;

import com.mojang.serialization.Codec;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public record CdcReg<T>(DeferredRegister<Codec<? extends T>> reg) {

	public static <T> CdcReg<T> of(Reg parent, Registry<Codec<? extends T>> reg) {
		return new CdcReg<>(parent.make(reg));
	}

	public <R extends T> CdcVal<R> reg(String id, Codec<R> codec) {
		return new CdcValImpl<>(reg.register(id, () -> codec));
	}

	public <R extends T> CdcVal<R> reg(String id, Class<R> cls) {
		return new CdcValImpl<>(reg.register(id, () -> new CodecAdaptor<>(cls)));
	}

	private record CdcValImpl<R extends T, T>(DeferredHolder<Codec<? extends T>, Codec<R>> val)
			implements CdcVal<R> {

		@Override
		public Codec<R> get() {
			return val.get();
		}
	}

}
