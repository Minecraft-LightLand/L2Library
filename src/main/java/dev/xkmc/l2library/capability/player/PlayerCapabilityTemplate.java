package dev.xkmc.l2library.capability.player;

import dev.xkmc.l2library.serial.SerialClass;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@SerialClass
public class PlayerCapabilityTemplate<T extends PlayerCapabilityTemplate<T>> {

	public Player player;
	public Level world;

	public void preInject(){

	}

	public void init(){

	}

	public T check(){
		return getThis();
	}

	@SuppressWarnings("unchecked")
	public T getThis(){
		return (T)this;
	}

	public void tick() {
	}

}
