package dev.xkmc.l2library.serial.generic;

import dev.xkmc.l2library.serial.unified.UnifiedContext;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unchecked", "unsafe", "rawtypes"})
public class EnumCodec extends GenericCodec {

	@Override
	public boolean predicate(TypeInfo cls, @Nullable Object obj) {
		return cls.getAsClass().isEnum();
	}

	@Override
	public <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	Object deserializeValue(C ctx, E e, TypeInfo cls, @Nullable Object ans) throws Exception {
		return Enum.valueOf((Class) cls.getAsClass(), ctx.getAsString(e));
	}

	@Override
	public <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	E serializeValue(C ctx, TypeInfo cls, @Nullable Object obj) throws Exception {
		return ctx.fromString(((Enum<?>) obj).name());
	}

}
