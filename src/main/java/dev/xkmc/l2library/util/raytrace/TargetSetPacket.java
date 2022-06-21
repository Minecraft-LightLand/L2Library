package dev.xkmc.l2library.util.raytrace;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.UUID;

@SerialClass
public class TargetSetPacket extends SerialPacketBase {

	@SerialClass.SerialField
	public UUID player, target;

	public TargetSetPacket(UUID player, @Nullable UUID target) {
		this.player = player;
		this.target = target;
	}

	@Deprecated
	public TargetSetPacket() {
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		RayTraceUtil.sync(this);
	}
}
