package dev.xkmc.l2library.serial.handler;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class RLClassHandler<R extends Tag, T> extends ClassHandler<R, T> {

	public RLClassHandler(Class<T> cls, Supplier<IForgeRegistry<T>> r) {
		super(cls, e -> e == null ? JsonNull.INSTANCE : new JsonPrimitive(r.get().getKey(e).toString()),
				e -> e.isJsonNull() ? null : r.get().getValue(new ResourceLocation(e.getAsString())),
				p -> {
					int index = p.readInt();
					if (index == -1)
						return null;
					return ((ForgeRegistry<T>) r.get()).getValue(index);
				},
				(p, t) -> p.writeInt(t == null ? -1 : ((ForgeRegistry<T>) r.get()).getID(t)),
				s -> s.getAsString().length() == 0 ? null : r.get().getValue(new ResourceLocation(s.getAsString())),
				t -> t == null ? StringTag.valueOf("") : StringTag.valueOf(r.get().getKey(t).toString()));
	}

}
