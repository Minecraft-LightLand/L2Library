package dev.xkmc.l2library.serial.generic;

import dev.xkmc.l2library.serial.handler.Handlers;
import dev.xkmc.l2library.serial.unified.UnifiedContext;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;

import javax.annotation.Nullable;

public abstract class GenericCodec {

	protected GenericCodec(){
		Handlers.LIST.add(this);
	}

	public abstract boolean predicate(TypeInfo info, @Nullable Object obj);

	public abstract <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	Object deserializeValue(C ctx, E e, TypeInfo cls, @Nullable Object ans) throws Exception;

	public abstract <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	E serializeValue(C ctx, TypeInfo cls, @Nullable Object obj) throws Exception;
}
