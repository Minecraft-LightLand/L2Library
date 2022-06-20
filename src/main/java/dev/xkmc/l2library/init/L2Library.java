package dev.xkmc.l2library.init;

import dev.xkmc.l2library.capability.player.PlayerCapToClient;
import dev.xkmc.l2library.capability.player.PlayerCapabilityEvents;
import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.effects.EffectSyncEvents;
import dev.xkmc.l2library.effects.EffectToClient;
import dev.xkmc.l2library.init.events.AttackEventHandler;
import dev.xkmc.l2library.init.events.GenericEventHandler;
import dev.xkmc.l2library.menu.tabs.contents.AttributeEntry;
import dev.xkmc.l2library.network.PacketHandler;
import dev.xkmc.l2library.network.SyncPacket;
import dev.xkmc.l2library.serial.handler.Handlers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
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
		event.enqueueWork(() -> {
			AttributeEntry.add(() -> Attributes.MAX_HEALTH, false, 1000);
			AttributeEntry.add(() -> Attributes.ARMOR, false, 2000);
			AttributeEntry.add(() -> Attributes.ARMOR_TOUGHNESS, false, 3000);
			AttributeEntry.add(() -> Attributes.KNOCKBACK_RESISTANCE, false, 4000);
			AttributeEntry.add(() -> Attributes.MOVEMENT_SPEED, false, 5000);
			AttributeEntry.add(() -> Attributes.ATTACK_DAMAGE, false, 6000);
			AttributeEntry.add(() -> Attributes.ATTACK_SPEED, false, 7000);
			AttributeEntry.add(ForgeMod.REACH_DISTANCE, false, 8000);
			AttributeEntry.add(ForgeMod.ATTACK_RANGE, false, 9000);
			AttributeEntry.add(() -> Attributes.LUCK, false, 10000);
		});
	}

}
