package dev.xkmc.l2library.init.reg.simple;

import com.mojang.serialization.Codec;
import dev.xkmc.l2library.serial.ingredients.BaseIngredient;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public record IngReg(DeferredRegister<IngredientType<?>> reg) {

	public <R extends Ingredient> IngVal<R> reg(String id, Codec<R> codec) {
		return new IngValImpl<>(reg.register(id, () -> new IngredientType<>(codec)));
	}

	public <R extends BaseIngredient<R>> IngVal<R> reg(String id, Class<R> cls) {
		return new IngValImpl<>(reg.register(id, () -> new IngredientType<>(new CodecAdaptor<>(cls, BaseIngredient::validate))));
	}

	private record IngValImpl<R extends Ingredient>(DeferredHolder<IngredientType<?>, IngredientType<R>> val)
			implements IngVal<R> {

		@Override
		public IngredientType<R> get() {
			return val.get();
		}

	}

}
