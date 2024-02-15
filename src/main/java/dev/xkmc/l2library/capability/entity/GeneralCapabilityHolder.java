package dev.xkmc.l2library.capability.entity;

import dev.xkmc.l2library.capability.attachment.AttachmentDef;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * only entities will be automatically attached by default for efficiency
 */
public class GeneralCapabilityHolder<E extends AttachmentHolder, T extends GeneralCapabilityTemplate<E, T>> extends AttachmentDef<T> {

	public static final Map<ResourceLocation, GeneralCapabilityHolder<?, ?>> INTERNAL_MAP = new ConcurrentHashMap<>();

	public final ResourceLocation id;
	public final Class<E> entity_class;
	private final Predicate<E> pred;


	public GeneralCapabilityHolder(ResourceLocation id, Class<T> holder_class, Supplier<T> sup,
								   Class<E> entity_class, Predicate<E> pred) {
		super(holder_class, sup);
		this.id = id;
		this.entity_class = entity_class;
		this.pred = pred;
		INTERNAL_MAP.put(id, this);
	}

	public T get(E e) {
		return e.getData(type());
	}

	public boolean isProper(E entity) {
		return pred.test(entity);
	}

	public GeneralCapabilitySerializer<E, T> generateSerializer(E entity) {
		return new GeneralCapabilitySerializer<>(this);
	}

}
