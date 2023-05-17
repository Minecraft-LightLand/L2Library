package dev.xkmc.l2library.init.events.screen.triggers;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.serial.advancements.BaseCriterion;
import dev.xkmc.l2library.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ExitMenuTrigger extends BaseCriterion<ExitMenuTrigger.Ins, ExitMenuTrigger> {

	public static final ExitMenuTrigger EXIT_MENU = new ExitMenuTrigger(new ResourceLocation(L2Library.MODID, "exit_menu"));

	public static Ins exitOne() {
		return new Ins(EXIT_MENU.getId(), EntityPredicate.Composite.ANY);
	}

	public static Ins exitAll() {
		Ins ans = exitOne();
		ans.all = true;
		return ans;
	}

	public ExitMenuTrigger(ResourceLocation id) {
		super(id, Ins::new, Ins.class);
	}

	public void trigger(ServerPlayer player, boolean all) {
		this.trigger(player, e -> e.all == all);
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, ExitMenuTrigger> {

		@SerialClass.SerialField
		private boolean all = false;

		public Ins(ResourceLocation id, EntityPredicate.Composite player) {
			super(id, player);
		}

	}

}
