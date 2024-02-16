package dev.xkmc.l2library.capability.attachment;

import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.Objects;
import java.util.function.Supplier;

public class AttachmentDef<E> implements IAttachmentSerializer<CompoundTag, E> {
	private final Class<E> cls;
	private final Supplier<E> sup;
	private AttachmentType<E> type;

	public AttachmentDef(Class<E> cls, Supplier<E> sup) {
		this.cls = cls;
		this.sup = sup;
	}

	public AttachmentType<E> type() {
		if (type != null) return type;
		var builder = AttachmentType.builder(sup);
		builder.serialize(this);
		if (copyOnDeath())
			builder.copyOnDeath();
		type = builder.build();
		return type;
	}

	protected boolean copyOnDeath() {
		return false;
	}

	@Override
	public E read(IAttachmentHolder holder, CompoundTag tag) {
		return Objects.requireNonNull(Wrappers.get(() -> TagCodec.fromTag(tag, cls, null, f -> true)));
	}

	@Override
	public CompoundTag write(E attachment) {
		return Objects.requireNonNull(TagCodec.toTag(new CompoundTag(), attachment));
	}

	public Class<E> cls() {
		return cls;
	}

	public boolean isFor(IAttachmentHolder holder) {
		return true;
	}

}
