package dev.xkmc.l2library.serial.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class AutoPacketNBTHandler<T> extends ClassHandler<CompoundTag, T> {

	public AutoPacketNBTHandler(@NotNull Class<T> cls, Function<CompoundTag, T> ft, Function<T, Tag> tt, @NotNull Class<?>... others) {
		super(cls, null, null, p -> ft.apply(p.readAnySizeNbt()), (p, o) -> p.writeNbt((CompoundTag) tt.apply(o)), ft, tt, others);
	}
}
