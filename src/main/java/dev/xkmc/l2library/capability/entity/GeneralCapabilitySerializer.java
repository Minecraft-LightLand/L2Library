package dev.xkmc.l2library.capability.entity;

import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SerialClass
public class GeneralCapabilitySerializer<E extends AttachmentHolder, C extends GeneralCapabilityTemplate<E, C>> {

	public final GeneralCapabilityHolder<E, C> holder;
	public C handler;

	public GeneralCapabilitySerializer(GeneralCapabilityHolder<E, C> holder) {
		this.holder = holder;
		handler = holder.sup().get();
	}

}

