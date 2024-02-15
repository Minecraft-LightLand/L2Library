package dev.xkmc.l2library.init.reg.simple;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public record Reg(String modid) {

	public <T> SR<T> simple(Registry<T> reg) {
		return new SR<>(DeferredRegister.create(reg, modid()));
	}

	public <T> CdcReg<T> codec(Registry<Codec<? extends T>> reg) {
		return new CdcReg<>(DeferredRegister.create(reg, modid()));
	}

	public IngReg ingredient() {
		return new IngReg(DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, modid()));
	}

}
