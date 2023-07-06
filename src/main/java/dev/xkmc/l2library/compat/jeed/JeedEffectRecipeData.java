package dev.xkmc.l2library.compat.jeed;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;

public record JeedEffectRecipeData(MobEffect effect, ArrayList<Ingredient> providers) {
}
