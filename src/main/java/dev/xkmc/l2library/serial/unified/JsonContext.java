package dev.xkmc.l2library.serial.unified;

import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2library.serial.handler.Handlers;
import dev.xkmc.l2library.serial.wrapper.ClassCache;
import dev.xkmc.l2library.serial.wrapper.FieldCache;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;

import java.util.Map;
import java.util.Optional;

public class JsonContext extends TreeContext<JsonElement, JsonObject, JsonArray> {

	@Override
	public boolean hasSpecialHandling(Class<?> cls) {
		return Handlers.JSON_MAP.containsKey(cls);
	}

	@Override
	public Object deserializeSpecial(Class<?> cls, JsonElement e) {
		return Handlers.JSON_MAP.get(cls).fromJson(e);
	}

	@Override
	public JsonElement serializeSpecial(Class<?> cls, Object obj) {
		return Handlers.JSON_MAP.get(cls).toJson(obj);
	}

	@Override
	public Optional<Either<Optional<Object>, TypeInfo>> fetchRealClass(JsonElement e, TypeInfo def) throws Exception {
		if (e.isJsonNull()) {
			return Optional.of(Either.left(Optional.empty()));
		}
		if (e.isJsonObject()) {
			JsonObject obj = e.getAsJsonObject();
			if (obj.has("_class")) {
				return Optional.of(Either.right(TypeInfo.of(Class.forName(obj.get("_class").getAsString()))));
			}
		}
		return Optional.empty();
	}

	@Override
	public Optional<Pair<Optional<JsonElement>, Optional<ClassCache>>> writeRealClass(TypeInfo cls, Object obj) throws Exception {
		if (obj == null) {
			return Optional.of(Pair.of(Optional.of(JsonNull.INSTANCE), Optional.empty()));
		}
		if (obj.getClass() != cls.getAsClass()) {
			ClassCache cache = ClassCache.get(obj.getClass());
			if (cache.getSerialAnnotation() != null) {
				JsonObject ans = new JsonObject();
				ans.addProperty("_class", obj.getClass().getName());
				return Optional.of(Pair.of(Optional.of(ans), Optional.of(cache)));
			}
		}
		return Optional.empty();
	}

	@Override
	public boolean shouldRead(JsonObject obj, FieldCache field) {
		return obj.has(field.getName());
	}

	@Override
	public JsonElement retrieve(JsonObject obj, String field) {
		return obj.get(field);
	}

	@Override
	public JsonArray castAsList(JsonElement e) {
		return e.getAsJsonArray();
	}

	@Override
	public int getSize(JsonArray arr) {
		return arr.size();
	}

	@Override
	public JsonElement getElement(JsonArray arr, int i) {
		return arr.get(i);
	}

	@Override
	public boolean isListFormat(JsonElement e) {
		return e.isJsonArray();
	}

	@Override
	public JsonObject castAsMap(JsonElement e) {
		return e.getAsJsonObject();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Object deserializeEfficientMap(JsonElement e, TypeInfo ckey, TypeInfo cval, Object ans) throws Exception {
		for (Map.Entry<String, JsonElement> ent : e.getAsJsonObject().entrySet()) {
			Object key = ckey.getAsClass() == String.class ? ent.getKey() :
					ckey.getAsClass().isEnum() ? Enum.valueOf((Class) ckey.getAsClass(), ent.getKey()) :
							Handlers.JSON_MAP.get(ckey.getAsClass()).fromJson(new JsonPrimitive(ent.getKey()));
			((Map) ans).put(key, UnifiedCodec.deserializeValue(this, ent.getValue(), cval, null));
		}
		return ans;
	}

	@Override
	public String getAsString(JsonElement e) {
		return e.getAsString();
	}

	@Override
	public void addField(JsonObject obj, String str, JsonElement e) {
		obj.add(str, e);
	}

	@Override
	public JsonArray createList(int size) {
		return new JsonArray(size);
	}

	@Override
	public JsonObject createMap() {
		return new JsonObject();
	}

	@Override
	public void addListItem(JsonArray arr, JsonElement e) {
		arr.add(e);
	}

	@Override
	public boolean canBeString(JsonElement e) {
		return e instanceof JsonPrimitive p && p.isString();
	}

	@Override
	public JsonElement fromString(String str) {
		return new JsonPrimitive(str);
	}

}
