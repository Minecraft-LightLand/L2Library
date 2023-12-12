package dev.xkmc.l2library.compat.curio;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public record CuriosSlotWrapper(LivingEntity player, ICurioStacksHandler cap, int index, String identifier) {

	public Slot toSlot(int x, int y) {
		return new TabCurioSlot(player, cap.getStacks(), index, identifier, x, y, cap.getRenders(), false);
	}

}
