package dev.xkmc.l2library.serial.unified;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2library.serial.wrapper.ClassCache;
import dev.xkmc.l2library.serial.wrapper.FieldCache;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;

import java.util.Optional;

public interface UnifiedContext<E, O, A> {

	boolean hasSpecialHandling(Class<?> cls);

	Object deserializeSpecial(Class<?> cls, E e);

	E serializeSpecial(Class<?> cls, Object obj);

	/**
	 * Optional.empty() : normal
	 * Optional.of(Either.left(...)) : fast return
	 * Optional.of(Either.right(...)) : class override
	 */
	Optional<Either<Optional<Object>, TypeInfo>> fetchRealClass(E obj, TypeInfo def) throws Exception;

	/**
	 * Optional.empty() : normal
	 * Optional.of(Pair.of(..., Optional.empty())) : fast return
	 * Optional.of(Pair.of(..., Optional.of(...))) : class override
	 */
	Optional<Pair<Optional<E>, Optional<ClassCache>>> writeRealClass(TypeInfo cls, Object obj) throws Exception;

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
