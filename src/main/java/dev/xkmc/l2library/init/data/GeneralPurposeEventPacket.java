package dev.xkmc.l2library.init.data;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class GeneralPurposeEventPacket extends SerialPacketBase {

	@SerialClass.SerialField
	public GeneralPurposeEvent event;

	@Deprecated
	public GeneralPurposeEventPacket() {

	}

	public GeneralPurposeEventPacket(GeneralPurposeEvent event) {
		this.event = event;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		if (context.getSender() != null) {
			event.invoke(context.getSender());
		}
	}
}
