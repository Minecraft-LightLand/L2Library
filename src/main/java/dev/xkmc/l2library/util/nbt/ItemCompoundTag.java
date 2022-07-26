package dev.xkmc.l2library.util.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemCompoundTag {

	public static ItemCompoundTag of(ItemStack stack) {
		return new ItemCompoundTag(stack, stack::getOrCreateTag, stack::setTag, stack.getTag());
	}

	private final ItemStack stack;
	private final Supplier<CompoundTag> creator;
	private final Consumer<CompoundTag> replacer;

	@Nullable
	private CompoundTag tag;

	ItemCompoundTag(ItemStack root, Supplier<CompoundTag> creator, Consumer<CompoundTag> replacer, @Nullable CompoundTag tag) {
		this.stack = root;
		this.creator = creator;
		this.replacer = replacer;
		this.tag = tag;
	}

	public ItemStack getHolderStack() {
		return stack;
	}

	public boolean isPresent() {
		return tag != null;
	}

	public CompoundTag getOrCreate() {
		if (tag == null) {
			tag = creator.get();
		}
		return tag;
	}

	public void setTag(CompoundTag tag) {
		this.tag = tag;
		this.replacer.accept(tag);
	}

	public ItemCompoundTag getSubTag(String key) {
		CompoundTag sub = null;
		if (tag != null && tag.contains(key, Tag.TAG_COMPOUND)) {
			sub = tag.getCompound(key);
		}
		return new ItemCompoundTag(stack, () -> {
			CompoundTag self = getOrCreate();
			CompoundTag next = self.getCompound(key);
			if (!self.contains(key)) {
				self.put(key, next);
			}
			return next;
		}, newTag -> {
			CompoundTag self = getOrCreate();
			self.put(key, newTag);
		}, sub);
	}

	public ItemListTag getSubList(String key, int type) {
		ListTag sub = null;
		if (tag != null && tag.contains(key, Tag.TAG_LIST)) {
			sub = tag.getList(key, type);
		}
		return new ItemListTag(stack, () -> {
			CompoundTag self = getOrCreate();
			ListTag next = self.getList(key, type);
			if (!self.contains(key)) {
				self.put(key, next);
			}
			return next;
		}, newTag -> {
			CompoundTag self = getOrCreate();
			self.put(key, newTag);
		}, sub);
	}

}
