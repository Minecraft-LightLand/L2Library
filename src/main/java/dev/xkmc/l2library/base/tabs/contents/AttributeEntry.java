package dev.xkmc.l2library.base.tabs.contents;

import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public record AttributeEntry(Supplier<Attribute> sup, boolean usePercent, int order) {

	static final List<AttributeEntry> LIST = new ArrayList<>();

	public static void add(Supplier<Attribute> sup, boolean usePercent, int order) {
		sup.get().setSyncable(true);
		LIST.add(new AttributeEntry(sup, usePercent, order));
		LIST.sort(Comparator.comparingInt(AttributeEntry::order));
	}

}
