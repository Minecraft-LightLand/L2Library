package dev.xkmc.l2library.init.events.listeners;

import com.mojang.blaze3d.platform.InputConstants;
import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.L2LibraryConfig;
import dev.xkmc.l2library.init.data.L2Keys;
import dev.xkmc.l2library.init.events.select.SelectionRegistry;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2library.util.raytrace.EntityTarget;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.client.Minecraft;
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
		var sel = SelectionRegistry.getClientActiveListener(player);
		if (sel.isEmpty()) return;
		for (L2Keys k : L2Keys.values()) {
			if (k.map.getKey().getValue() == event.getKey() && event.getAction() == InputConstants.PRESS) {
				sel.get().handleClientKey(k, player);
				return;
			}
		}
		for (int i = 0; i < 9; i++) {
			if (sel.get().handleClientNumericKey(i, Minecraft.getInstance().options.keyHotbarSlots[i]::consumeClick)) {
				return;
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
		var sel = SelectionRegistry.getClientActiveListener(player);
		if (sel.isEmpty()) return;
		if (L2LibraryConfig.CLIENT.selectionScrollRequireShift.get() && !player.isShiftKeyDown()) return;
		if (sel.get().handleClientScroll(event.diff, player)) {
			event.setCanceled(true);
		}
	}

}
