package dev.xkmc.l2library.serial.wrapper;

import dev.xkmc.l2library.util.code.LazyExc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unsafe"})
public class RecordCache {

	private static final Map<Class<?>, RecordCache> CACHE = new HashMap<>();

	public static RecordCache get(Class<?> cls) {
		return CACHE.computeIfAbsent(cls, RecordCache::new);
	}

	private final LazyExc<Field[]> fields;
	private final LazyExc<Constructor<?>> factory;

	private RecordCache(Class<?> cls) {
		fields = new LazyExc<>(() -> {
			var ans = cls.getDeclaredFields();
			for (Field f : ans) {
				f.setAccessible(true);
			}
			return ans;
		});
		factory = new LazyExc<>(() -> {
			Class[] clss = Arrays.stream(fields.get()).map(Field::getType).toArray(Class[]::new);
			var ans = cls.getConstructor(clss);
			ans.setAccessible(true);
			return ans;
		});
	}

	public Field[] getFields() throws Exception {
		return fields.get();
	}

	public Object create(Object[] objs) throws Exception {
		return factory.get().newInstance(objs);
	}
}
