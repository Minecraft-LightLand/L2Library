package dev.xkmc.l2library.init;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class L2LibraryConfig {

	public static class Client {

		public final ModConfigSpec.DoubleValue infoAlpha;
		public final ModConfigSpec.IntValue infoAnchor;
		public final ModConfigSpec.DoubleValue infoMaxWidth;

		public final ModConfigSpec.BooleanValue selectionDisplayRequireShift;
		public final ModConfigSpec.BooleanValue selectionScrollRequireShift;


		Client(ModConfigSpec.Builder builder) {
			infoAlpha = builder.comment("Info background transparency. 1 means opaque.")
					.defineInRange("infoAlpha", 0.5, 0, 1);
			infoAnchor = builder.comment("Info alignment. 0 means top. 1 means middle. 2 means bottom.")
					.defineInRange("infoAnchor", 1, 0, 2);
			infoMaxWidth = builder.comment("Info max width. 0.5 means half screen. default: 0.3")
					.defineInRange("infoMaxWidth", 0.3, 0, 0.5);

			selectionDisplayRequireShift = builder.comment("Render Selection only when pressing shift")
					.define("selectionDisplayRequireShift", false);
			selectionScrollRequireShift = builder.comment("Scroll for selection only when pressing shift")
					.define("selectionScrollRequireShift", true);


		}

	}

	public static class Server {

		public final ModConfigSpec.BooleanValue restoreFullHealthOnRespawn;

		Server(ModConfigSpec.Builder builder) {
			restoreFullHealthOnRespawn = builder.comment("Restore full health on respawn")
					.define("restoreFullHealthOnRespawn", true);
		}

	}

	public static final ModConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;

	public static final ModConfigSpec SERVER_SPEC;
	public static final Server SERVER;

	static {
		final Pair<Client, ModConfigSpec> client = new ModConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = client.getRight();
		CLIENT = client.getLeft();

		final Pair<Server, ModConfigSpec> server = new ModConfigSpec.Builder().configure(Server::new);
		SERVER_SPEC = server.getRight();
		SERVER = server.getLeft();
	}

	/**
	 * Registers any relevant listeners for config
	 */
	public static void init() {
		register(ModConfig.Type.CLIENT, CLIENT_SPEC);
		register(ModConfig.Type.SERVER, SERVER_SPEC);
	}

	private static void register(ModConfig.Type type, IConfigSpec<?> spec) {
		var mod = ModLoadingContext.get().getActiveContainer();
		String path = "l2_configs/" + mod.getModId() + "-" + type.extension() + ".toml";
		ModLoadingContext.get().registerConfig(type, spec, path);
	}


}
