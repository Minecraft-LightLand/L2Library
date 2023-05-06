package dev.xkmc.l2library.init.events.click;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class SlotClickToServer extends SerialPacketBase {

	@SerialClass.SerialField
	private int index, slot, wid;
	@SerialClass.SerialField
	private ResourceLocation type;

	@Deprecated
	public SlotClickToServer() {

	}

	SlotClickToServer(ResourceLocation type, int index, int slot, int wid) {
		this.index = index;
		this.slot = slot;
		this.wid = wid;
		this.type = type;
	}

	@Override
	public void handle(NetworkEvent.Context ctx) {
		ServerPlayer player = ctx.getSender();
		if (player == null) return;
		var handler = SlotClickHandler.MAP.get(type);
		if (handler != null) {
			handler.handle(player, index, slot, wid);
		}
	}
}
