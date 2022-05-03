package dev.xkmc.l2library.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;

public interface NameSetable extends Nameable {

	void setCustomName(Component component);

}
