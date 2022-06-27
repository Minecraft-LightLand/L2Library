package dev.xkmc.l2library.serial.unified;

import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import dev.xkmc.l2library.serial.wrapper.ClassCache;
import dev.xkmc.l2library.serial.wrapper.FieldCache;

public interface UnifiedContext<E,O,A> {

	boolean hasSpecialHandling(Class<?> cls);

	Object deserializeSpecial(Class<?> cls, E e);

	E serializeSpecial(Class<?> cls, Object obj);

	ClassCache fetchRealClass(O obj, ClassCache def);

	void writeRealClass(O obj, ClassCache cls);

	boolean shouldRead(O obj, FieldCache field) throws Exception;

	/**
	 * used for object deserialization
	 */
	E retrieve(O obj, String field);

	/**
	 * used for collection-like deserialization
	 */
	A castAsList(E e);

	/**
	 * used for collection-like deserialization
	 */
	int getSize(A a);

	/**
	 * used for collection-like deserialization
	 */
	E getElement(A arr, int i);

	/**
	 * used for map deserialization
	 */
	boolean isListFormat(E e);

	/**
	 * used for map-like and object deserialization
	 */
	O castAsMap(E element);

	/**
	 * used for map-like deserialization, to retrieve key-value pair
	 */
	E getKeyOfEntry(O obj);

	/**
	 * used for map-like deserialization, to retrieve key-value pair
	 */
	E getValueOfEntry(O obj);


	/**
	 * used for map-like deserialization, to retrieve key-value pair
	 */
	void setKeyOfEntry(O obj, E e);

	/**
	 * used for map-like deserialization, to retrieve key-value pair
	 */
	void setValueOfEntry(O obj, E e);

	/**
	 * when isListFormat return false, call this one to deserialize non-list maps
	 */
	Object deserializeEfficientMap(E e, TypeInfo ckey, TypeInfo cval, Object ans) throws Exception;

	/**
	 * for enum
	 */
	String getAsString(E e);

	void addField(O obj, String str, E e);

	A createList(int size);

	O createMap();

	void addListItem(A a, E e);

	/**
	 * for map serialization. Return false to disable efficient map
	 */
	boolean canBeString(E e);

	E fromString(String str);

}
