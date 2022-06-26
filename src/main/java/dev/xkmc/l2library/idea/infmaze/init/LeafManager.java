package dev.xkmc.l2library.idea.infmaze.init;

import dev.xkmc.l2library.idea.infmaze.dim3d.MazeCell3D;
import net.minecraft.world.level.levelgen.RandomState;

import javax.annotation.Nullable;
import java.util.Random;

public interface LeafManager {

	@Nullable
	CellContent getLeaf(Random random, MazeCell3D cell);

	void decoratePath(RandomState random, MazeCell3D cell);

}
