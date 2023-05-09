package dev.xkmc.l2library.init.events.select;

import dev.xkmc.l2library.init.data.L2Keys;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public interface ISelectionListener {

	boolean handleServerSetSelection(SetSelectedToServer setSelectedToServer, Player sender);

	boolean handleClientScroll(int diff, LocalPlayer player);

	boolean handleClientKey(L2Keys k, LocalPlayer player);
}
