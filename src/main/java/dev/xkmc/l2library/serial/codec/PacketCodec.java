package dev.xkmc.l2library.serial.codec;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.handler.Handlers;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import dev.xkmc.l2library.util.code.Wrappers;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PacketCodec {

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T from(FriendlyByteBuf buf, Class<T> cls, T ans) {
		return Wrappers.get(() -> (T) fromRaw(buf, TypeInfo.of(cls), ans));
	}

	public static <T> void to(FriendlyByteBuf buf, T obj) {
		Wrappers.run(() -> toRaw(buf, TypeInfo.of(obj.getClass()), obj));
	}

	public static <T extends R, R> void to(FriendlyByteBuf buf, T obj, Class<R> r) {
		Wrappers.run(() -> toRaw(buf, TypeInfo.of(r), obj));
	}

	private static Object fromImpl(FriendlyByteBuf buf, Class<?> cls, @Nullable Object ans) throws Exception {
		if (cls.getAnnotation(SerialClass.class) == null)
			throw new Exception("cannot deserialize " + cls);
		if (ans == null)
			ans = cls.getConstructor().newInstance();
		Class<?> mcls = cls;
		while (cls.getAnnotation(SerialClass.class) != null) {
			TreeMap<String, Field> map = new TreeMap<>();
			for (Field f : cls.getDeclaredFields()) {
				if (f.getAnnotation(SerialClass.SerialField.class) != null) {
					map.put(f.getName(), f);
				}
			}
			for (Field f : map.values()) {
				f.setAccessible(true);
				f.set(ans, fromRaw(buf, TypeInfo.of(f), null));
			}
			cls = cls.getSuperclass();
		}
		cls = mcls;
		while (cls.getAnnotation(SerialClass.class) != null) {
			Method m0 = null;
			for (Method m : cls.getDeclaredMethods()) {
				if (m.getAnnotation(SerialClass.OnInject.class) != null) {
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

	@Nullable
	@SuppressWarnings({"unchecked", "rawtypes"})
	private static Object fromRaw(FriendlyByteBuf buf, TypeInfo cls, @Nullable Object ans) throws Exception {
		byte type = buf.readByte();
		if (type == 0)
			return null;
		else if (type == 2) {
			cls = TypeInfo.of(Class.forName(buf.readUtf()));
		}
		if (cls.isArray()) {
			int n = buf.readInt();
			TypeInfo com = cls.getComponentType();
			if (ans == null)
				ans = Array.newInstance(com.getAsClass(), n);
			for (int i = 0; i < n; i++) {
				Array.set(ans, i, fromRaw(buf, com, null));
			}
			return ans;
		}
		if (ans instanceof AliasCollection<?> alias) {
			int n = buf.readInt();
			TypeInfo com = TypeInfo.of(alias.getElemClass());
			for (int i = 0; i < n; i++) {
				alias.setRaw(n, i, fromRaw(buf, com, null));
			}
			return ans;
		}
		if (List.class.isAssignableFrom(cls.getAsClass())) {
			int n = buf.readInt();
			TypeInfo com = cls.getGenericType(0);
			if (ans == null) ans = cls.newInstance();
			List list = (List) ans;
			for (int i = 0; i < n; i++) {
				list.add(fromRaw(buf, com, null));
			}
			return ans;
		}
		if (Map.class.isAssignableFrom(cls.getAsClass())) {
			if (ans == null)
				ans = cls.newInstance();
			int n = buf.readInt();
			TypeInfo ckey = cls.getGenericType(0);
			TypeInfo cval = cls.getGenericType(1);
			for (int i = 0; i < n; i++) {
				Object key = fromRaw(buf, ckey, null);
				Object val = fromRaw(buf, cval, null);
				((Map) ans).put(key, val);
			}
			return ans;
		}
		if (cls.getAsClass().isEnum())
			return Enum.valueOf((Class) cls.getAsClass(), buf.readUtf());
		if (Handlers.PACKET_MAP.containsKey(cls.getAsClass()))
			return Handlers.PACKET_MAP.get(cls.getAsClass()).fromPacket(buf);
		return fromImpl(buf, cls.getAsClass(), ans);
	}

	private static void toImpl(FriendlyByteBuf buf, Class<?> cls, Object obj) throws Exception {
		if (cls.getAnnotation(SerialClass.class) == null)
			throw new Exception("cannot serialize " + cls);
		while (cls.getAnnotation(SerialClass.class) != null) {
			TreeMap<String, Field> map = new TreeMap<>();
			for (Field f : cls.getDeclaredFields()) {
				if (f.getAnnotation(SerialClass.SerialField.class) != null) {
					map.put(f.getName(), f);
				}
			}
			for (Field f : map.values()) {
				f.setAccessible(true);
				toRaw(buf, TypeInfo.of(f), f.get(obj));
			}
			cls = cls.getSuperclass();
		}
	}

	private static void toRaw(FriendlyByteBuf buf, TypeInfo cls, @Nullable Object obj) throws Exception {
		if (obj == null) {
			buf.writeByte(0);
			return;
		} else if (obj.getClass() != cls.getAsClass() && obj.getClass().isAnnotationPresent(SerialClass.class)) {
			buf.writeByte(2);
			cls = TypeInfo.of(obj.getClass());
			buf.writeUtf(cls.getAsClass().getName());
		} else {
			buf.writeByte(1);
		}
		if (cls.isArray()) {
			int n = Array.getLength(obj);
			buf.writeInt(n);
			TypeInfo com = cls.getComponentType();
			for (int i = 0; i < n; i++) {
				toRaw(buf, com, Array.get(obj, i));
			}
		} else if (obj instanceof AliasCollection<?> alias) {
			List<?> list = alias.getAsList();
			buf.writeInt(list.size());
			TypeInfo com = TypeInfo.of(alias.getElemClass());
			for (Object o : list) {
				toRaw(buf, com, o);
			}
		} else if (List.class.isAssignableFrom(cls.getAsClass())) {
			List<?> list = (List<?>) obj;
			buf.writeInt(list.size());
			TypeInfo com = cls.getGenericType(0);
			for (Object o : list) {
				toRaw(buf, com, o);
			}
		} else if (Map.class.isAssignableFrom(cls.getAsClass())) {
			Map<?, ?> map = (Map<?, ?>) obj;
			buf.writeInt(map.size());
			TypeInfo ckey = cls.getGenericType(0);
			TypeInfo cval = cls.getGenericType(1);
			for (Map.Entry<?, ?> ent : map.entrySet()) {
				toRaw(buf, ckey, ent.getKey());
				toRaw(buf, cval, ent.getValue());
			}
		} else if (cls.getAsClass().isEnum())
			buf.writeUtf(((Enum<?>) obj).name());
		else if (Handlers.PACKET_MAP.containsKey(cls.getAsClass()))
			Handlers.PACKET_MAP.get(cls.getAsClass()).toPacket(buf, obj);
		else
			toImpl(buf, cls.getAsClass(), obj);

	}

}
