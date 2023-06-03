package dev.xkmc.l2library.serial.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2serial.network.BasePacketHandler;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@SuppressWarnings("unused")
public class PacketHandlerWithConfig extends PacketHandler {

	static final Map<ResourceLocation, PacketHandlerWithConfig> INTERNAL = new ConcurrentHashMap<>();

	public static void onDatapackSync(OnDatapackSyncEvent event) {
		for (PacketHandlerWithConfig handler : INTERNAL.values()) {
			SyncPacket packet = new SyncPacket(handler, handler.configs);
			if (event.getPlayer() == null) L2Library.PACKET_HANDLER.toAllClient(packet);
			else L2Library.PACKET_HANDLER.toClientPlayer(packet, event.getPlayer());
		}
	}

	public static void addReloadListeners(AddReloadListenerEvent event) {
		for (PacketHandlerWithConfig handler : INTERNAL.values()) {
			if (handler.listener != null)
				event.addListener(handler.listener);
		}
	}

	public HashMap<ResourceLocation, ConfigInstance> configs = new HashMap<>();

	public final String config_path;

	final ConfigReloadListener listener;
	final List<Runnable> listener_before = new ArrayList<>();
	final List<Runnable> listener_after = new ArrayList<>();
	final Map<String, BaseConfigType<?>> types = new HashMap<>();

	@SafeVarargs
	public PacketHandlerWithConfig(ResourceLocation id, int version, Function<BasePacketHandler, LoadedPacket<?>>... values) {
		super(id, version, values);
		INTERNAL.put(id, this);
		config_path = id.getNamespace() + "_config";
		listener = new ConfigReloadListener(config_path);
	}

	public void addBeforeReloadListener(Runnable runnable) {
		listener_before.add(runnable);
	}

	public void addAfterReloadListener(Runnable runnable) {
		listener_after.add(runnable);
	}

	public <T extends BaseConfig> void addConfig(String id, Class<T> loader) {
		BaseConfigType<T> c = new BaseConfigType<>(this, id, loader);
		types.put(id, c);
		addBeforeReloadListener(c::beforeReload);
		addAfterReloadListener(c::afterReload);
	}

	public <T extends BaseConfig> void addCachedConfig(String id, Class<T> loader) {
		MergedConfigType<T> c = new MergedConfigType<>(this, id, loader);
		types.put(id, c);
		addBeforeReloadListener(c::beforeReload);
		addAfterReloadListener(c::afterReload);
	}

	<T extends BaseConfig> T getCachedConfig(String id) {
		MergedConfigType<T> type = Wrappers.cast(types.get(id));
		return type.load();
	}

	class ConfigReloadListener extends SimpleJsonResourceReloadListener {

		public ConfigReloadListener(String path) {
			super(new Gson(), path);
		}

		@Override
		protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller filler) {
			listener_before.forEach(Runnable::run);
			map.forEach((k, v) -> {
				if (!k.getNamespace().startsWith("_")) {
					if (!ModList.get().isLoaded(k.getNamespace())) {
						return;
					}
				}
				String id = k.getPath().split("/")[0];
				if (types.containsKey(id)) {
					String name = k.getPath().substring(id.length() + 1);
					ResourceLocation nk = new ResourceLocation(k.getNamespace(), name);
					addJson(types.get(id), nk, v);
				}
			});
			listener_after.forEach(Runnable::run);
		}

		private <T extends BaseConfig> void addJson(BaseConfigType<T> type, ResourceLocation k, JsonElement v) {
			T config = JsonCodec.from(v, type.cls, null);
			if (config != null) {
				addConfig(type, k, config);
			}
		}

		private <T extends BaseConfig> void addConfig(BaseConfigType<T> type, ResourceLocation k, T config) {
			config.id = k;
			type.configs.put(k, config);
			configs.put(k, new ConfigInstance(type.id, k, config));
		}

		/**
		 * Called on client side only
		 */
		public void apply(ArrayList<ConfigInstance> list) {
			listener_before.forEach(Runnable::run);
			for (var e : list) {
				addConfig(types.get(e.name), e.id(), Wrappers.cast(e.config));
			}
			listener_after.forEach(Runnable::run);
		}
	}

	public record ConfigInstance(String name, ResourceLocation id, BaseConfig config) {

	}

}
