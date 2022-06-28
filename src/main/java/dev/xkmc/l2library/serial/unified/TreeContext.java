package dev.xkmc.l2library.serial.unified;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2library.serial.wrapper.ClassCache;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import dev.xkmc.l2library.util.code.Wrappers;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class TreeContext<E, O extends E, A extends E> extends UnifiedContext<E, O, A> {

	private final Optional<Pair<Optional<E>, Optional<ClassCache>>> nil;

	protected TreeContext(Optional<Pair<Optional<E>, Optional<ClassCache>>> nil) {
		this.nil = nil;
	}


	@Override
	public E getKeyOfEntry(O obj) {
		return retrieve(obj, "_key");
	}

	@Override
	public E getValueOfEntry(O obj) {
		return retrieve(obj, "_val");
	}

	@Override
	public void setKeyOfEntry(O obj, E e) {
		addField(obj, "_key", e);
	}

	@Override
	public void setValueOfEntry(O obj, E e) {
		addField(obj, "_val", e);
	}


	@Override
	public Optional<Pair<Optional<E>, Optional<ClassCache>>> writeRealClass(TypeInfo cls, Object obj) throws Exception {
		if (obj == null) {
			return nil;
		}
		Optional<Wrappers.ExcSup<E>> special = UnifiedCodec.serializeSpecial(this, cls, obj);
		if (special.isPresent()) {
			return Optional.of(Pair.of(Optional.ofNullable(special.get().get()), Optional.empty()));
		}
		if (obj.getClass() != cls.getAsClass()) {
			ClassCache cache = ClassCache.get(obj.getClass());
			if (cache.getSerialAnnotation() != null) {
				O ans = createMap();
				addField(ans, "_class", fromString(obj.getClass().getName()));
				return Optional.of(Pair.of(Optional.of(ans), Optional.of(cache)));
			}
		}
		return Optional.empty();
	}

}
