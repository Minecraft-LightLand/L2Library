package dev.xkmc.l2library.compat.curio;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class OpenCuriosPacket extends SerialPacketBase {

	@SerialClass.SerialField
	public OpenCurioHandler event;

	@Deprecated
	public OpenCuriosPacket() {

	}

	public OpenCuriosPacket(OpenCurioHandler event) {
		this.event = event;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		if (context.getSender() != null) {
			event.invoke(context.getSender());
		}
	}
}
