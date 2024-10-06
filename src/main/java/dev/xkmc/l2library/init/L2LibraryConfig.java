package dev.xkmc.l2library.init;

import dev.xkmc.l2core.util.ConfigInit;
import net.neoforged.neoforge.common.ModConfigSpec;

public class L2LibraryConfig {

	public static class Server extends ConfigInit {

		public final ModConfigSpec.BooleanValue restoreFullHealthOnRespawn;

		Server(Builder builder) {
			markL2();
			restoreFullHealthOnRespawn = builder.text("Restore full health on respawn")
					.define("restoreFullHealthOnRespawn", true);
		}

	}

	public static final Server SERVER = L2Library.REGISTRATE.registerSynced(Server::new);

	public static void init() {
	}

}
