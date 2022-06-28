package dev.xkmc.l2library.serial.generic;

import dev.xkmc.l2library.serial.unified.UnifiedCodec;
import dev.xkmc.l2library.serial.unified.UnifiedContext;
import dev.xkmc.l2library.serial.wrapper.RecordCache;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class RecordCodec extends GenericCodec {

	@Override
	public boolean predicate(TypeInfo cls, @Nullable Object obj) {
		return cls.getAsClass().isRecord();
	}

	@Override
	public <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	Object deserializeValue(C ctx, E e, TypeInfo cls, @Nullable Object ans) throws Exception {
		O o = ctx.castAsMap(e);
		RecordCache cache = RecordCache.get(cls.getAsClass());
		Field[] fields = cache.getFields();
		Object[] objs = new Object[fields.length];
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			objs[i] = UnifiedCodec.deserializeValue(ctx, ctx.retrieve(o, f.getName()), TypeInfo.of(f), null);
		}
		return cache.create(objs);
	}

	@Override
	public <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	E serializeValue(C ctx, TypeInfo cls, Object obj) throws Exception {
		O o = ctx.createMap();
		RecordCache cache = RecordCache.get(cls.getAsClass());
		for (Field f : cache.getFields()) {
			ctx.addField(o, f.getName(), UnifiedCodec.serializeValue(ctx, TypeInfo.of(f), f.get(obj)));
		}
		return o;
	}

}
