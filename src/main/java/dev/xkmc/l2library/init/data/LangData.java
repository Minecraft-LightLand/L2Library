package dev.xkmc.l2library.init.data;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum LangData {
	;

	public static void genLang(RegistrateLangProvider pvd) {
		pvd.add("key.categories.l2library", "L2Library Keys");
	}

}
