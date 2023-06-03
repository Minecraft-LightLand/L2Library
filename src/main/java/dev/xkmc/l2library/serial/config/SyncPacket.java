package dev.xkmc.l2library.serial.config;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;

@SerialClass
public class SyncPacket extends SerialPacketBase {

	@SerialClass.SerialField
	public ResourceLocation id;

	@SerialClass.SerialField
	public ArrayList<PacketHandlerWithConfig.ConfigInstance> map;

	@Deprecated
	public SyncPacket() {

	}

	SyncPacket(PacketHandlerWithConfig handler, HashMap<ResourceLocation, PacketHandlerWithConfig.ConfigInstance> map) {
		this.id = handler.CHANNEL_NAME;
		this.map = new ArrayList<>(map.values());
	}

	@Override
	public void handle(NetworkEvent.Context ctx) {
		if (map != null) {
			var handler = PacketHandlerWithConfig.INTERNAL.get(id);
			handler.listener.apply(map);
		}
	}

}
