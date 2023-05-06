package dev.xkmc.l2library.serial.advancements;

import com.google.gson.JsonObject;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

public class BaseCriterion<T extends BaseCriterionInstance<T, R>, R extends BaseCriterion<T, R>> extends SimpleCriterionTrigger<T> {

	private final ResourceLocation id;
	private final BiFunction<ResourceLocation, EntityPredicate.Composite, T> factory;
	private final Class<T> cls;

	public BaseCriterion(ResourceLocation id, BiFunction<ResourceLocation, EntityPredicate.Composite, T> factory, Class<T> cls) {
		this.id = id;
		this.factory = factory;
		this.cls = cls;
		CriteriaTriggers.register(this);
	}

	@Override
	protected T createInstance(JsonObject json, EntityPredicate.Composite player, DeserializationContext ctx) {
		T ans = factory.apply(id, player);
		JsonCodec.from(json, cls, ans);
		return ans;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

}
