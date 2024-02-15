package dev.xkmc.l2library.init.reg.simple;

import dev.xkmc.l2library.capability.attachment.AttachmentDef;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public record AttReg(DeferredRegister<AttachmentType<?>> att) {

	public <E, T extends AttachmentDef<E>> AttVal<E> reg(String id, T type) {
		return new AttValImpl<>(att.register(id, type::type), type);
	}

	private record AttValImpl<E, T extends AttachmentDef<E>>(
			DeferredHolder<AttachmentType<?>, AttachmentType<E>> val, T type
	) implements AttVal<E> {

		@Override
		public AttachmentType<E> get() {
			return val.get();
		}

	}

}
