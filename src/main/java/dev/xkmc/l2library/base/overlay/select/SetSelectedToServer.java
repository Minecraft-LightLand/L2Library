package dev.xkmc.l2library.base.overlay.select;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.SerialClass.SerialField;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

@SerialClass
public class SetSelectedToServer extends SerialPacketBase {

	@SerialField
	private int slot;

	/**
	 * @deprecated
	 */
	@Deprecated
	public SetSelectedToServer() {
	}

	public SetSelectedToServer(int slot) {
		this.slot = slot;
	}

	public void handle(Context ctx) {
		Player sender = ctx.getSender();
		if (sender != null) {
			IItemSelector sel = IItemSelector.getSelection(sender);
			if (sel != null)
				sel.swap(sender, slot);
		}
	}

}
