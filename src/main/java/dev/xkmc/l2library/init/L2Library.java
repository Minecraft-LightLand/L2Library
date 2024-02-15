package dev.xkmc.l2library.init;

import dev.xkmc.l2library.init.reg.L2Registrate;
import dev.xkmc.l2library.base.effects.ClientEffectCap;
import dev.xkmc.l2library.base.effects.EffectToClient;
import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import dev.xkmc.l2library.capability.conditionals.ConditionalData;
import dev.xkmc.l2library.capability.conditionals.TokenToClient;
import dev.xkmc.l2library.capability.player.PlayerCapToClient;
import dev.xkmc.l2library.serial.config.ConfigTypeEntry;
import dev.xkmc.l2library.serial.config.PacketHandlerWithConfig;
import dev.xkmc.l2library.serial.config.SyncPacket;
import dev.xkmc.l2library.util.raytrace.TargetSetPacket;
import dev.xkmc.l2serial.serialization.custom_handler.Handlers;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(L2Library.MODID)
@Mod.EventBusSubscriber(modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class L2Library {

	public static final String MODID = "l2library";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);

	public static final PacketHandlerWithConfig PACKET_HANDLER = new PacketHandlerWithConfig(MODID, 1,
			e -> e.create(SyncPacket.class),
			e -> e.create(EffectToClient.class),
			e -> e.create(PlayerCapToClient.class),
			e -> e.create(TargetSetPacket.class),
			e -> e.create(TokenToClient.class)
	);

	public static final ConfigTypeEntry<MenuLayoutConfig> MENU_LAYOUT =
			new ConfigTypeEntry<>(PACKET_HANDLER, "menu_layout", MenuLayoutConfig.class);

	public L2Library() {
		Handlers.register();
		L2LibraryConfig.init();
		ConditionalData.register();
		ClientEffectCap.register();
	}

	public static void onPacketReg(RegisterPayloadHandlerEvent event) {
		PACKET_HANDLER.register(event);
	}

	/* TODO
	@SubscribeEvent
	public static void registerCaps(RegisterCapabilitiesEvent event) {
		for (GeneralCapabilityHolder<?, ?> holder : GeneralCapabilityHolder.INTERNAL_MAP.values()) {
			event.register(holder.holder_class);
		}
	}*/

	@SubscribeEvent
	public static void registerRecipeSerializers(RegisterEvent event) {

		if (event.getRegistryKey().equals(Registries.RECIPE_SERIALIZER)) {

		}
	}

}
