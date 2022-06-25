package dev.xkmc.l2library.idea.infmaze.worldgen.leaf;

import dev.xkmc.l2library.idea.infmaze.init.CellContent;

import javax.annotation.Nullable;

public record CellContentEntry(@Nullable CellContent content, int weight) {
}
