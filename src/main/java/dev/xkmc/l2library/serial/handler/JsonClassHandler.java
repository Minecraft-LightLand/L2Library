package dev.xkmc.l2library.serial.handler;

import com.google.gson.JsonElement;

public interface JsonClassHandler<T> {

	JsonElement toJson(Object obj);

	T fromJson(JsonElement e);
}
