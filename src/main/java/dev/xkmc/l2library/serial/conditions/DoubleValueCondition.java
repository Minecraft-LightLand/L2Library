package dev.xkmc.l2library.serial.conditions;

import com.mojang.serialization.Codec;
import dev.xkmc.l2library.init.L2LibReg;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;

public record DoubleValueCondition(String path, ArrayList<String> line, double low, double high) implements ICondition {

	public static DoubleValueCondition of(String file, ModConfigSpec.ConfigValue<Double> config, double low, double high) {
		return new DoubleValueCondition(file, new ArrayList<>(config.getPath()), low, high);
	}

	@Override
	public boolean test(IContext context) {
		var file = ConfigTracker.INSTANCE.fileMap().get(path);
		if (file == null) return false;
		var line = file.getConfigData().get(line());
		if (line == null) return false;
		return line instanceof Double val && low <= val && val <= high;
	}

	@Override
	public Codec<DoubleValueCondition> codec() {
		return L2LibReg.CONDITION_DOUBLE.get();
	}

}
