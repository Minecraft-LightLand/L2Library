package dev.xkmc.l2library.serial.conditions;

import dev.xkmc.l2library.init.L2Library;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;

public record StringValueCondition(String path, ArrayList<String> line, String key) implements ICondition {

	public static final ResourceLocation ID = new ResourceLocation(L2Library.MODID, "string_config");

	public static StringValueCondition of(String file, ForgeConfigSpec.ConfigValue<String> config, String key) {
		return new StringValueCondition(file, new ArrayList<>(config.getPath()), key);
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test(IContext context) {
		var file = ConfigTracker.INSTANCE.fileMap().get(path);
		if (file == null) return false;
		var line = file.getConfigData().get(line());
		if (line == null) return false;
		return line instanceof String val && val.equals(key);
	}

}
