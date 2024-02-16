package dev.xkmc.l2library.init;

import dev.xkmc.l2library.base.effects.EffectToClient;
import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import dev.xkmc.l2library.capability.conditionals.TokenToClient;
import dev.xkmc.l2library.capability.player.PlayerCapToClient;
import dev.xkmc.l2library.init.reg.L2LibReg;
import dev.xkmc.l2library.serial.config.ConfigTypeEntry;
import dev.xkmc.l2library.serial.config.PacketHandlerWithConfig;
import dev.xkmc.l2library.serial.config.SyncPacket;
import dev.xkmc.l2library.util.raytrace.TargetSetPacket;
import dev.xkmc.l2serial.serialization.custom_handler.Handlers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(L2Library.MODID)
@Mod.EventBusSubscriber(modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class L2Library {

	public static final String MODID = "l2library";
	public static final Logger LOGGER = LogManager.getLogger();

	// TODO public static final L2Registrate REGISTRATE = new L2Registrate(MODID);

	public static final PacketHandlerWithConfig PACKET_HANDLER = new PacketHandlerWithConfig(MODID, 1,
			e -> e.create(SyncPacket.class),
			e -> e.create(EffectToClient.class),
			e -> e.create(PlayerCapToClient.class),
			e -> e.create(TargetSetPacket.class),
			e -> e.create(TokenToClient.class)
	);

	public static final ConfigTypeEntry<MenuLayoutConfig> MENU_LAYOUT =
			new ConfigTypeEntry<>(PACKET_HANDLER, "menu_layout", MenuLayoutConfig.class);

	public L2Library(IEventBus bus) {
		Handlers.register();
		L2LibReg.register(bus);
		L2LibraryConfig.init();
	}

	@SubscribeEvent
	public static void onPacketReg(RegisterPayloadHandlerEvent event) {
		PACKET_HANDLER.register(event);
	}

}
