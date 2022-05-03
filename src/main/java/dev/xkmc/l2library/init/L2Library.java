package dev.xkmc.l2library.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(L2Library.MODID)
public class L2Library {

	public static final String MODID = "l2library";

	public L2Library() {
		FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
		IEventBus bus = ctx.getModEventBus();
		MinecraftForge.EVENT_BUS.register(GenericEventHandler.class);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> L2Client.onCtorClient(bus, MinecraftForge.EVENT_BUS));
	}

}
