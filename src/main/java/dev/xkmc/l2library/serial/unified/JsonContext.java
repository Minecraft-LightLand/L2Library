package dev.xkmc.l2library.serial.unified;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import dev.xkmc.l2library.serial.wrapper.ClassCache;
import dev.xkmc.l2library.serial.wrapper.FieldCache;
import dev.xkmc.l2library.serial.handler.Handlers;

import java.util.Map;

public class JsonContext implements TreeContext<JsonElement, JsonObject, JsonArray> {

	@Override
	public ClassCache fetchRealClass(JsonObject obj, ClassCache def) {
		return null;//TODO
	}

	@Override
	public void writeRealClass(JsonObject obj, ClassCache cls) {
		//TODO
	}

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
