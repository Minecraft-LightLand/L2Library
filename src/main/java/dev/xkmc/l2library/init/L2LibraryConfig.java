package dev.xkmc.l2library.init;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class L2LibraryConfig {

	public static class Client {

		public final ForgeConfigSpec.DoubleValue infoAlpha;
		public final ForgeConfigSpec.IntValue infoAnchor;
		public final ForgeConfigSpec.DoubleValue infoMaxWidth;

		public final ForgeConfigSpec.BooleanValue showTabs;

		public final ForgeConfigSpec.BooleanValue selectionDisplayRequireShift;
		public final ForgeConfigSpec.BooleanValue selectionScrollRequireShift;
		public final ForgeConfigSpec.DoubleValue scrollTick;


		Client(ForgeConfigSpec.Builder builder) {
			infoAlpha = builder.comment("Info background transparency. 1 means opaque.")
					.defineInRange("infoAlpha", 0.5, 0, 1);
			infoAnchor = builder.comment("Info alignment. 0 means top. 1 means middle. 2 means bottom.")
					.defineInRange("infoAnchor", 1, 0, 2);
			infoMaxWidth = builder.comment("Info max with. 0.5 means half screen. default: 0.3")
					.defineInRange("infoMaxWidth", 0.3, 0, 0.5);

			showTabs = builder.comment("Show inventory tabs")
					.define("showTabs", true);

			scrollTick = builder.comment("Scroll sensitivity")
					.defineInRange("scrollTick", 1, 0.01, 10000);
			selectionDisplayRequireShift = builder.comment("Render Selection only when pressing shift")
					.define("selectionDisplayRequireShift", false);
			selectionScrollRequireShift = builder.comment("Scroll for selection only when pressing shift")
					.define("selectionScrollRequireShift", true);


		}

	}

	public static class Common {

		public final ForgeConfigSpec.BooleanValue restoreFullHealthOnRespawn;
		public final ForgeConfigSpec.BooleanValue tabSafeMode;

		Common(ForgeConfigSpec.Builder builder) {
			restoreFullHealthOnRespawn = builder.comment("Restore full health on respawn")
					.define("restoreFullHealthOnRespawn", true);
			tabSafeMode = builder.comment("Safe Mode for menu stacking")
					.define("tabSafeMode", false);
		}

	}

	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;

	public static final ForgeConfigSpec COMMON_SPEC;
	public static final Common COMMON;

	static {
		final Pair<Client, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = client.getRight();
		CLIENT = client.getLeft();

		final Pair<Common, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = common.getRight();
		COMMON = common.getLeft();
	}

	/**
	 * Registers any relevant listeners for config
	 */
	public static void init() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, L2LibraryConfig.CLIENT_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, L2LibraryConfig.COMMON_SPEC);
	}


}
