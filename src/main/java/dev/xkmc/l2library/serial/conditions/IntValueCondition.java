package dev.xkmc.l2library.serial.conditions;

import dev.xkmc.l2library.init.L2Library;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;

public record IntValueCondition(String path, ArrayList<String> line, int low, int high) implements ICondition {

	public static final ResourceLocation ID = new ResourceLocation(L2Library.MODID, "int_config");

	private static IntValueCondition of(ModConfig file, ForgeConfigSpec.ConfigValue<Integer> config, int low, int high) {
		return new IntValueCondition(file.getFileName(), new ArrayList<>(config.getPath()), low, high);
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
		return line instanceof Integer val && low <= val && val <= high;
	}


}
