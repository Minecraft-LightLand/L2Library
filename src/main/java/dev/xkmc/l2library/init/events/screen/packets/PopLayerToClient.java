package dev.xkmc.l2library.init.events.screen.packets;

import dev.xkmc.l2library.init.events.screen.base.LayerPopType;
import dev.xkmc.l2library.init.events.screen.base.ScreenTrackerClient;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class PopLayerToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public LayerPopType type;

	@SerialClass.SerialField
	public int wid;

	@Deprecated
	public PopLayerToClient() {

	}

	public PopLayerToClient(LayerPopType type, int wid) {
		this.type = type;
		this.wid = wid;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ScreenTrackerClient.clientPop(type, wid);
	}

}
