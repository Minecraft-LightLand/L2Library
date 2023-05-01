package dev.xkmc.l2library.base.overlay.select;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2library.util.annotation.ServerOnly;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class IItemSelector {

	private static final List<IItemSelector> LIST = new ArrayList<>();

	@Nullable
	public static IItemSelector getSelection(Player player) {
		for (var sel : LIST) {
			if (sel.test(player.getMainHandItem()))
				return sel;
			if (sel.test(player.getOffhandItem()))
				return sel;
		}
		return null;
	}

	public final int index;

	public IItemSelector() {
		index = LIST.size();
		LIST.add(this);
	}

	public abstract boolean test(ItemStack stack);

	@ServerOnly
	public void swap(Player sender, int index) {
		index = (index + getList().size()) % getList().size();
		if (index < 0) return;
		if (test(sender.getMainHandItem())) {
			ItemStack stack = getList().get(index).copy();
			stack.setCount(sender.getMainHandItem().getCount());
			sender.setItemInHand(InteractionHand.MAIN_HAND, stack);
		} else if (test(sender.getOffhandItem())) {
			ItemStack stack = getList().get(index).copy();
			stack.setCount(sender.getOffhandItem().getCount());
			sender.setItemInHand(InteractionHand.OFF_HAND, stack);
		}
	}

	public abstract int getIndex(Player player);

	@OnlyIn(Dist.CLIENT)
	public void move(int i) {
		int index = getIndex(Proxy.getClientPlayer());
		while (i < 0) i += getList().size();
		index = (index + i) % getList().size();
		L2Library.PACKET_HANDLER.toServer(new SetSelectedToServer(index));
	}

	public abstract List<ItemStack> getList();

	public List<ItemStack> getDisplayList() {
		return getList();
	}

}
