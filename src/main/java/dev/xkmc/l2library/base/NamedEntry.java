package dev.xkmc.l2library.base;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class NamedEntry<T extends NamedEntry<T>> extends ForgeRegistryEntry<T> {

	private final Supplier<IForgeRegistry<T>> registry;

	private String desc = null;

	public NamedEntry(Supplier<IForgeRegistry<T>> registry) {
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

	public TranslatableComponent getDesc() {
		return new TranslatableComponent(getDescriptionId());
	}

	public String getID() {
		return getRegistryName().toString();
	}

}
