package dev.xkmc.l2library.base.effects;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

@SerialClass
public class EffectToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public UUID entity;
	@SerialClass.SerialField
	public MobEffect effect;
	@SerialClass.SerialField
	public boolean exist;
	@SerialClass.SerialField
	public int level;

	public EffectToClient(UUID entity, MobEffect effect, boolean exist, int level) {
		this.entity = entity;
		this.effect = effect;
		this.exist = exist;
		this.level = level;
	}

	@Deprecated
	public EffectToClient() {
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		EffectSyncEvents.sync(this);
	}
}
