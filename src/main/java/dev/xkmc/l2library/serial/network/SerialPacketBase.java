package dev.xkmc.l2library.serial.network;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.codec.PacketCodec;
import dev.xkmc.l2library.serial.codec.TagCodec;
import dev.xkmc.l2library.serial.unified.UnifiedCodec;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

@SerialClass
public abstract class SerialPacketBase extends SimplePacketBase {

	public static <T extends SerialPacketBase> T serial(Class<T> cls, FriendlyByteBuf buf) {
		return Objects.requireNonNull(PacketCodec.from(buf, cls, null));
	}

	@Override
	public final void write(FriendlyByteBuf buffer) {
		PacketCodec.to(buffer, this);
	}

	@Override
	public final void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> handle(context.get()));
		context.get().setPacketHandled(true);
	}

	public abstract void handle(NetworkEvent.Context ctx);

}
