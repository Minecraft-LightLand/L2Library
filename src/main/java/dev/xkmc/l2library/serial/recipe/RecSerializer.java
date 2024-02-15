package dev.xkmc.l2library.serial.recipe;

import com.mojang.serialization.Codec;
import dev.xkmc.l2serial.serialization.codec.CodecAdaptor;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Objects;

public class RecSerializer<R extends Recipe<I>, I extends Container> implements RecipeSerializer<R> {

	public final Class<R> cls;
	private final CodecAdaptor<R> codec;

	public RecSerializer(Class<R> cls) {
		this.cls = cls;
		this.codec = new CodecAdaptor<>(cls);
	}

	@Override
	public Codec<R> codec() {
		return codec;
	}

	@Override
	public R fromNetwork(FriendlyByteBuf buf) {
		return Objects.requireNonNull(PacketCodec.from(buf, cls, null));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf, R recipe) {
		PacketCodec.to(buf, recipe);
	}

	@SuppressWarnings("ConstantConditions")
	public R blank() {
		return Wrappers.get(() -> cls.getConstructor(ResourceLocation.class).newInstance(new ResourceLocation("dummy")));
	}

}
