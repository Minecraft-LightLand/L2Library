package dev.xkmc.l2library.serial.config;

import com.google.gson.JsonElement;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ConfigDataProvider implements DataProvider {

	private final DataGenerator generator;
	private final String name;

	private final Map<String, ConfigEntry<?>> map = new HashMap<>();

	public ConfigDataProvider(DataGenerator generator, String name) {
		this.generator = generator;
		this.name = name;
	}

	public abstract void add(Collector map);

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		Path folder = generator.getPackOutput().getOutputFolder();
		add(new Collector(map));
		List<CompletableFuture<?>> list = new ArrayList<>();
		map.forEach((k, v) -> {
			JsonElement elem = v.serialize();
			if (elem != null) {
				Path path = folder.resolve(k + ".json");
				list.add(DataProvider.saveStable(cache, elem, path));
			}
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	@Override
	public String getName() {
		return name;
	}

	public record Collector(Map<String, ConfigEntry<?>> map) {

		public <T extends BaseConfig> void add(ConfigTypeEntry<T> type, ResourceLocation id, T config) {
			map.put(type.asPath(id), new ConfigEntry<>(type, id, config));
		}

	}

	public record ConfigEntry<T extends BaseConfig>(ConfigTypeEntry<T> type, ResourceLocation id, T config) {

		@Nullable
		public JsonElement serialize() {
			return JsonCodec.toJson(config, type.cls());
		}

	}

}
