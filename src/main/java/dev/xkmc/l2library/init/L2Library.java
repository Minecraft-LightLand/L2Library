package dev.xkmc.l2library.init;

import dev.xkmc.l2library.capability.player.PlayerCapToClient;
import dev.xkmc.l2library.capability.player.PlayerCapabilityEvents;
import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.effects.EffectSyncEvents;
import dev.xkmc.l2library.effects.EffectToClient;
import dev.xkmc.l2library.init.events.AttackEventHandler;
import dev.xkmc.l2library.init.events.GenericEventHandler;
import dev.xkmc.l2library.network.PacketHandler;
import dev.xkmc.l2library.network.SyncPacket;
import dev.xkmc.l2library.serial.handler.Handlers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(L2Library.MODID)
public class L2Library {

	public static final String MODID = "l2library";
	public static final Logger LOGGER = LogManager.getLogger();

	public static final PacketHandler PACKET_HANDLER = new PacketHandler(new ResourceLocation(MODID, "main"), 1,
			e -> e.create(SyncPacket.class, PLAY_TO_CLIENT),
			e -> e.create(EffectToClient.class, PLAY_TO_CLIENT),
			e -> e.create(PlayerCapToClient.class, PLAY_TO_CLIENT));

	public L2Library() {
		Handlers.register();
		FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
		IEventBus bus = ctx.getModEventBus();
		MinecraftForge.EVENT_BUS.register(GenericEventHandler.class);
		MinecraftForge.EVENT_BUS.register(EffectSyncEvents.class);
		MinecraftForge.EVENT_BUS.register(PlayerCapabilityEvents.class);
		MinecraftForge.EVENT_BUS.register(AttackEventHandler.class);
		bus.addListener(L2Library::registerCaps);
		bus.addListener(PacketHandler::setup);
		bus.addListener(L2Library::setup);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> L2Client.onCtorClient(bus, MinecraftForge.EVENT_BUS));
	}

	public static void registerCaps(RegisterCapabilitiesEvent event) {
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			event.register(holder.cls);
		}
	}

	public static void setup(FMLCommonSetupEvent event) {

	}

}
