package dev.xkmc.l2library.capability.conditionals;

import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.world.entity.player.Player;

@SerialClass
public class ConditionalToken {

	/**
	 * return true to remove
	 */
	public boolean tick(Player player) {
		return false;
	}

}
