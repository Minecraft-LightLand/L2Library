package dev.xkmc.l2library.base.menu;

import dev.xkmc.l2library.util.code.Wrappers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.*;

/**
 * Base Class for ContainerMenu.
 * Containers multiple helper functions.
 */
public class BaseContainerMenu<T extends BaseContainerMenu<T>> extends AbstractContainerMenu {

	private record SlotKey(String name, int i, int j) {

		private static final Comparator<SlotKey> COMPARATOR;

		static {
			Comparator<SlotKey> comp = Comparator.comparing(SlotKey::name);
			comp = comp.thenComparingInt(SlotKey::i);
			comp = comp.thenComparingInt(SlotKey::j);
			COMPARATOR = comp;
		}

	}

	/**
	 * simple container that prevents looping change
	 */
	public static class BaseContainer<T extends BaseContainerMenu<T>> extends SimpleContainer {

		protected final T parent;
		private boolean updating = false;

		public BaseContainer(int size, T menu) {
			super(size);
			parent = menu;
		}

		@Override
		public void setChanged() {
			super.setChanged();
			if (!updating) {
				updating = true;
				parent.slotsChanged(this);
				updating = false;
			}
		}

	}

	/**
	 * return items in the slot to player
	 */
	public static void clearSlot(Player pPlayer, Container pContainer, int index) {
		if (!pPlayer.isAlive() || pPlayer instanceof ServerPlayer && ((ServerPlayer) pPlayer).hasDisconnected()) {
			pPlayer.drop(pContainer.removeItemNoUpdate(index), false);
		} else {
			Inventory inventory = pPlayer.getInventory();
			if (inventory.player instanceof ServerPlayer) {
				inventory.placeItemBackInInventory(pContainer.removeItemNoUpdate(index));
			}
		}
	}

	public final Inventory inventory;
	public final SimpleContainer container;
	public final SpriteManager sprite;
	protected int added = 0;
	protected final boolean isVirtual;

	private boolean updating = false;

	private final Map<SlotKey, Slot> slotMap = new TreeMap<>(SlotKey.COMPARATOR);

	/**
	 * This contructor will bind player inventory first, so player inventory has lower slot index.
	 *
	 * @param type      registered menu type
	 * @param wid       window id
	 * @param plInv     player inventory
	 * @param manager   sprite manager used for slot positioning and rendering
	 * @param factory   container supplier
	 * @param isVirtual true if the slots should be cleared and item returned to player on menu close.
	 */
	protected BaseContainerMenu(MenuType<?> type, int wid, Inventory plInv, SpriteManager manager, Function<T, SimpleContainer> factory, boolean isVirtual) {
		super(type, wid);
		this.inventory = plInv;
		container = factory.apply(Wrappers.cast(this));
		sprite = manager;
		int x = manager.getPlInvX();
		int y = manager.getPlInvY();
		this.bindPlayerInventory(plInv, x, y);
		this.isVirtual = isVirtual;
	}

	/**
	 * Binds player inventory. Should not be called by others, but permits override.
	 */
	protected void bindPlayerInventory(Inventory plInv, int x, int y) {
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlot(createSlot(plInv, j + i * 9 + 9, x + j * 18, y + i * 18));
		for (int k = 0; k < 9; ++k)
			addSlot(createSlot(plInv, k, x + k * 18, y + 58));
	}

	/**
	 * Used by bindPlayerInventory only. Create slots as needed. Some slots could be locked.
	 */
	protected Slot createSlot(Inventory inv, int slot, int x, int y) {
		return shouldLock(inv, slot) ? new SlotLocked(inv, slot, x, y) : new Slot(inv, slot, x, y);
	}

	/**
	 * Lock slots you don't want players modifying, such as the slot player is opening backpack in.
	 */
	protected boolean shouldLock(Inventory inv, int slot) {
		return false;
	}

	/**
	 * Add new slots, with item input predicate
	 */
	protected void addSlot(String name, Predicate<ItemStack> pred) {
		sprite.getSlot(name, (x, y) -> new PredSlot(container, added++, x, y, pred), this::addSlot);
	}

