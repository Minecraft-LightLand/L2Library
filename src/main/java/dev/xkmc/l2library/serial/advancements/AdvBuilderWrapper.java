package dev.xkmc.l2library.serial.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;

import java.util.List;

public class AdvBuilderWrapper extends Advancement {

	private final Advancement adv;
	private final List<IAdvBuilder> list;

	public AdvBuilderWrapper(Advancement adv, List<IAdvBuilder> list) {
		super(adv.getId(), adv.getParent(), adv.getDisplay(), adv.getRewards(), adv.getCriteria(),
				adv.getRequirements(), adv.sendsTelemetryEvent());
		this.adv = adv;
		this.list = list;
	}

	@Override
	public Builder deconstruct() {
		return new FakeBuilder(adv.deconstruct());
	}

	private class FakeBuilder extends Builder {

		private final Advancement.Builder parent;

		public FakeBuilder(Advancement.Builder parent) {
			super(false);
			this.parent = parent;
		}

		@Override
		public JsonObject serializeToJson() {
			JsonObject ans = parent.serializeToJson();
			for (var e : list)
				e.modifyJson(ans);
			return ans;
		}
	}

}
