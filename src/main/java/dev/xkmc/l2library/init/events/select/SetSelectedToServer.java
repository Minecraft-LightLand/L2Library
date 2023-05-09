package dev.xkmc.l2library.init.events.select;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

@SerialClass
public class SetSelectedToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public int slot;

	@SerialClass.SerialField
	public boolean isCtrlDown, isAltDown, isShiftDown;

	/**
	 * @deprecated
	 */
	@Deprecated
	public SetSelectedToServer() {
	}

	public SetSelectedToServer(int slot) {
		this.slot = slot;
		this.isCtrlDown = Screen.hasControlDown();
		this.isAltDown = Screen.hasAltDown();
		this.isShiftDown = Screen.hasShiftDown();
	}

	public void handle(Context ctx) {
		Player sender = ctx.getSender();
		if (sender != null) {
			for (var e : SelectionRegistry.getListeners()) {
				if (e.handleServerSetSelection(this, sender)) {
					break;
				}
			}
		}
	}

}
