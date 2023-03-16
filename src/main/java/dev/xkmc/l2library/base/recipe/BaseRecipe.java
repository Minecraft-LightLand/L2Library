package dev.xkmc.l2library.base.recipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BaseRecipe<
		Rec extends SRec,
		SRec extends BaseRecipe<?, SRec, Inv>,
		Inv extends Container>
		implements Recipe<Inv> {

	private final RecType<Rec, SRec, Inv> factory;
	public ResourceLocation id;

	public BaseRecipe(ResourceLocation id, RecType<Rec, SRec, Inv> fac) {
		this.id = id;
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
	public final ResourceLocation getId() {
		return id;
	}

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

	public static class RecType<Rec extends SRec, SRec extends BaseRecipe<?, SRec, Inv>, Inv extends Container>
			extends RecSerializer<Rec, Inv> {

		public final RegistryObject<RecipeType<SRec>> type;

		public RecType(Class<Rec> rec, RegistryObject<RecipeType<SRec>> type) {
			super(rec);
			this.type = type;
		}

	}

}