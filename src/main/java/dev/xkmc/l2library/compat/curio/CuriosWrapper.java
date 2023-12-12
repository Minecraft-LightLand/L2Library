package dev.xkmc.l2library.compat.curio;

import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

public class CuriosWrapper extends BaseCuriosWrapper {

	private final ArrayList<CuriosSlotWrapper> list = new ArrayList<>();

	public CuriosWrapper(LivingEntity player, int page) {
		super(player);
		int max = 6;
		Optional<ICuriosItemHandler> opt = player.getCapability(CuriosCapability.INVENTORY).resolve();
		this.page = page;
		if (opt.isEmpty()) {
			total = 0;
			return;
		}
		var cap = opt.get();
		int offset = page * max * 9;
		int count = 0;
		for (var ent : cap.getCurios().entrySet()) {
			var stack = ent.getValue();
			if (!stack.isVisible()) continue;
			for (int i = 0; i < stack.getSlots(); i++) {
				count++;
				if (offset > 0) {
					offset--;
				} else {
					if (list.size() < max * 9) {
						list.add(new CuriosSlotWrapper(player, stack, i, ent.getKey()));
					}
				}
			}
		}
		this.total = (count - 1) / (max * 9) + 1;
	}

	public int getSize() {
		return list.size();
	}

	@Override
	public int getRows() {
		return list.isEmpty() ? 0 : (list.size() - 1) / 9 + 1;
	}

	@Nullable
	public CuriosSlotWrapper getSlotAtPosition(int i) {
		if (i < 0 || i >= list.size()) return null;
		return list.get(i);
	}

}
