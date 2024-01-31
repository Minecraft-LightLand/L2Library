package dev.xkmc.l2library.serial.conditions;

import dev.xkmc.l2library.init.L2Library;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;

public record BooleanValueCondition(String path, ArrayList<String> line, boolean expected) implements ICondition {

	public static final ResourceLocation ID = new ResourceLocation(L2Library.MODID, "boolean_config");

	private static BooleanValueCondition of(ModConfig file, ForgeConfigSpec.ConfigValue<Boolean> config, boolean value) {
		return new BooleanValueCondition(file.getFileName(), new ArrayList<>(config.getPath()), value);
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
		return line instanceof Boolean bool && bool == expected;
	}


}
