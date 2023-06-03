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

	public HashMap<ResourceLocation, JsonElement> configs = new HashMap<>();

	public final String config_path;

	final ConfigReloadListener listener;
	final List<Runnable> listener_before = new ArrayList<>();
	final List<Runnable> listener_after = new ArrayList<>();

	private final Map<String, CachedConfig<?>> cache = new HashMap<>();

	@SafeVarargs
	public PacketHandlerWithConfig(ResourceLocation id, int version, String config_path, Function<BasePacketHandler, LoadedPacket<?>>... values) {
		super(id, version, values);
		INTERNAL.put(id, this);
		listener = new ConfigReloadListener(config_path);
		this.config_path = config_path;
	}

	public void addBeforeReloadListener(Runnable runnable) {
		listener_before.add(runnable);
	}

	public void addAfterReloadListener(Runnable runnable) {
		listener_after.add(runnable);
	}

	public <T extends BaseConfig> void addCachedConfig(String id, Class<T> loader) {
		CachedConfig<T> c = new CachedConfig<>(id, loader);
		cache.put(id, c);
		addBeforeReloadListener(c.list::clear);
		addAfterReloadListener(() -> c.result = null);
	}

	public <T extends BaseConfig> T getCachedConfig(String id) {
		return Wrappers.cast(cache.get(id).load());
	}

	private class CachedConfig<T extends BaseConfig> {

		private final Class<T> cls;
		private final String id;

		private final List<T> list = new ArrayList<>();

		private T result;

		CachedConfig(String id, Class<T> cls) {
			this.id = id;
			this.cls = cls;
		}

		T load() {
			if (result != null) {
				return result;
			}
			result = new ConfigMerger<>(cls).apply(list);
			result.id = new ResourceLocation(CHANNEL_NAME.getNamespace(), id);
			return result;
		}

	}

	class ConfigReloadListener extends SimpleJsonResourceReloadListener {

		public ConfigReloadListener(String path) {
			super(new Gson(), path);
		}

		@Override
		protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller filler) {
			apply(map);
		}

		protected void apply(Map<ResourceLocation, JsonElement> map) {
			listener_before.forEach(Runnable::run);
			map.forEach((k, v) -> {
				if (!k.getNamespace().startsWith("_")) {
					if (!ModList.get().isLoaded(k.getNamespace())) {
						return;
					}
				}
				String id = k.getPath().split("/")[0];
				if (cache.containsKey(id)) {
					add(cache.get(id), k, v);
				}
			});
			listener_after.forEach(Runnable::run);
		}

		private <T extends BaseConfig> void add(CachedConfig<T> type, ResourceLocation k, JsonElement v) {
			T config = JsonCodec.from(v, type.cls, null);
			if (config != null) {
				config.id = k;
				type.list.add(config);
				configs.put(k, v);
			}
		}
	}

}
