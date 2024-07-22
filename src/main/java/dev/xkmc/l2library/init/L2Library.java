package dev.xkmc.l2library.init;

import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2library.content.raytrace.TargetSetPacket;
import dev.xkmc.l2serial.network.PacketHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(L2Library.MODID)
public class L2Library {

	public static final String MODID = "l2library";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);

	public static final PacketHandler PACKET_HANDLER = new PacketHandler(MODID, 1,
			e -> e.create(TargetSetPacket.class, PacketHandler.NetDir.PLAY_TO_SERVER)
	);

	public L2Library(IEventBus bus) {
		L2LibraryConfig.init();
	}

}
