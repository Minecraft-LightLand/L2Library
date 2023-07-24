package dev.xkmc.l2library.base.datapack;

import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2serial.serialization.custom_handler.StringClassHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public class DatapackInstance<T, R extends EntryHolder<T>> {

	public final ResourceKey<Registry<T>> key;
	public final Function<Holder.Reference<T>, R> func;

	public DatapackInstance(ResourceLocation id, Class<R> cls, Function<Holder.Reference<T>, R> func) {
		key = ResourceKey.createRegistryKey(id);
		this.func = func;
		new StringClassHandler<>(cls,
				str -> getValue(Proxy.getWorld(), new ResourceLocation(str)).get(),
				e -> e.getID().toString());
	}

	public ResourceKey<T> entryKey(ResourceLocation key) {
		return ResourceKey.create(this.key, key);
	}

	public Collection<R> getValues(Level level) {
		return level.registryAccess().registryOrThrow(key).holders().map(func).toList();
	}

	public Optional<R> getValue(Level level, ResourceLocation id) {
		return level.registryAccess().registryOrThrow(key).getHolder(entryKey(id)).map(func);
	}

	public String getEntryDescID(ResourceLocation id) {
		return key.location().getPath() + "." + id.getNamespace() + "." + id.getPath();
	}

	public EntrySetBuilder<T, R> startGen(L2Registrate r) {
		return new EntrySetBuilder<>(r, this);
	}

}
