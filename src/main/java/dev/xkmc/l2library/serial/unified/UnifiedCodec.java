package dev.xkmc.l2library.serial.unified;

import dev.xkmc.l2library.serial.generic.*;
import dev.xkmc.l2library.serial.wrapper.ClassCache;
import dev.xkmc.l2library.serial.wrapper.FieldCache;
import dev.xkmc.l2library.serial.wrapper.MethodCache;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings({"unsafe"})
public class UnifiedCodec {

	private static final List<GenericCodec> LIST = new ArrayList<>();

	static {
		LIST.add(new RecordCodec());
		LIST.add(new EnumCodec());
		LIST.add(new ArrayCodec());
		LIST.add(new AliasCodec());
		LIST.add(new ListCodec());
		LIST.add(new SetCodec());
		LIST.add(new MapCodec());
	}

	public static <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	Object deserializeObject(C ctx, O obj, ClassCache cls, @Nullable Object ans) throws Exception {
		cls = ctx.fetchRealClass(obj, cls);
		if (cls.getSerialAnnotation() == null) {
			throw new Exception("invalid class " + cls + " with object " + obj);
		}
		if (ans == null) {
			ans = cls.create();
		}
		ClassCache mcls = cls;
		while (cls.getSerialAnnotation() != null) {
			for (FieldCache f : cls.getFields()) {
				if (f.getSerialAnnotation() != null) {
					if (ctx.shouldRead(obj, f)) {
						Object def = f.get(ans);
						f.set(ans, deserializeValue(ctx, ctx.retrieve(obj, f.getName()), f.toType(), def));
					}
				}
			}
			cls = cls.getSuperclass();
		}
		cls = mcls;
		while (cls.getSerialAnnotation() != null) {
			MethodCache m0 = null;
			for (MethodCache m : cls.getMethods()) {
				if (m.getInjectAnnotation() != null) {
					m0 = m;
				}
			}
			if (m0 != null) {
				m0.invoke(ans);
				break;
			}
			cls = cls.getSuperclass();
		}
		return ans;
	}

	public static <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	Object deserializeValue(C ctx, E e, TypeInfo cls, @Nullable Object ans) throws Exception {
		//TODO header info
		if (ctx.hasSpecialHandling(cls.getAsClass())) {
			return ctx.deserializeSpecial(cls.getAsClass(), e);
		}
		for (GenericCodec codec : LIST) {
			if (codec.predicate(cls, ans)) {
				return codec.deserializeValue(ctx, e, cls, ans);
			}
		}
		return deserializeObject(ctx, ctx.castAsMap(e), cls.toCache(), ans);
	}

	public static <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	O serializeObject(C ctx, O ans, ClassCache cls, Object obj) throws Exception {
		ClassCache ocls = ClassCache.get(obj.getClass());
		if (ocls != cls && ocls.getSerialAnnotation() != null) {
			cls = ocls;
			ctx.writeRealClass(ans, ocls);
		}
		if (cls.getSerialAnnotation() == null)
			throw new Exception("cannot serialize " + cls);
		while (cls.getSerialAnnotation() != null) {
			TreeMap<String, FieldCache> map = new TreeMap<>();
			for (FieldCache f : cls.getFields()) {
				if (f.getSerialAnnotation() != null) {
					map.put(f.getName(), f);
				}
			}
			for (Map.Entry<String, FieldCache> entry : map.entrySet()) {
				FieldCache f = entry.getValue();
				ctx.addField(ans, entry.getKey(), serializeValue(ctx, f.toType(), f.get(obj)));
			}
			cls = cls.getSuperclass();
		}
		return ans;
	}

	public static <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	E serializeValue(C ctx, TypeInfo cls, @Nullable Object obj) throws Exception {
		// TODO header
		if (ctx.hasSpecialHandling(cls.getAsClass())) {
			return ctx.serializeSpecial(cls.getAsClass(), obj);
		}
		for (GenericCodec codec : LIST) {
			if (codec.predicate(cls, obj)) {
				return codec.serializeValue(ctx, cls, obj);
			}
		}
		return serializeObject(ctx, ctx.createMap(), cls.toCache(), obj);
	}

}
