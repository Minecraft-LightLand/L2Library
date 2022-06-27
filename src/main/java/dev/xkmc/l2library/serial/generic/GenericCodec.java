package dev.xkmc.l2library.serial.generic;

import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import dev.xkmc.l2library.serial.unified.UnifiedContext;

import javax.annotation.Nullable;

public interface GenericCodec {

	boolean predicate(TypeInfo info, @Nullable Object obj);

	<C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	Object deserializeValue(C ctx, E e, TypeInfo cls, @Nullable Object ans) throws Exception;

	<C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	E serializeValue(C ctx, TypeInfo cls, @Nullable Object obj) throws Exception;
}
