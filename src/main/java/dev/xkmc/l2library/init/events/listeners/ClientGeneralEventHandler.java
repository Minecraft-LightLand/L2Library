package dev.xkmc.l2library.init.events.listeners;

import dev.xkmc.l2library.base.overlay.select.IItemSelector;
import dev.xkmc.l2library.base.overlay.select.ItemSelectionOverlay;
import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.L2LibraryConfig;
import dev.xkmc.l2library.init.data.L2Keys;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2library.util.raytrace.EntityTarget;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientGeneralEventHandler {

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		for (EntityTarget target : EntityTarget.LIST) {
			target.tickRender();
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(ClientPlayerNetworkEvent.Clone event) {
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			CompoundTag tag0 = holder.getCache(event.getOldPlayer());
			Wrappers.run(() -> TagCodec.fromTag(tag0, holder.cls, holder.get(event.getNewPlayer()), f -> true));
			holder.get(event.getNewPlayer());
		}
	}

	@SubscribeEvent
	public static void keyEvent(InputEvent.Key event) {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;
		if (ItemSelectionOverlay.INSTANCE.isScreenOn()) {
			IItemSelector sel = IItemSelector.getSelection(player);
			if (sel == null) return;
			if (event.getKey() == L2Keys.UP.map.getKey().getValue() && event.getAction() == 1) {
				sel.move(-1);
			} else if (event.getKey() == L2Keys.DOWN.map.getKey().getValue() && event.getAction() == 1) {
				sel.move(1);
			}
		}
	}

	private static double scroll;

	@SubscribeEvent
	public static void scrollEvent(InputEvent.MouseScrollingEvent event) {
		double scroll_tick = L2LibraryConfig.CLIENT.scrollTick.get();
		double delta = event.getScrollDelta();
		scroll += delta;
		int diff = 0;
		if (scroll > scroll_tick) {
			diff = (int) Math.floor(scroll / scroll_tick);
			scroll -= diff * scroll_tick;
		} else if (scroll < -scroll_tick) {
			diff = -(int) Math.floor(-scroll / scroll_tick);
			scroll -= diff * scroll_tick;
		}
		if (MinecraftForge.EVENT_BUS.post(new FineScrollEvent(diff))) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void fineScroll(FineScrollEvent event) {
		LocalPlayer player = Proxy.getClientPlayer();
		if (player == null) return;
		if (!ItemSelectionOverlay.INSTANCE.isScreenOn()) return;
		if (L2LibraryConfig.CLIENT.selectionScrollRequireShift.get() && !player.isShiftKeyDown()) return;
		IItemSelector sel = IItemSelector.getSelection(Proxy.getClientPlayer());
		if (sel == null) return;
		sel.move(event.diff);
		event.setCanceled(true);
	}


}
