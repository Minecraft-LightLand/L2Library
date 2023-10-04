package dev.xkmc.l2library.compat.curios;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record CurioEntityBuilder(
		ArrayList<ResourceLocation> entities,
		ArrayList<String> slots,
		ArrayList<SlotCondition> conditions) {
}
