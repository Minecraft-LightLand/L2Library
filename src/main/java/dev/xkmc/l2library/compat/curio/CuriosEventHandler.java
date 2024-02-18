package dev.xkmc.l2library.compat.curio;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.event.SlotModifiersUpdatedEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class CuriosEventHandler {
	private static final Map<Player, Runnable> MAP = new LinkedHashMap<>();

	public CuriosEventHandler() {
	}

	@SubscribeEvent
	public static void onSlotModifierUpdate(SlotModifiersUpdatedEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.level instanceof ServerLevel sl) {
			for (ServerPlayer player : sl.players()) {
				AbstractContainerMenu var6 = player.containerMenu;
				if (var6 instanceof BaseCuriosListMenu<?> menu) {
					if (menu.curios.entity == entity) {
						MAP.put(player, () -> openMenuWrapped(player, () -> menu.switchPage(player, menu.curios.page)));
					}
				}
			}
		}

	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
		if (!MAP.isEmpty()) {
			if (event.getEntity() instanceof ServerPlayer player) {
				Runnable run = MAP.get(player);
				if (run != null) {
					if (player.isAlive() && !player.hasDisconnected()) {
						run.run();
					}
					MAP.remove(player);
				}
			}
		}

	}

	public static void openMenuWrapped(ServerPlayer player, Runnable run) {
		AbstractContainerMenu menu = player.containerMenu;
		ItemStack stack = menu.getCarried();
		menu.setCarried(ItemStack.EMPTY);
		run.run();
		menu = player.containerMenu;
		menu.setCarried(stack);
	}

}