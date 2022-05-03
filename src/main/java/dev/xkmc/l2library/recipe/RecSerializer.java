package dev.xkmc.l2library.recipe;

import com.google.gson.JsonObject;
import dev.xkmc.l2library.serial.ExceptionHandler;
import dev.xkmc.l2library.serial.codec.JsonCodec;
import dev.xkmc.l2library.serial.codec.PacketCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RecSerializer<R extends Recipe<I>, I extends Container> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<R> {

	public final Class<R> cls;

	public RecSerializer(Class<R> cls) {
		this.cls = cls;
	}

	@Override
	public R fromJson(ResourceLocation id, JsonObject json) {
		return JsonCodec.from(json, cls,
				ExceptionHandler.get(() -> cls.getConstructor(ResourceLocation.class).newInstance(id)));

	}

	@Override
	public R fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		return PacketCodec.from(buf, cls,
				ExceptionHandler.get(() -> cls.getConstructor(ResourceLocation.class).newInstance(id)));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf, R recipe) {
		PacketCodec.to(buf, recipe);
	}

	public R blank() {
		return ExceptionHandler.get(() -> cls.getConstructor(ResourceLocation.class).newInstance(new ResourceLocation("dummy")));
	}

}
