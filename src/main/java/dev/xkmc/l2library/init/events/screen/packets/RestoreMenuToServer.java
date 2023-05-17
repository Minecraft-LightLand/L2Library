package dev.xkmc.l2library.init.events.screen.packets;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.events.screen.base.ClientCloseResult;
import dev.xkmc.l2library.init.events.screen.base.ScreenTracker;
import dev.xkmc.l2library.init.events.screen.triggers.ExitMenuTrigger;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class RestoreMenuToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public int wid;

	@Deprecated
	public RestoreMenuToServer() {
	}

	public RestoreMenuToServer(int wid) {
		this.wid = wid;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ServerPlayer player = context.getSender();
		if (player == null) return;
		if (wid < 0) {
			if (wid == ClientCloseResult.POP_ALL.id) {
				ExitMenuTrigger.EXIT_MENU.trigger(player, true);
			}
			ScreenTracker.removeAll(player);
			return;
		}
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId != wid || !ScreenTracker.get(player).serverRestore(player, wid)) {
			L2Library.PACKET_HANDLER.toClientPlayer(new SetScreenToClient(ScreenType.NONE), player);
		}
		ExitMenuTrigger.EXIT_MENU.trigger(player, false);
	}

}
