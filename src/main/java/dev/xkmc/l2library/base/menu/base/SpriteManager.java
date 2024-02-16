package dev.xkmc.l2library.base.menu.base;

import dev.xkmc.l2library.init.L2LibReg;
import dev.xkmc.l2library.init.L2Library;
import net.minecraft.resources.ResourceLocation;

public record SpriteManager(String modid, String path) {

	public MenuLayoutConfig get() {
		return L2LibReg.MENU_LAYOUT.getEntry(new ResourceLocation(modid, path));
	}

}
