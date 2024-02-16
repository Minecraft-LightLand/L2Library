package dev.xkmc.l2library.capability.conditionals;

import dev.xkmc.l2library.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2library.init.L2LibraryConfig;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.HashMap;

@SerialClass
public class ConditionalData extends PlayerCapabilityTemplate<ConditionalData> {

	@SerialClass.SerialField
	public HashMap<TokenKey<?>, ConditionalToken> data = new HashMap<>();
	@SerialClass.SerialField
	public int tickSinceDeath = 0;

	@Override
	public void onClone(boolean isWasDeath) {
		tickSinceDeath = 0;
	}

	public <T extends ConditionalToken, C extends Context> T getOrCreateData(TokenProvider<T, C> setEffect, C ent) {
		return Wrappers.cast(data.computeIfAbsent(setEffect.getKey(), e -> setEffect.getData(ent)));
	}

	@Nullable
	public <T extends ConditionalToken> T getData(TokenKey<T> setEffect) {
		return Wrappers.cast(data.get(setEffect));
	}

	@Override
	public void tick(Player player) {
		tickSinceDeath++;
		if (L2LibraryConfig.SERVER.restoreFullHealthOnRespawn.get() &&
				tickSinceDeath < 60 && player.getHealth() < player.getMaxHealth()) {
			player.setHealth(player.getMaxHealth());
		}
		data.entrySet().removeIf(e -> e.getValue().tick(player));
	}

	public boolean hasData(TokenKey<?> eff) {
		return data.containsKey(eff);
	}

}
