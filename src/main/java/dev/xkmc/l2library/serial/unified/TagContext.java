package dev.xkmc.l2library.serial.unified;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.handler.Handlers;
import dev.xkmc.l2library.serial.wrapper.ClassCache;
import dev.xkmc.l2library.serial.wrapper.FieldCache;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class TagContext extends TreeContext<Tag, CompoundTag, ListTag> {

	private final Predicate<SerialClass.SerialField> pred;

	public TagContext(Predicate<SerialClass.SerialField> pred) {
		super(Optional.of(Pair.of(Optional.empty(), Optional.empty())));
		this.pred = pred;
	}

	@Override
	public Optional<Either<Optional<Object>, TypeInfo>> fetchRealClass(Tag e, TypeInfo def) throws Exception {
		if (e == null) {
			return Optional.of(Either.left(def.getAsClass() == ItemStack.class ? Optional.of(ItemStack.EMPTY) : Optional.empty()));
		}
		if (e instanceof CompoundTag obj) {
			if (obj.contains("_class")) {
				return Optional.of(Either.right(TypeInfo.of(Class.forName(obj.get("_class").getAsString()))));
			}
		}
		return Optional.empty();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Object deserializeEfficientMap(Tag tag, TypeInfo key, TypeInfo val, Object def) throws Exception {
		CompoundTag ctag = (CompoundTag) tag;
		Map map = (Map) def;
		map.clear();
		for (String str : ctag.getAllKeys()) {
			Object mkey = key.getAsClass() == String.class ? str :
					key.getAsClass().isEnum() ? Enum.valueOf((Class) key.getAsClass(), str) :
							Handlers.NBT_MAP.get(key.getAsClass()).fromTag(StringTag.valueOf(str));
			map.put(mkey, UnifiedCodec.deserializeValue(this, ctag.get(str), val, null));
		}
		return map;
	}

	@Override
	public boolean hasSpecialHandling(Class<?> cls) {
		return Handlers.NBT_MAP.containsKey(cls);
	}

	@Override
	public Object deserializeSpecial(Class<?> cls, Tag e) {
		return Handlers.NBT_MAP.get(cls).fromTag(e);
	}

	@Override
	public Tag serializeSpecial(Class<?> cls, Object obj) {
		return Handlers.NBT_MAP.get(cls).toTag(obj);
	}

	@Override
	public boolean shouldRead(CompoundTag obj, FieldCache field) throws Exception {
		return pred.test(field.getSerialAnnotation()) && obj.contains(field.getName());
	}

	@Override
	public Tag retrieve(CompoundTag obj, String field) {
		return obj.get(field);
	}

	@Override
	public ListTag castAsList(Tag e) {
		return (ListTag) e;
	}

	@Override
	public int getSize(ListTag arr) {
		return arr.size();
	}

	@Override
	public Tag getElement(ListTag arr, int i) {
		return arr.get(i);
	}

	@Override
	public boolean isListFormat(Tag e) {
		return e instanceof ListTag;
	}

	@Override
	public CompoundTag castAsMap(Tag e) {
		return (CompoundTag) e;
	}

	@Override
	public String getAsString(Tag e) {
		return e.getAsString();
	}

	@Override
	public void addField(CompoundTag obj, String str, Tag e) {
		if (e != null) {
			obj.put(str, e);
		}
	}

	@Override
	public ListTag createList(int size) {
		return new ListTag();
	}

	@Override
	public CompoundTag createMap() {
		return new CompoundTag();
	}

	@Override
	public void addListItem(ListTag arr, Tag e) {
		arr.add(e);
	}

	@Override
	public boolean canBeString(Tag e) {
		return e instanceof StringTag;
	}

	@Override
	public Tag fromString(String str) {
		return StringTag.valueOf(str);
	}

}
