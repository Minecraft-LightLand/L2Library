package dev.xkmc.l2library.serial.generic;

import dev.xkmc.l2library.serial.codec.AliasCollection;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import dev.xkmc.l2library.serial.unified.UnifiedContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"unchecked", "unsafe", "rawtypes"})
public class AliasCodec implements GenericCodec {

	@Override
	public boolean predicate(TypeInfo cls, @Nullable Object obj) {
		return obj instanceof AliasCollection<?>;
	}

	@Override
	public <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	Object deserializeValue(C ctx, E e, TypeInfo cls, @Nullable Object ans) throws Exception {
		AliasCollection<?> alias = (AliasCollection<?>) ans;
		A arr = ctx.castAsList(e);
		TypeInfo com = TypeInfo.of(alias.getElemClass());
		int n = ctx.getSize(arr);
		for (int i = 0; i < n; i++) {
			alias.setRaw(n, i, deserializeValue(ctx, ctx.getElement(arr, i), com, null));
		}
		return alias;
	}

	@Override
	public <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	E serializeValue(C ctx, TypeInfo cls, @Nullable Object obj) throws Exception {
		AliasCollection<?> alias = (AliasCollection<?>) obj;
		List<?> list = alias.getAsList();
		A ans = ctx.createList(list.size());
		TypeInfo com = TypeInfo.of(alias.getElemClass());
		for (Object o : list) {
			ctx.addListItem(ans, serializeValue(ctx, com, o));
		}
		return ans;
	}

}
