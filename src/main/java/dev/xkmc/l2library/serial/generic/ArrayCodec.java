package dev.xkmc.l2library.serial.generic;

import dev.xkmc.l2library.serial.unified.UnifiedCodec;
import dev.xkmc.l2library.serial.unified.UnifiedContext;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;

@SuppressWarnings({"unchecked", "unsafe", "rawtypes"})
public class ArrayCodec extends GenericCodec {

	@Override
	public boolean predicate(TypeInfo cls, @Nullable Object obj) {
		return cls.isArray();
	}

	@Override
	public <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	Object deserializeValue(C ctx, E e, TypeInfo cls, @Nullable Object ans) throws Exception {
		A arr = ctx.castAsList(e);
		TypeInfo com = cls.getComponentType();
		int n = ctx.getSize(arr);
		if (ans == null)
			ans = Array.newInstance(com.getAsClass(), n);
		for (int i = 0; i < n; i++) {
			Array.set(ans, i, UnifiedCodec.deserializeValue(ctx, ctx.getElement(arr, i), com, null));
		}
		return ans;
	}

	@Override
	public <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	E serializeValue(C ctx, TypeInfo cls, @Nullable Object obj) throws Exception {
		int n = Array.getLength(obj);
		A ans = ctx.createList(n);
		TypeInfo com = cls.getComponentType();
		for (int i = 0; i < n; i++) {
			ctx.addListItem(ans, UnifiedCodec.serializeValue(ctx, com, Array.get(obj, i)));
		}
		return ans;
	}

}
