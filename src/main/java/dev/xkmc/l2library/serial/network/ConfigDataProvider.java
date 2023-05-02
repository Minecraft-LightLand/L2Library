package dev.xkmc.l2library.serial.network;

import com.google.gson.JsonElement;
import dev.xkmc.l2library.serial.codec.JsonCodec;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ConfigDataProvider implements DataProvider {

	private final DataGenerator generator;
	private final String folder_path, name;

	private final Map<String, BaseConfig> map = new HashMap<>();

	public ConfigDataProvider(DataGenerator generator, String folder_path, String name) {
		this.generator = generator;
		this.folder_path = folder_path;
		this.name = name;
	}

	public abstract void add(Map<String, BaseConfig> map);

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		Path folder = generator.getPackOutput().getOutputFolder();
		add(map);
		List<CompletableFuture<?>> list = new ArrayList<>();
		map.forEach((k, v) -> {
			JsonElement elem = JsonCodec.toJson(v, BaseConfig.class);
			Path path = folder.resolve(folder_path + k + ".json");
			list.add(DataProvider.saveStable(cache, elem, path));
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	@Override
	public String getName() {
		return name;
	}
}
