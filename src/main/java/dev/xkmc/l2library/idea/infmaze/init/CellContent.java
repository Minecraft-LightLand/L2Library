package dev.xkmc.l2library.idea.infmaze.init;

import dev.xkmc.l2library.idea.infmaze.pos.BoundBox;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface CellContent {

	void generate(RandomSource random, BoundBox boxC, ChunkAccess access);

}
