package dev.xkmc.l2library.serial.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public abstract class BaseRecipe<Rec extends SRec, SRec extends BaseRecipe<?, SRec, Inv>, Inv extends Container> implements Recipe<Inv> {

	private final RecType<Rec, SRec, Inv> factory;

	public BaseRecipe(RecType<Rec, SRec, Inv> fac) {
		factory = fac;
	}

	@Override
	public abstract boolean matches(Inv inv, Level world);

	@Override
	public abstract ItemStack assemble(Inv inv, RegistryAccess access);

	@Override
	public abstract boolean canCraftInDimensions(int r, int c);

	public abstract ItemStack getResultItem(RegistryAccess access);

	@Override
	public final RecipeSerializer<?> getSerializer() {
		return factory;
	}

	@Override
	public final RecipeType<?> getType() {
		return factory.type.get();
	}

	public interface RecInv<R extends BaseRecipe<?, R, ?>> extends Container {

	}

	public static class RecType<Rec extends SRec, SRec extends BaseRecipe<?, SRec, Inv>, Inv extends Container> extends RecSerializer<Rec, Inv> {

		public final Supplier<RecipeType<SRec>> type;

		public RecType(Class<Rec> rec, Supplier<RecipeType<SRec>> type) {
			super(rec);
			this.type = type;
		}

	}

}