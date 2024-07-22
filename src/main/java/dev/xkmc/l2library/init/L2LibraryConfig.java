package dev.xkmc.l2library.init;

import dev.xkmc.l2core.util.ConfigInit;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

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