	/**
	 * Add new slots, with index-sensitive item input predicate.
	 * The index here is relative to the first slot added by this method.
	 */
	protected void addSlot(String name, BiPredicate<Integer, ItemStack> pred) {
		int current = added;
		sprite.getSlot(name, (x, y) -> {
			var ans = new PredSlot(container, added, x, y, e -> pred.test(added - current, e));
			added++;
			return ans;
		}, this::addSlot);
	}

	/**
	 * Add new slots, with other modifications to the slot.
	 */
	protected void addSlot(String name, Predicate<ItemStack> pred, Consumer<PredSlot> modifier) {
		sprite.getSlot(name, (x, y) -> {
			PredSlot s = new PredSlot(container, added++, x, y, pred);
			modifier.accept(s);
			return s;
		}, this::addSlot);
	}

	/**
	 * Add new slots, with index-sensitive modifications to the slot.
	 * The index here is relative to the first slot added by this method.
	 */
	protected void addSlot(String name, BiPredicate<Integer, ItemStack> pred, BiConsumer<Integer, PredSlot> modifier) {
		int current = added;
		sprite.getSlot(name, (x, y) -> {
			int i = added - current;
			var ans = new PredSlot(container, added, x, y, e -> pred.test(i, e));
			modifier.accept(i, ans);
			added++;
			return ans;
		}, this::addSlot);
	}

	/**
	 * internal add slot
	 */
	protected void addSlot(String name, int i, int j, Slot slot) {
		slotMap.put(new SlotKey(name, i, j), slot);
		this.addSlot(slot);
	}

	/**
	 * get the slot by name and id in the grid
	 */
	protected Slot getSlot(String name, int i, int j) {
		return slotMap.get(new SlotKey(name, i, j));
	}

	/**
	 * get a slot as PredSlot, as most slots should be
	 */
	protected PredSlot getAsPredSlot(String name, int i, int j) {
		return (PredSlot) getSlot(name, i, j);
	}

	/**
	 * get a slot as PredSlot, as most slots should be
	 */
	protected PredSlot getAsPredSlot(String name) {
		return (PredSlot) getSlot(name, 0, 0);
	}

	@Override
	public ItemStack quickMoveStack(Player pl, int id) {
		ItemStack stack = slots.get(id).getItem();
		int n = container.getContainerSize();
		if (id >= 36) {
			moveItemStackTo(stack, 0, 36, true);
		} else {
			moveItemStackTo(stack, 36, 36 + n, false);
		}
		container.setChanged();
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return player.isAlive();
	}

	@Override
	public void removed(Player player) {
		if (isVirtual && !player.level.isClientSide())
			clearContainerFiltered(player, container);
		super.removed(player);
	}

	/**
	 * return true (and when isVirtual is true), clear the corresponding slot on menu close.
	 */
	protected boolean shouldClear(Container container, int slot) {
		return isVirtual;
	}

	/**
	 * clear slots using shouldClear
	 */
	protected void clearContainerFiltered(Player player, Container container) {
		if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer) player).hasDisconnected()) {
			for (int j = 0; j < container.getContainerSize(); ++j) {
				if (shouldClear(container, j)) {
					player.drop(container.removeItemNoUpdate(j), false);
				}
			}
		} else {
			Inventory inventory = player.getInventory();
			for (int i = 0; i < container.getContainerSize(); ++i) {
				if (shouldClear(container, i)) {
					if (inventory.player instanceof ServerPlayer) {
						inventory.placeItemBackInInventory(container.removeItemNoUpdate(i));
					}
				}
			}

		}
	}

	@Override
	public void slotsChanged(Container cont) {
		if (inventory.player.level.isClientSide()) {
			super.slotsChanged(cont);
		} else {
			if (!updating) {
				updating = true;
				securedServerSlotChange(cont);
				updating = false;
			}
			super.slotsChanged(cont);
		}
	}

	/**
	 * server side only slot change detector that will not be called recursively.
	 */
	protected void securedServerSlotChange(Container cont) {

	}

}
