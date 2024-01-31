package dev.xkmc.l2library.serial.conditions;

import dev.xkmc.l2library.init.L2Library;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;

public record DoubleValueCondition(String path, ArrayList<String> line, double low, double high) implements ICondition {

	public static final ResourceLocation ID = new ResourceLocation(L2Library.MODID, "double_config");

	public static DoubleValueCondition of(String file, ForgeConfigSpec.ConfigValue<Double> config, double low, double high) {
		return new DoubleValueCondition(file, new ArrayList<>(config.getPath()), low, high);
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
		return line instanceof Double val && low <= val && val <= high;
	}


}
