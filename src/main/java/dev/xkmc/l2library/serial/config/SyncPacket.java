package dev.xkmc.l2library.serial.config;

import com.google.gson.JsonElement;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;

@SerialClass
public class SyncPacket extends SerialPacketBase {

	@SerialClass.SerialField
	public ResourceLocation id;

	@SerialClass.SerialField
	public HashMap<ResourceLocation, JsonElement> map = null;

	@Deprecated
	public SyncPacket() {

	}

	SyncPacket(PacketHandlerWithConfig handler, HashMap<ResourceLocation, JsonElement> map) {
		this.id = handler.CHANNEL_NAME;
		this.map = map;
	}

	@Override
	public void handle(NetworkEvent.Context ctx) {
		if (map != null) {
			var handler = PacketHandlerWithConfig.INTERNAL.get(id);
			handler.listener_before.forEach(Runnable::run);
			handler.listener.apply(map);
			handler.configs = map;
			handler.listener_after.forEach(Runnable::run);
		}
	}

}
