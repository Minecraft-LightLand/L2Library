package dev.xkmc.l2library.init.events.select.item;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2library.base.overlay.ItemSelSideBar;
import dev.xkmc.l2library.base.overlay.SideBar;
import dev.xkmc.l2library.base.overlay.TextBox;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemSelectionOverlay extends ItemSelSideBar<ItemSelectionOverlay.ItemSelSignature> {

	public static final ItemSelectionOverlay INSTANCE = new ItemSelectionOverlay();

	private static final ResourceLocation EMPTY = new ResourceLocation("empty");

	public record ItemSelSignature(ResourceLocation id, int val) implements Signature<ItemSelSignature> {

		@Override
		public boolean shouldRefreshIdle(SideBar<?> sideBar, @Nullable ItemSelectionOverlay.ItemSelSignature old) {
			return !equals(old);
		}

	}

	private ItemSelectionOverlay() {
		super(40, 3);
	}

	@Override
	public Pair<List<ItemStack>, Integer> getItems() {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return Pair.of(List.of(), 0);
		IItemSelector sel = IItemSelector.getSelection(player);
		assert sel != null;
		return Pair.of(sel.getDisplayList(), sel.getIndex(player));
	}

	@Override
	public boolean isAvailable(ItemStack itemStack) {
		return true;
	}

	@Override
	public boolean onCenter() {
		return false;
	}

	@Override
	public ItemSelSignature getSignature() {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return new ItemSelSignature(EMPTY, 0);
		IItemSelector sel = IItemSelector.getSelection(Proxy.getClientPlayer());
		if (sel == null) return new ItemSelSignature(EMPTY, 0);
		return new ItemSelSignature(sel.getID(), sel.getIndex(player));
	}

	@Override
	public boolean isScreenOn() {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return false;
		return ItemSelectionListener.INSTANCE.isClientActive(player);
	}

	@Override
	protected void renderEntry(Context ctx, ItemStack stack, int i, int selected) {
		float y = 18 * i + ctx.y0();
		renderSelection(ctx.x0(), y, 64, isAvailable(stack), selected == i);
		if (ease_time == max_ease) {
			TextBox box = new TextBox(ctx.width(), ctx.height(), 0, 1, (int) (ctx.x0() + 22), (int) (y + 8), -1);
			box.renderLongText(ctx.gui(), ctx.pose(), List.of(stack.getHoverName()));
		}
		ctx.renderItem(stack, (int) ctx.x0(), (int) y);
	}

	protected float getXOffset(int width) {
		float progress = (max_ease - ease_time) / max_ease;
		return this.onCenter() ? width / 2f - 54 - 1 - progress * (float) width / 2.0F : 2 - progress * 20.0F;
	}

	protected float getYOffset(int height) {
		LocalPlayer player = Proxy.getClientPlayer();
		assert player != null;
		IItemSelector sel = IItemSelector.getSelection(player);
		assert sel != null;
		return height / 2f - 9 * sel.getList().size() + 1;
	}

}
