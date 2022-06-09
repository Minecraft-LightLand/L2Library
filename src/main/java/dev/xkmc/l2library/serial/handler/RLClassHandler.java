package dev.xkmc.l2library.serial.handler;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class RLClassHandler<R extends Tag, T> extends ClassHandler<R, T> {

	public RLClassHandler(Class<T> cls, Supplier<IForgeRegistry<T>> r) {
		super(cls, e -> e == null ? JsonNull.INSTANCE : new JsonPrimitive(r.get().getKey(e).toString()),
				e -> e.isJsonNull() ? null : r.get().getValue(new ResourceLocation(e.getAsString())),
				p -> {
					String str = p.readUtf();
					if (str.length() == 0)
						return null;
					return r.get().getValue(new ResourceLocation(str));
				},
				(p, t) -> p.writeUtf(t == null ? "" : r.get().getKey(t).toString()),
				s -> s.getAsString().length() == 0 ? null : r.get().getValue(new ResourceLocation(s.getAsString())),
				t -> t == null ? StringTag.valueOf("") : StringTag.valueOf(r.get().getKey(t).toString()));
	}

}
