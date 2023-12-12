package dev.xkmc.l2library.serial.network;

import com.google.gson.JsonElement;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.serial.codec.JsonCodec;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class RecordDataProvider implements DataProvider {
	private final DataGenerator generator;
	private final String name;
	private final Map<String, Record> map = new HashMap<>();

	public RecordDataProvider(DataGenerator generator, String name) {
		this.generator = generator;
		this.name = name;
	}

	public abstract void add(BiConsumer<String, Record> map);


	public void run(CachedOutput cache) {
		Path folder = this.generator.getOutputFolder();
		this.add(this.map::put);
		this.map.forEach((k, v) -> {
			JsonElement elem = JsonCodec.toJson(v);
			if (elem != null) {
				Path path = folder.resolve("data/" + k + ".json");
				try {
					DataProvider.saveStable(cache, elem, path);
				} catch (Exception e) {
					L2Library.LOGGER.throwing(e);
				}
			}

		});
	}

	public String getName() {
		return this.name;
	}

}
