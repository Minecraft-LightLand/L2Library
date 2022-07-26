package dev.xkmc.l2library.util.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemListTag {

	private final ItemStack stack;
	private final Supplier<ListTag> creator;
	private final Consumer<ListTag> replacer;

	@Nullable
	private ListTag tag;

	ItemListTag(ItemStack root, Supplier<ListTag> creator, Consumer<ListTag> setter, @Nullable ListTag tag) {
		this.stack = root;
		this.creator = creator;
		this.replacer = setter;
		this.tag = tag;
	}

	public ListTag getOrCreate() {
		if (tag == null) {
			tag = creator.get();
		}
		return tag;
	}

	public boolean isPresent() {
		return tag != null;
	}

	public void setTag(ListTag tag) {
		this.tag = tag;
		replacer.accept(tag);
	}

	public ItemCompoundTag addCompound() {
		ListTag list = getOrCreate();
		CompoundTag newTag = new CompoundTag();
		int index = list.size();
		list.add(newTag);
		return new ItemCompoundTag(stack, () -> list.getCompound(index), nextTag -> list.setTag(index, nextTag), newTag);
	}

	public void clear() {
		getOrCreate().clear();
	}
}
