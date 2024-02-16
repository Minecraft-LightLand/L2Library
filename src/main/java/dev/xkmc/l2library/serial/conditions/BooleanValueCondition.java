package dev.xkmc.l2library.serial.conditions;

import com.mojang.serialization.Codec;
import dev.xkmc.l2library.init.reg.L2LibReg;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;

public record BooleanValueCondition(String path, ArrayList<String> line, boolean expected) implements ICondition {

	public static BooleanValueCondition of(String file, ModConfigSpec.ConfigValue<Boolean> config, boolean value) {
		return new BooleanValueCondition(file, new ArrayList<>(config.getPath()), value);
	}

	@Override
	public boolean test(IContext context) {
		var file = ConfigTracker.INSTANCE.fileMap().get(path);
		if (file == null) return false;
		var line = file.getConfigData().get(line());
		if (line == null) return false;
		return line instanceof Boolean bool && bool == expected;
	}

	@Override
	public Codec<BooleanValueCondition> codec() {
		return L2LibReg.CONDITION_BOOL.get();
	}

}
