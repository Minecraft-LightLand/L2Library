package dev.xkmc.l2library.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import dev.xkmc.l2library.serial.codec.JsonCodec;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ConfigDataProvider implements DataProvider {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

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
	public void run(HashCache cache) {
		Path folder = generator.getOutputFolder();
		add(map);
		map.forEach((k, v) -> {
			try {
				JsonElement elem = JsonCodec.toJson(v, BaseConfig.class);
				String str = GSON.toJson(elem);
				String hash = SHA1.hashUnencodedChars(str).toString();
				Path path = folder.resolve(folder_path + k + ".json");
				if (!Objects.equals(cache.getHash(path), hash) || !Files.exists(path)) {
					Files.createDirectories(path.getParent());
					BufferedWriter writer = Files.newBufferedWriter(path);
					try {
						writer.write(str);
					} catch (Throwable err) {
						try {
							writer.close();
						} catch (Throwable throwable) {
							err.addSuppressed(throwable);
						}
						throw err;
					}
					writer.close();
				}
				cache.putNew(path, hash);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public String getName() {
		return name;
	}
}
