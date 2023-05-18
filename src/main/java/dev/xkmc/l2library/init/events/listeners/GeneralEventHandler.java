package dev.xkmc.l2library.init.events.listeners;

import dev.xkmc.l2library.base.menu.SpriteManager;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.data.L2TagGen;
import dev.xkmc.l2library.init.data.LangData;
import dev.xkmc.l2library.init.events.screen.base.ScreenTracker;
import dev.xkmc.l2library.init.materials.generic.GenericArmorItem;
import dev.xkmc.l2library.serial.config.PacketHandlerWithConfig;
import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static dev.xkmc.l2library.init.events.select.item.ItemConvertor.convert;

@Mod.EventBusSubscriber(modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GeneralEventHandler {

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof AbstractArrow arrow) {
			if (arrow.getOwner() instanceof Player player) {
				double cr = player.getAttributeValue(L2Library.CRIT_RATE.get());
				double cd = player.getAttributeValue(L2Library.CRIT_DMG.get());
				double strength = player.getAttributeValue(L2Library.BOW_STRENGTH.get());
				if (player.getRandom().nextDouble() < cr) {
					strength *= (1 + cd);
				}
				arrow.setBaseDamage((float) (arrow.getBaseDamage() * strength));
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onPotionTest(MobEffectEvent.Applicable event) {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;
			ItemStack stack = event.getEntity().getItemBySlot(slot);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof GenericArmorItem armor) {
					if (armor.getConfig().immuneToEffect(stack, armor, event.getEffectInstance())) {
						event.setResult(Event.Result.DENY);
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new BaseJsonReloadListener("gui/coords", map -> {
			SpriteManager.CACHE.clear();
			SpriteManager.CACHE.putAll(map);
		}));
		PacketHandlerWithConfig.addReloadListeners(event);
	}

	@SubscribeEvent
	public static void onDatapackSync(OnDatapackSyncEvent event) {
		PacketHandlerWithConfig.onDatapackSync(event);
	}

	@SubscribeEvent
	public static void serverTick(TickEvent.ServerTickEvent event) {
		RayTraceUtil.serverTick();
	}

	@SubscribeEvent
	public static void addItemToInventory(EntityItemPickupEvent event) {
		ItemStack prev = event.getItem().getItem();
		ItemStack next = convert(prev, event.getEntity());
		if (next != prev) {
			event.getItem().setItem(next);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void tooltipEvent(ItemTooltipEvent event) {
		if (event.getItemStack().is(L2TagGen.QUICK_ACCESS_VANILLA))
			event.getToolTip().add(LangData.QUICK_ACCESS.get().withStyle(ChatFormatting.GRAY));
	}

}
