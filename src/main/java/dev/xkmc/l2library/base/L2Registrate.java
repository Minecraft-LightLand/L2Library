package dev.xkmc.l2library.base;

import com.google.common.base.Suppliers;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2serial.serialization.custom_handler.RLClassHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class L2Registrate extends AbstractRegistrate<L2Registrate> {
	/**
	 * Construct a new Registrate for the given mod ID.
	 *
	 * @param modid The mod ID for which objects will be registered
	 */
	public L2Registrate(String modid) {
		super(modid);
		registerEventListeners(FMLJavaModLoadingContext.get().getModEventBus());
	}

	public <T extends NamedEntry<T>, P extends T> GenericBuilder<T, P> generic(RegistryInstance<T> cls, String id, NonNullSupplier<P> sup) {
		return entry(id, cb -> new GenericBuilder<>(this, id, cb, cls.key(), sup));
	}

	public <T extends Recipe<?>> RegistryEntry<RecipeType<T>> recipe(String id) {
		return simple(id, ForgeRegistries.Keys.RECIPE_TYPES, () -> new RecipeType<>() {
		});
	}

	@SuppressWarnings({"unchecked", "unsafe"})
	public <E extends NamedEntry<E>> RegistryInstance<E> newRegistry(String id, Class<?> cls) {
		ResourceKey<Registry<E>> key = makeRegistry(id, () ->
				new RegistryBuilder<E>().onCreate((r, s) ->
						new RLClassHandler<>((Class<E>) cls, () -> r)));
		return new RegistryInstance<>(Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(key)), key);
	}

	public RegistryEntry<CreativeModeTab> buildModCreativeTab(String name, String def, Consumer<CreativeModeTab.Builder> config) {
		ResourceLocation id = new ResourceLocation(getModid(), name);
		defaultCreativeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, id));
		return buildCreativeTabImpl(name, this.addLang("itemGroup", id, def), config);
	}

	public RegistryEntry<CreativeModeTab> buildL2CreativeTab(String name, String def, Consumer<CreativeModeTab.Builder> config) {
		ResourceLocation id = new ResourceLocation(L2Library.MODID, name);
		defaultCreativeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, id));
		return L2Library.REGISTRATE.buildCreativeTabImpl(name, this.addLang("itemGroup", id, def), config);
	}

	private RegistryEntry<CreativeModeTab> buildCreativeTabImpl(String name, Component comp, Consumer<CreativeModeTab.Builder> config) {
		return this.generic(self(), name, Registries.CREATIVE_MODE_TAB, () -> {
			var builder = CreativeModeTab.builder().title(comp);
			config.accept(builder);
			return builder.build();
		}).register();
	}

	public record RegistryInstance<E extends NamedEntry<E>>(Supplier<IForgeRegistry<E>> supplier,
															ResourceKey<Registry<E>> key) implements Supplier<IForgeRegistry<E>> {

		@Override
		public IForgeRegistry<E> get() {
			return supplier().get();
		}
	}

	public static class GenericBuilder<T extends NamedEntry<T>, P extends T> extends AbstractBuilder<T, P, L2Registrate, GenericBuilder<T, P>> {

		private final NonNullSupplier<P> sup;

		GenericBuilder(L2Registrate parent, String name, BuilderCallback callback, ResourceKey<Registry<T>> registryType, NonNullSupplier<P> sup) {
			super(parent, parent, name, callback, registryType);
			this.sup = sup;
		}

		@Override
		protected @NonnullType @NotNull P createEntry() {
			return sup.get();
		}

		public GenericBuilder<T, P> defaultLang() {
			return lang(NamedEntry::getDescriptionId, RegistrateLangProvider.toEnglishName(this.getName()));
		}

	}

}
