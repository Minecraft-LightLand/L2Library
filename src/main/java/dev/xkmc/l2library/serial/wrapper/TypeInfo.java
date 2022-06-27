package dev.xkmc.l2library.serial.wrapper;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeInfo {

	public static TypeInfo of(Class<?> cls) {
		return new TypeInfo(cls, null);
	}

	public static TypeInfo of(Field field) {
		return new TypeInfo(field.getType(), field.getGenericType());
	}

	private static TypeInfo of(Type type) {
		if (type instanceof Class cls) {
			return new TypeInfo(cls, null);
		}
		if (type instanceof ParameterizedType ptype && ptype.getRawType() instanceof Class cls) {
			return new TypeInfo(cls, ptype);
		}
		if (type instanceof GenericArrayType array) {
			TypeInfo sub = of(array.getGenericComponentType());
			return new TypeInfo(sub.cls.arrayType(), array);
		}
		throw new IllegalStateException("type parameter cannot be converted to class. Generic Type: " + type + ", class: " + type.getClass());
	}

	private final Class<?> cls;
	private final Type type;

	private TypeInfo(Class<?> cls, Type type) {
		this.cls = cls;
		this.type = type;
	}

	public Class<?> getAsClass() {
		return cls;
	}

	public TypeInfo getComponentType() {
		Type com = null;
		if (type instanceof GenericArrayType array)
			com = array.getGenericComponentType();
		return new TypeInfo(cls.getComponentType(), com);
	}

	public TypeInfo getGenericType(int i) {
		if (type instanceof ParameterizedType param) {
			Type[] types = param.getActualTypeArguments();
			if (types.length <= i)
				throw new IllegalArgumentException("generic type " + type + "has " + types.length + " fields, accessing index " + i);
			return TypeInfo.of(types[i]);
		}
		throw new IllegalStateException("type parameter is missing. Type: " + type + ", Class: " + cls);
	}

	public Object newInstance() throws Exception {
		return cls.getConstructor().newInstance();
	}

	public boolean isArray() {
		return cls.isArray();
	}

	@Override
	public String toString() {
		return "{cls = " + cls + ", type = " + type + "}";
	}

	public ClassCache toCache() {
		return ClassCache.get(cls);
	}
}
