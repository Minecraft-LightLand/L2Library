package dev.xkmc.l2library.serial.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BaseRecipeBuilder<T extends BaseRecipeBuilder<T, Rec, SRec, Inv>, Rec extends SRec, SRec extends BaseRecipe<?, SRec, Inv>, Inv extends Container> {

	protected final BaseRecipe.RecType<Rec, SRec, Inv> type;
	protected final Rec recipe;
	protected final Advancement.Builder advancement = Advancement.Builder.advancement();

	public BaseRecipeBuilder(BaseRecipe.RecType<Rec, SRec, Inv> type) {
		this.type = type;
		this.recipe = type.blank();
	}

	@SuppressWarnings({"unchecked", "unsafe"})
	public T getThis() {
		return (T) this;
	}

	public T unlockedBy(String name, CriterionTriggerInstance trigger) {
		this.advancement.addCriterion(name, trigger);
		return getThis();
	}

	@SuppressWarnings("ConstantConditions")
	public void save(RecipeOutput pvd, ResourceLocation id) {
		this.ensureValid(id);
		this.advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
		ResourceLocation id = new ResourceLocation(id.getNamespace(), "recipes/" +
				BuiltInRegistries.RECIPE_SERIALIZER.getKey(type).getPath() + "/" + id.getPath());
		pvd.accept(id, recipe, advancement);
	}

	protected void ensureValid(ResourceLocation id) {
		if (this.advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id);
		}
	}

}

