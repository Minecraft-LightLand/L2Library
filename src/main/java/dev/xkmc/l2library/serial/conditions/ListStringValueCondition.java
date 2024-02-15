package dev.xkmc.l2library.serial.conditions;

import com.mojang.serialization.Codec;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.reg.L2LibraryRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;

public record ListStringValueCondition(String path, ArrayList<String> line, String key) implements ICondition {

	public static final ResourceLocation ID = new ResourceLocation(L2Library.MODID, "string_list_config");

	public static ListStringValueCondition of(String file, ModConfigSpec.ConfigValue<List<String>> config, String key) {
		return new ListStringValueCondition(file, new ArrayList<>(config.getPath()), key);
	}

	@Override
	public boolean test(IContext context) {
		var file = ConfigTracker.INSTANCE.fileMap().get(path);
		if (file == null) return false;
		var line = file.getConfigData().get(line());
		if (line == null) return false;
		return line instanceof List<?> val && val.contains(key);
	}

	@Override
	public Codec<ListStringValueCondition> codec() {
		return L2LibraryRegistry.CONDITION_LIST_STR.get();
	}

}
