package dev.xkmc.l2library.serial.codec;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2library.serial.ExceptionHandler;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.handler.Handlers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Capable of serializing primitive type, Arrays, Item, ItemStacl, Ingredient
 * <br>
 * Not capable of handling inheritance, collections
 */
public class JsonCodec {

	private static final Gson GSON = new Gson();

	@SuppressWarnings("unchecked")
	public static <T> T from(JsonElement obj, Class<T> cls, T ans) {
		return ExceptionHandler.get(() -> (T) fromRaw(obj, TypeInfo.of(cls), ans));
	}

	public static <T> JsonElement toJson(T obj) {
		return ExceptionHandler.get(() -> toRaw(TypeInfo.of(obj.getClass()), obj));
	}

	private static Object fromImpl(JsonObject obj, Class<?> cls, Object ans) throws Exception {
		if (obj.has("_class")) {
			cls = Class.forName(obj.get("_class").getAsString());
		}
		if (cls.getAnnotation(SerialClass.class) == null)
			throw new Exception("invalid class " + cls + " with object " + obj);
		if (ans == null)
			ans = cls.getConstructor().newInstance();
		Class<?> mcls = cls;
		while (cls.getAnnotation(SerialClass.class) != null) {
			for (Field f : cls.getDeclaredFields()) {
				if (f.getAnnotation(SerialClass.SerialField.class) != null) {
					if (obj.has(f.getName())) {
						f.setAccessible(true);
						f.set(ans, fromRaw(obj.get(f.getName()), TypeInfo.of(f), null));
					}
				}
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

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static Object fromRaw(JsonElement e, TypeInfo cls, Object ans) throws Exception {
		if (cls.isArray()) {
			JsonArray arr = e.getAsJsonArray();
			TypeInfo com = cls.getComponentType();
			int n = arr.size();
			if (ans == null) ans = Array.newInstance(com.getAsClass(), n);
			for (int i = 0; i < n; i++) {
				Array.set(ans, i, fromRaw(arr.get(i), com, null));
			}
			return ans;
		}
		if (ans instanceof AliasCollection<?> alias) {
			JsonArray arr = e.getAsJsonArray();
			TypeInfo com = TypeInfo.of(alias.getElemClass());
			int n = arr.size();
			for (int i = 0; i < n; i++) {
				alias.setRaw(n, i, fromRaw(arr.get(i), com, null));
			}
			return alias;
		}
		if (List.class.isAssignableFrom(cls.getAsClass())) {
			JsonArray arr = e.getAsJsonArray();
			TypeInfo com = cls.getGenericType(0);
			int n = arr.size();
			if (ans == null) ans = cls.newInstance();
			List list = (List) ans;
			for (int i = 0; i < n; i++) {
				list.add(fromRaw(arr.get(i), com, null));
			}
			return ans;
		}
		if (Map.class.isAssignableFrom(cls.getAsClass())) {
			if (ans == null)
				ans = cls.newInstance();
			TypeInfo ckey = cls.getGenericType(0);
			TypeInfo cval = cls.getGenericType(1);
			if (e.isJsonArray()) {
				for (JsonElement je : e.getAsJsonArray()) {
					JsonObject jeo = je.getAsJsonObject();
					((Map) ans).put(fromRaw(jeo.get("_key"), ckey, null), fromRaw(jeo.get("_val"), cval, null));
				}
				return ans;
			}
			if (e.isJsonObject()) {
				for (Map.Entry<String, JsonElement> ent : e.getAsJsonObject().entrySet()) {
					Object key = ckey.getAsClass() == String.class ? ent.getKey() :
							Handlers.JSON_MAP.get(ckey.getAsClass()).fromJson(new JsonPrimitive(ent.getKey()));
					((Map) ans).put(key, fromRaw(ent.getValue(), cval, null));
				}
				return ans;
			}
		}
		if (cls.getAsClass().isEnum())
			return Enum.valueOf((Class) cls.getAsClass(), e.getAsString());
		if (cls.getAsClass() == JsonElement.class)
			return e;
		if (Handlers.JSON_MAP.containsKey(cls.getAsClass()))
			return Handlers.JSON_MAP.get(cls.getAsClass()).fromJson(e);
		return fromImpl(e.getAsJsonObject(), cls.getAsClass(), ans);
	}

	private static JsonObject toImpl(Class<?> cls, Object obj) throws Exception {
		JsonObject ans = new JsonObject();
		if (obj.getClass() != cls && obj.getClass().isAnnotationPresent(SerialClass.class)) {
			cls = obj.getClass();
			ans.addProperty("_class", cls.getName());
		}
		if (cls.getAnnotation(SerialClass.class) == null)
			throw new Exception("cannot serialize " + cls);
		while (cls.getAnnotation(SerialClass.class) != null) {
			TreeMap<String, Field> map = new TreeMap<>();
			for (Field f : cls.getDeclaredFields()) {
				if (f.getAnnotation(SerialClass.SerialField.class) != null) {
					map.put(f.getName(), f);
				}
			}
			for (Map.Entry<String, Field> entry : map.entrySet()) {
				Field f = entry.getValue();
				f.setAccessible(true);
				ans.add(entry.getKey(), toRaw(TypeInfo.of(f), f.get(obj)));
			}
			cls = cls.getSuperclass();
		}
		return ans;
	}

	private static JsonElement toRaw(TypeInfo cls, Object obj) throws Exception {
		if (obj == null) {
			return JsonNull.INSTANCE;
		}
		if (obj.getClass() != cls.getAsClass() && obj.getClass().isAnnotationPresent(SerialClass.class)) {
			return toImpl(obj.getClass(), obj);
		}
		if (cls.isArray()) {
			int n = Array.getLength(obj);
			JsonArray ans = new JsonArray(n);
			TypeInfo com = cls.getComponentType();
			for (int i = 0; i < n; i++) {
				ans.add(toRaw(com, Array.get(obj, i)));
			}
			return ans;
		}
		if (obj instanceof AliasCollection<?> alias) {
			List<?> list = alias.getAsList();
			JsonArray ans = new JsonArray(list.size());
			TypeInfo com = TypeInfo.of(alias.getElemClass());
			for (Object o : list) {
				ans.add(toRaw(com, o));
			}
			return ans;
		}
		if (List.class.isAssignableFrom(cls.getAsClass())) {
			List<?> list = (List<?>) obj;
			JsonArray ans = new JsonArray(list.size());
			TypeInfo com = cls.getGenericType(0);
			for (Object o : list) {
				ans.add(toRaw(com, o));
			}
			return ans;
		}
		if (Map.class.isAssignableFrom(cls.getAsClass())) {
			Map<?, ?> map = (Map<?, ?>) obj;
			TypeInfo ckey = cls.getGenericType(0);
			TypeInfo cval = cls.getGenericType(1);
			List<Pair<JsonElement, JsonElement>> list = new ArrayList<>();
			boolean can_be_map = true;
			for (Map.Entry<?, ?> ent : map.entrySet()) {
				JsonElement k = toRaw(ckey, ent.getKey());
				JsonElement v = toRaw(cval, ent.getValue());
				list.add(Pair.of(k, v));
				can_be_map &= k instanceof JsonPrimitive p && p.isString();
			}
			if (can_be_map) {
				JsonObject ans = new JsonObject();
				for (Pair<JsonElement, JsonElement> p : list) {
					ans.add(p.getFirst().getAsString(), p.getSecond());
				}
				return ans;
			} else {
				JsonArray ans = new JsonArray(list.size());
				for (Pair<JsonElement, JsonElement> p : list) {
					JsonObject entry = new JsonObject();
					entry.add("_key", p.getFirst());
					entry.add("_val", p.getSecond());
					ans.add(entry);
				}
				return ans;
			}
		}
		if (cls.getAsClass().isEnum()) {
			return new JsonPrimitive(((Enum<?>) obj).name());
		}
		if (Handlers.JSON_MAP.containsKey(cls.getAsClass())) {
			return Handlers.JSON_MAP.get(cls.getAsClass()).toJson(obj);
		}
		return toImpl(cls.getAsClass(), obj);
	}

}