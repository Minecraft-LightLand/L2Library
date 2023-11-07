package dev.xkmc.l2library.serial.advancements;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2library.base.L2Registrate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.function.Supplier;

public record RewardBuilder(L2Registrate reg, int exp, ResourceLocation loot,
							Supplier<LootTable.Builder> sup) implements IAdvBuilder {

	@Override
	public void modify(String id, Advancement.Builder builder) {
		builder.rewards(AdvancementRewards.Builder.loot(loot).addExperience(exp).build());
	}

	@Override
	public void onBuild() {
		reg.addDataGenerator(ProviderType.LOOT, e -> e.addLootAction(LootContextParamSets.EMPTY,
				x -> x.accept(loot, sup.get())));
	}

}
