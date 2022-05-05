package dev.xkmc.l2library.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.serial.codec.JsonCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PacketHandlerWithConfig extends PacketHandler {

	static final HashMap<ResourceLocation, PacketHandlerWithConfig> INTERNAL = new HashMap<>();

	public static void onDatapackSync(OnDatapackSyncEvent event) {
		for (PacketHandlerWithConfig handler : INTERNAL.values()) {
			SyncPacket packet = new SyncPacket(handler, handler.CONFIGS);
			if (event.getPlayer() == null) L2Library.PACKET_HANDLER.toAllClient(packet);
			else L2Library.PACKET_HANDLER.toClientPlayer(packet, event.getPlayer());
		}
	}

	public static void addReloadListeners(AddReloadListenerEvent event) {
		for (PacketHandlerWithConfig handler : INTERNAL.values()) {
			if (handler.CONFIG != null)
				event.addListener(handler.CONFIG);
		}
	}

	public HashMap<String, BaseConfig> CONFIGS = new HashMap<>();

	private final PreparableReloadListener CONFIG;
	private final List<Runnable> listener_before = new ArrayList<>();
	private final List<Runnable> listener_after = new ArrayList<>();

	@SafeVarargs
	public PacketHandlerWithConfig(ResourceLocation id, int version, String config_path, Function<PacketHandler, LoadedPacket<?>>... values) {
		super(id, version, values);
		INTERNAL.put(id, this);
		CONFIG = new ConfigReloadListener(config_path);
	}

	public void addBeforeReloadListener(Runnable runnable) {
		listener_before.add(runnable);
	}

	public void addAfterReloadListener(Runnable runnable) {
		listener_after.add(runnable);
	}

	private class ConfigReloadListener extends SimpleJsonResourceReloadListener {

		public ConfigReloadListener(String path) {
			super(new Gson(), path);
		}

		@Override
		protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller filler) {
			listener_before.forEach(Runnable::run);
			map.forEach((k, v) -> {
				BaseConfig config = JsonCodec.from(v, BaseConfig.class, null);
				if (config != null)
					CONFIGS.put(k.toString(), config);
			});
			listener_after.forEach(Runnable::run);
		}
	}

}
