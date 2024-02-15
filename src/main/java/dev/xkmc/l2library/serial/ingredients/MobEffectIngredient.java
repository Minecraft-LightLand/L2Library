package dev.xkmc.l2library.serial.ingredients;

import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.List;

@SerialClass
public class MobEffectIngredient extends BaseIngredient<MobEffectIngredient> {

	@SerialClass.SerialField
	public Item item;
	@SerialClass.SerialField
	public MobEffect effect;
	@SerialClass.SerialField
	public int min_level, min_time;

	@Deprecated
	public MobEffectIngredient() {

	}

	public MobEffectIngredient validate() {
		return new MobEffectIngredient(item, effect, min_level, min_time);
	}

	public MobEffectIngredient(Item item, MobEffect effect, int minLevel, int minTime) {
		super(PotionUtils.setCustomEffects(new ItemStack(item),
				List.of(new MobEffectInstance(effect, minTime, minLevel))));
		this.item = item;
		this.effect = effect;
		this.min_level = minLevel;
		this.min_time = minTime;
	}

	public boolean test(ItemStack stack) {
		return PotionUtils.getMobEffects(stack).stream().anyMatch(e ->
				e.getEffect() == effect &&
						e.getAmplifier() >= min_level &&
						e.getDuration() >= min_time);
	}

}
