package dev.xkmc.l2library.serial.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseRecipeBuilder<
		T extends BaseRecipeBuilder<T, Rec, SRec, Inv>,
		Rec extends SRec,
		SRec extends BaseRecipe<?, SRec, Inv>,
		Inv extends Container
		> implements RecipeBuilder {

	protected final BaseRecipe.RecType<Rec, SRec, Inv> type;
	protected final Rec recipe;
	protected final Item result;
	protected final Advancement.Builder advancement = Advancement.Builder.advancement();
	protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

	public BaseRecipeBuilder(BaseRecipe.RecType<Rec, SRec, Inv> type, Item result) {
		this.type = type;
		this.recipe = type.blank();
		this.result = result;
	}

	@SuppressWarnings({"unchecked", "unsafe"})
	public T getThis() {
		return (T) this;
	}

	@Override
	public T unlockedBy(String name, Criterion<?> trigger) {
		criteria.put(name, trigger);
		return getThis();
	}

	@Override
	public T group(@Nullable String pGroupName) {
		return getThis();
	}

	@Override
	public Item getResult() {
		return result;
	}

	@Override
	public void save(RecipeOutput pvd, ResourceLocation id) {
		Advancement.Builder builder = pvd.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id))
				.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(builder::addCriterion);
		id = new ResourceLocation(id.getNamespace(), "recipes/" +
				BuiltInRegistries.RECIPE_SERIALIZER.getKey(type).getPath() + "/" + id.getPath());
		pvd.accept(id, recipe, builder.build(id));
	}

}

