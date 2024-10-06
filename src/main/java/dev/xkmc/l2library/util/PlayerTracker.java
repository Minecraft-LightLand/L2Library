package dev.xkmc.l2library.util;

import dev.xkmc.l2core.capability.conditionals.ConditionalToken;
import dev.xkmc.l2core.capability.conditionals.TokenKey;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.L2LibraryConfig;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.world.entity.player.Player;

@SerialClass
public class PlayerTracker extends ConditionalToken {

	public static final TokenKey<PlayerTracker> KEY = TokenKey.of(L2Library.loc("tracker"));

	public static PlayerTracker get(Player player) {
		return L2LibReg.CONDITIONAL.type().getOrCreate(player).getOrCreateData(KEY, PlayerTracker::new);
	}

	@SerialField
	private int tickSinceDeath;

	@Override
	public boolean tick(Player player) {
		tickSinceDeath++;
		if (L2LibraryConfig.SERVER.restoreFullHealthOnRespawn.get() && tickSinceDeath < 60) {
			if (player.getHealth() < player.getMaxHealth()) {
				player.setHealth(player.getMaxHealth());
			}
		}
		return false;
	}

	public int getTickSinceDeath() {
		return tickSinceDeath;
	}

	@Override
	public boolean retainOnDeath(Player player) {
		tickSinceDeath = 0;
		return true;
	}

}
