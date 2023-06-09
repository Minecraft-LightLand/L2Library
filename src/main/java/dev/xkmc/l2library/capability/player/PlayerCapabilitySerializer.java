package dev.xkmc.l2library.capability.player;

import dev.xkmc.l2library.capability.entity.GeneralCapabilitySerializer;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@SerialClass
public class PlayerCapabilitySerializer<C extends PlayerCapabilityTemplate<C>> extends GeneralCapabilitySerializer<Player, C> {

	public final Player player;
	public final Level level;

	public PlayerCapabilitySerializer(Player player, PlayerCapabilityHolder<C> holder) {
		super(holder);
		this.player = player;
		this.level = player.level();
		handler = holder.sup.get();
		handler.world = player.level();
		handler.player = player;
	}

}

