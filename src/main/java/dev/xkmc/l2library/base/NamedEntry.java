package dev.xkmc.l2library.base;

import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class NamedEntry<T extends NamedEntry<T>>  {

	private final L2Registrate.RegistryInstance<T> registry;

	private String desc = null;

	public NamedEntry(L2Registrate.RegistryInstance<T> registry) {
		this.registry = registry;
	}

	public @NotNull String getDescriptionId() {
		if (desc != null)
			return desc;
		ResourceLocation rl = getRegistryName();
		ResourceLocation reg = registry.get().getRegistryName();
		desc = reg.getPath() + "." + rl.getNamespace() + "." + rl.getPath();
		return desc;
	}

	public TranslatableContents getDesc() {
		return new TranslatableContents(getDescriptionId());
	}

	public ResourceLocation getRegistryName(){
		return registry.get().getKey(getThis());
	}

	public String getID() {
		return getRegistryName().toString();
	}

	public T getThis(){
		return (T) this;
	}

}
