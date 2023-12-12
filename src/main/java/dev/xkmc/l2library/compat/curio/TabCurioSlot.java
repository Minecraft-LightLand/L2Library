package dev.xkmc.l2library.compat.curio;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.event.CurioEquipEvent;
import top.theillusivec4.curios.api.event.CurioUnequipEvent;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.CosmeticCurioSlot;
import top.theillusivec4.curios.common.inventory.CurioSlot;

import javax.annotation.Nonnull;

public class TabCurioSlot extends CurioSlot {

	private final String identifier;
	private final LivingEntity player;
	private final SlotContext slotContext;

	private final NonNullList<Boolean> renderStatuses;
	private final boolean canToggleRender;

	private final IDynamicStackHandler handler;
	private final int index;

	public TabCurioSlot(LivingEntity player, IDynamicStackHandler handler, int index, String identifier,
						int xPosition, int yPosition, NonNullList<Boolean> renders,
						boolean canToggleRender) {
		super(player instanceof Player pl ? pl : null, handler, index, identifier, xPosition, yPosition, renders);
		this.identifier = identifier;
		this.renderStatuses = renders;
		this.player = player;
		this.canToggleRender = canToggleRender;
		this.slotContext = new SlotContext(identifier, player, index, false, renders.get(index));
		this.setBackground(InventoryMenu.BLOCK_ATLAS,
				player.getCommandSenderWorld().isClientSide() ?
						CuriosApi.getIconHelper().getIcon(identifier)
						: new ResourceLocation(Curios.MODID, "slot/empty_curio_slot"));
		this.handler = handler;
		this.index = index;
	}

	public boolean isValid() {
		return handler.getSlots() > index;
	}

	@Override
	public @NotNull ItemStack getItem() {
		if (!isValid()) return ItemStack.EMPTY;
		return super.getItem();
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public boolean getRenderStatus() {
		return this.renderStatuses.size() > this.getSlotIndex() &&
				this.renderStatuses.get(this.getSlotIndex());
	}

	@OnlyIn(Dist.CLIENT)
	public String getSlotName() {
		return I18n.get("curios.identifier." + this.identifier);
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		if (!isValid()) return false;
		CurioEquipEvent equipEvent = new CurioEquipEvent(stack, slotContext);
		MinecraftForge.EVENT_BUS.post(equipEvent);
		Event.Result result = equipEvent.getResult();

		if (result == Event.Result.DENY) {
			return false;
		}
		return result == Event.Result.ALLOW ||
				(CuriosApi.getCuriosHelper().isStackValid(slotContext, stack) &&
						CuriosApi.getCuriosHelper().getCurio(stack).map(curio -> curio.canEquip(slotContext))
								.orElse(true) && super.mayPlace(stack));
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		if (!isValid()) return false;
		ItemStack stack = this.getItem();
		CurioUnequipEvent unequipEvent = new CurioUnequipEvent(stack, slotContext);
		MinecraftForge.EVENT_BUS.post(unequipEvent);
		Event.Result result = unequipEvent.getResult();

		if (result == Event.Result.DENY) {
			return false;
		}
		return result == Event.Result.ALLOW ||
				((stack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(stack)) &&
						CuriosApi.getCuriosHelper().getCurio(stack).map(curio -> curio.canUnequip(slotContext))
								.orElse(true) && super.mayPickup(playerIn));
	}


}
