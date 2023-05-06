package dev.xkmc.l2library.serial.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2serial.network.BasePacketHandler;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
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
import java.util.stream.Stream;

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

	public HashMap<String, BaseConfig> configs = new HashMap<>();

	private final PreparableReloadListener listener;
	private final Map<String, CachedConfig<?>> cache = new HashMap<>();
	final List<Runnable> listener_before = new ArrayList<>();
	final List<Runnable> listener_after = new ArrayList<>();

	@SafeVarargs
	public PacketHandlerWithConfig(ResourceLocation id, int version, String config_path, Function<BasePacketHandler, LoadedPacket<?>>... values) {
		super(id, version, values);
		INTERNAL.put(id, this);
		listener = new ConfigReloadListener(config_path);
	}

	public void addBeforeReloadListener(Runnable runnable) {
		listener_before.add(runnable);
	}

	public void addAfterReloadListener(Runnable runnable) {
		listener_after.add(runnable);
	}

	public <T extends BaseConfig> void addCachedConfig(String id, Function<Stream<Map.Entry<String, BaseConfig>>, T> loader) {
		CachedConfig<T> c = new CachedConfig<>(id, loader);
		cache.put(id, c);
		addAfterReloadListener(() -> c.result = null);
	}

	public <T extends BaseConfig> T getCachedConfig(String id) {
		return Wrappers.cast(cache.get(id).load());
	}

	public Stream<Map.Entry<String, BaseConfig>> getConfigs(String id) {
		return configs.entrySet().stream()
				.filter(e -> new ResourceLocation(e.getKey()).getPath().split("/")[0].equals(id));
	}

	private class CachedConfig<T extends BaseConfig> {

		private final Function<Stream<Map.Entry<String, BaseConfig>>, T> function;
		private final String id;

		private T result;

		CachedConfig(String id, Function<Stream<Map.Entry<String, BaseConfig>>, T> function) {
			this.id = id;
			this.function = function;
		}

		T load() {
			if (result != null) {
				return result;
			}
			result = function.apply(getConfigs(id));
			return result;
		}

	}

	private class ConfigReloadListener extends SimpleJsonResourceReloadListener {

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
				BaseConfig config = JsonCodec.from(v, BaseConfig.class, null);
				if (config != null) {
					config.id = k;
					configs.put(k.toString(), config);
				}
			});
			listener_after.forEach(Runnable::run);
		}
	}

}
