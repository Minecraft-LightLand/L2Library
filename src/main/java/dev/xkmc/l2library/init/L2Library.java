package dev.xkmc.l2library.init;

import dev.xkmc.l2library.network.PacketHandler;
import dev.xkmc.l2library.network.SyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(L2Library.MODID)
public class L2Library {

	public static final String MODID = "l2library";

	public static final PacketHandler PACKET_HANDLER = new PacketHandler(new ResourceLocation(MODID, "main"), 1,
			e->e.create(SyncPacket.class, PLAY_TO_CLIENT));

	public L2Library() {
		FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
		IEventBus bus = ctx.getModEventBus();
		MinecraftForge.EVENT_BUS.register(GenericEventHandler.class);
		bus.addListener(PacketHandler::setup);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> L2Client.onCtorClient(bus, MinecraftForge.EVENT_BUS));
	}

}
