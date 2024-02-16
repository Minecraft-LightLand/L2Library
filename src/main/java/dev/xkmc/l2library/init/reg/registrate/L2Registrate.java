package dev.xkmc.l2library.init.reg.registrate;

/*TODO
public class L2Registrate extends AbstractRegistrate<L2Registrate> {

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

	@Deprecated
	@Override
	public <T extends Enchantment> EnchantmentBuilder<T, L2Registrate> enchantment(String name, EnchantmentCategory type, EnchantmentBuilder.EnchantmentFactory<T> factory) {
		return super.enchantment(name, type, factory);
	}

	public <T extends Enchantment> EnchantmentBuilder<T, L2Registrate> enchantment(String name, EnchantmentCategory type, EnchantmentBuilder.EnchantmentFactory<T> factory, String desc) {
		addRawLang("enchantment." + getModid() + "." + name + ".desc", desc);
		return super.enchantment(name, type, factory);
	}

	public <T extends MobEffect> NoConfigBuilder<MobEffect, T, L2Registrate> effect(String name, NonNullSupplier<T> sup, String desc) {
		addRawLang("effect." + getModid() + "." + name + ".description", desc);
		return entry(name, cb -> new NoConfigBuilder<>(this, this, name, cb, ForgeRegistries.Keys.MOB_EFFECTS, sup));
	}

	@SuppressWarnings({"unchecked", "unsafe"})
	public <E extends NamedEntry<E>> RegistryInstance<E> newRegistry(String id, Class<?> cls, Consumer<RegistryBuilder<E>> cons) {
		ResourceKey<Registry<E>> key = makeRegistry(id, () -> {
			var ans = new RegistryBuilder<E>();
			ans.onCreate((r, s) -> new RLClassHandler<>((Class<E>) cls, () -> r));
			cons.accept(ans);
			return ans;
		});
		return new RegistryInstance<>(Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(key)), key);
	}

	public <E extends NamedEntry<E>> RegistryInstance<E> newRegistry(String id, Class<?> cls) {
		return newRegistry(id, cls, e -> {
		});
	}

	public synchronized RegistryEntry<CreativeModeTab> buildModCreativeTab(String name, String def, Consumer<CreativeModeTab.Builder> config) {
		ResourceLocation id = new ResourceLocation(getModid(), name);
		defaultCreativeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, id));
		return buildCreativeTabImpl(name, this.addLang("itemGroup", id, def), config);
	}

	public synchronized RegistryEntry<CreativeModeTab> buildL2CreativeTab(String name, String def, Consumer<CreativeModeTab.Builder> config) {
		ResourceLocation id = new ResourceLocation(L2Library.MODID, name);
		defaultCreativeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, id));
		TabSorter sorter = new TabSorter(getModid() + ":" + name, id);
		return L2Library.REGISTRATE.buildCreativeTabImpl(name, this.addLang("itemGroup", id, def), b -> {
			config.accept(b);
			sorter.sort(b);
		});
	}

	private synchronized RegistryEntry<CreativeModeTab> buildCreativeTabImpl(String name, Component comp, Consumer<CreativeModeTab.Builder> config) {
		return this.generic(self(), name, Registries.CREATIVE_MODE_TAB, () -> {
			var builder = CreativeModeTab.builder().title(comp)
					.withTabsBefore(CreativeModeTabs.SPAWN_EGGS);
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

	private static class TabSorter {

		private static final TreeMap<String, TabSorter> MAP = new TreeMap<>();
		private static final HashSet<ResourceLocation> SET = new HashSet<>();

		private final ResourceLocation id;

		private TabSorter(String str, ResourceLocation id) {
			MAP.put(str, this);
			SET.add(id);
			this.id = id;
		}

		public void sort(CreativeModeTab.Builder b) {
			var list = new ArrayList<>(MAP.values());
			boolean after = false;
			ResourceLocation before = null;
			for (var e : list) {
				if (e == this) {
					after = true;
					if (before != null) {
						b.withTabsBefore(before);
					}
					continue;
				}
				if (after) {
					b.withTabsAfter(e.id);
					return;
				} else {
					before = e.id;
				}
			}
			for (var e : BuiltInRegistries.CREATIVE_MODE_TAB.entrySet()) {
				var id = e.getKey().location();
				if (known(id) || known(e.getValue())) {
					continue;
				}
				b.withTabsAfter(id);
			}
		}

		private static boolean known(ResourceLocation id) {
			if (id.getNamespace().equals("minecraft")) {
				return true;
			}
			return SET.contains(id);
		}

		private static boolean known(CreativeModeTab tab) {
			for (var other : tab.tabsAfter) {
				if (known(other)) {
					return true;
				}
			}
			return false;
		}

	}

}
*/