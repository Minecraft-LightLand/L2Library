package dev.xkmc.l2library.compat.jeed;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.recipe.RecordRecipeFinished;
import net.mehvahdjukaar.jeed.forge.JeedImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class JeedDataGenerator {

	private final String modid;

	public JeedDataGenerator(String modid) {
		this.modid = modid;
	}

	public void add(Item item, MobEffect... effects) {
		for (MobEffect eff : effects) {
			put(eff, Ingredient.of(item));
		}
	}

	public void add(Ingredient item, MobEffect... effects) {
		for (MobEffect eff : effects) {
			put(eff, item);
		}
	}

	private final LinkedHashMap<MobEffect, List<Ingredient>> map = new LinkedHashMap<>();

	private void put(MobEffect eff, Ingredient item) {
		map.computeIfAbsent(eff, k -> new ArrayList<>()).add(item);
	}

	public void generate(RegistrateRecipeProvider pvd) {
		map.forEach((k, v) -> {
			pvd.accept(new RecordRecipeFinished<>(
					new ResourceLocation(modid, "jeed/" + ForgeRegistries.MOB_EFFECTS.getKey(k).getPath()),
					JeedImpl.getEffectProviderSerializer(),
					new JeedEffectRecipeData(k, new ArrayList<>(v))));

		});
	}

}
