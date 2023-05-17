package dev.xkmc.l2library.init.compat.tab;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.inventory.CurioSlot;

import java.util.ArrayList;

public class CuriosWrapper {

	private final ArrayList<CuriosSlotWrapper> list = new ArrayList<>();

	public CuriosWrapper(Player player) {
		var opt = player.getCapability(CuriosCapability.INVENTORY).resolve();
		if (opt.isEmpty()) return;
		var cap = opt.get();
		for (var ent : cap.getCurios().entrySet()) {
			var stack = ent.getValue();
			for (int i = 0; i < stack.getSlots(); i++) {
				list.add(new CuriosSlotWrapper(player, stack, i, ent.getKey()));
			}
		}
	}

	public int getSize() {
		return list.size();
	}

	public CuriosSlotWrapper get(int i) {
		return list.get(i);
	}

	public record CuriosSlotWrapper(Player player, ICurioStacksHandler cap, int index, String identifier) {

		public Slot toSlot(int x, int y) {
			return new CurioSlot(player, cap.getStacks(), index, identifier, x, y, cap.getRenders());
		}

	}

}
