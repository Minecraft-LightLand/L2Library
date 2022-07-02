package dev.xkmc.l2library.serial.unified;

import dev.xkmc.l2library.serial.generic.GenericCodec;
import dev.xkmc.l2library.serial.handler.Handlers;
import dev.xkmc.l2library.serial.nulldefer.NullDefer;
import dev.xkmc.l2library.serial.wrapper.ClassCache;
import dev.xkmc.l2library.serial.wrapper.FieldCache;
import dev.xkmc.l2library.serial.wrapper.MethodCache;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import dev.xkmc.l2library.util.code.Wrappers;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@SuppressWarnings({"unsafe"})
public class UnifiedCodec {

	public static boolean DEBUG = false;

	public static <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	Object deserializeObject(C ctx, O obj, ClassCache cls, @Nullable Object ans) throws Exception {
		if (DEBUG) {
			try {
				return deserializeObjectImpl(ctx, obj, cls, ans);
			} catch (Exception e) {
				throw new Exception("Error while deserializing " + cls.cls.getName(), e);
			}
		}
		return deserializeObjectImpl(ctx, obj, cls, ans);
	}

	public static <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	O serializeObject(C ctx, O ans, ClassCache cls, Object obj) throws Exception {
		if (DEBUG) {
			try {
				return serializeObjectImpl(ctx, ans, cls, obj);
			} catch (Exception e) {
				throw new Exception("Error while serializing " + cls.cls.getName(), e);
			}
		}
		return serializeObjectImpl(ctx, ans, cls, obj);
	}

	public static <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	Object deserializeValue(C ctx, E e, TypeInfo cls, @Nullable Object ans) throws Exception {
		var real = ctx.fetchRealClass(e, cls);
		if (real.isPresent()) {
			Optional<Optional<Object>> left = real.get().left();
			if (left.isPresent()) {
				Object result = left.get().orElse(null);
				if (result == null) {
					NullDefer<?> nil = NullDefer.get(cls.getAsClass());
					if (nil != null) {
						return nil.getNullDefault();
					}
				}
				return result;
			} else if (real.get().right().isPresent()) {
				cls = real.get().right().get();
			}
		} else {
			if (ctx.hasSpecialHandling(cls.getAsClass())) {
				return ctx.deserializeSpecial(cls.getAsClass(), e);
			}
			for (GenericCodec codec : Handlers.LIST) {
				if (codec.predicate(cls, ans)) {
					return codec.deserializeValue(ctx, e, cls, ans);
				}
			}
		}
		return deserializeObject(ctx, ctx.castAsMap(e), cls.toCache(), ans);
	}

	public static <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	E serializeValue(C ctx, TypeInfo cls, @Nullable Object obj) throws Exception {
		if (obj != null) {
			NullDefer<?> nil = NullDefer.get(cls.getAsClass());
			if (nil != null && nil.predicate(Wrappers.cast(obj))) {
				obj = null;
			}
		}
		var real = ctx.writeRealClass(cls, obj);
		if (real.isPresent()) {
			Optional<E> first = real.get().getFirst();
			Optional<ClassCache> second = real.get().getSecond();
			if (second.isEmpty()) {
				return first.orElse(null);
			}
			O o = first.map(ctx::castAsMap).orElseGet(ctx::createMap);
			return serializeObject(ctx, o, second.get(), obj);
		}
		return serializeObject(ctx, ctx.createMap(), cls.toCache(), obj);
	}

	private static <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	O serializeObjectImpl(C ctx, O ans, ClassCache cls, Object obj) throws Exception {
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
				E content;
				if (DEBUG) {
					try {
						content = serializeValue(ctx, f.toType(), f.get(obj));
					} catch (Exception e) {
						throw new Exception("Error while serializing field " + cls.cls.getName() + "." + f.getName(), e);
					}
				} else {
					content = serializeValue(ctx, f.toType(), f.get(obj));
				}
				ctx.addField(ans, entry.getKey(), content);
			}
			cls = cls.getSuperclass();
		}
		return ans;
	}

	private static <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	Object deserializeObjectImpl(C ctx, O obj, ClassCache cls, @Nullable Object ans) throws Exception {
		if (cls.getSerialAnnotation() == null) {
			throw new Exception("invalid class " + cls + " with object " + obj);
		}
		if (ans == null) {
			ans = cls.create();
		}
		ClassCache mcls = cls;
		while (cls.getSerialAnnotation() != null) {
			TreeMap<String, FieldCache> map = new TreeMap<>();
			for (FieldCache f : cls.getFields()) {
				if (f.getSerialAnnotation() != null) {
					map.put(f.getName(), f);
				}
			}
			for (Map.Entry<String, FieldCache> entry : map.entrySet()) {
				FieldCache f = entry.getValue();
				if (ctx.shouldRead(obj, f)) {
					Object def = f.get(ans);
					Object content;
					if (DEBUG) {
						try {
							content = deserializeValue(ctx, ctx.retrieve(obj, f.getName()), f.toType(), def);
						} catch (Exception e) {
							throw new Exception("Error while deserializing field " + cls.cls.getName() + "." + f.getName(), e);
						}
					} else {
						content = deserializeValue(ctx, ctx.retrieve(obj, f.getName()), f.toType(), def);
					}
					f.set(ans, content);
				} else {
					NullDefer<?> nil = NullDefer.get(f.toType().getAsClass());
					if (nil != null) {
						f.set(ans, nil.getNullDefault());
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

	static <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E>
	Optional<Wrappers.ExcSup<E>> serializeSpecial(C ctx, TypeInfo cls, @Nullable Object obj) throws Exception {
		if (ctx.hasSpecialHandling(cls.getAsClass())) {
			return Optional.of(() -> ctx.serializeSpecial(cls.getAsClass(), obj));
		}
		for (GenericCodec codec : Handlers.LIST) {
			if (codec.predicate(cls, obj)) {
				return Optional.of(() -> codec.serializeValue(ctx, cls, obj));
			}
		}
		return Optional.empty();
	}

}
