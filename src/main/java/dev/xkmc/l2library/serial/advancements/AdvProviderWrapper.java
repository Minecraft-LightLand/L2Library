package dev.xkmc.l2library.serial.advancements;

import com.tterrag.registrate.providers.RegistrateAdvancementProvider;
import net.minecraft.advancements.Advancement;

import java.util.List;

public class AdvProviderWrapper {

	public static void save(RegistrateAdvancementProvider pvd, List<IAdvBuilder> builder, Advancement r) {
		pvd.accept(new AdvBuilderWrapper(r, builder));
	}

}
