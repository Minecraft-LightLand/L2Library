package dev.xkmc.l2library.init.reg.simple;

import dev.xkmc.l2library.capability.attachment.AttachmentDef;
import dev.xkmc.l2library.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2library.capability.attachment.GeneralCapabilityTemplate;
import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.capability.player.PlayerCapabilityTemplate;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public record AttReg(DeferredRegister<AttachmentType<?>> att) {

	public static AttReg of(Reg reg) {
		return new AttReg(reg.make(NeoForgeRegistries.ATTACHMENT_TYPES));
	}

	public <E, T extends AttachmentDef<E>> AttVal<E, T> reg(String id, T type) {
		return new AttValImpl<>(att.register(id, type::type), type);
	}

	public <E, T extends AttachmentDef<E>> AttVal<E, T> reg(String id, Function<ResourceLocation, T> factory) {
		ResourceLocation rl = new ResourceLocation(att.getNamespace(), id);
		T type = factory.apply(rl);
		return reg(id, type);
	}

	public <E extends GeneralCapabilityTemplate<H, E>, H extends AttachmentHolder> AttVal.CapVal<H, E>
	entity(String id, Class<E> holder_class, Supplier<E> sup, Class<H> entity_class, Predicate<H> pred) {
		ResourceLocation rl = new ResourceLocation(att.getNamespace(), id);
		var type = new GeneralCapabilityHolder<>(rl, holder_class, sup, entity_class, pred);
		return new CapValImpl<>(att.register(id, type::type), type);
	}

	public <E extends PlayerCapabilityTemplate<E>> AttVal.PlayerVal<E>
	player(String id, Class<E> holder_class, Supplier<E> sup, PlayerCapabilityHolder.NetworkFactory<E> network) {
		ResourceLocation rl = new ResourceLocation(att.getNamespace(), id);
		var type = new PlayerCapabilityHolder<>(rl, holder_class, sup, network);
		return new PlayerValImpl<>(att.register(id, type::type), type);
	}

	private record AttValImpl<E, T extends AttachmentDef<E>>(
			DeferredHolder<AttachmentType<?>, AttachmentType<E>> val, T type
	) implements AttVal<E, T> {

		@Override
		public AttachmentType<E> get() {
			return val.get();
		}

	}

	private record CapValImpl<E extends AttachmentHolder, T extends GeneralCapabilityTemplate<E, T>>(
			DeferredHolder<AttachmentType<?>, AttachmentType<T>> val, GeneralCapabilityHolder<E, T> type
	) implements AttVal.CapVal<E, T> {

		@Override
		public AttachmentType<T> get() {
			return val.get();
		}

	}

	private record PlayerValImpl<T extends PlayerCapabilityTemplate<T>>(
			DeferredHolder<AttachmentType<?>, AttachmentType<T>> val, PlayerCapabilityHolder<T> type
	) implements AttVal.PlayerVal<T> {

		@Override
		public AttachmentType<T> get() {
			return val.get();
		}

	}

}
