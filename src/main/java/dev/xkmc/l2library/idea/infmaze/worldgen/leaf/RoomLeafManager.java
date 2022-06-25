package dev.xkmc.l2library.idea.infmaze.worldgen.leaf;

import dev.xkmc.l2library.idea.infmaze.dim3d.MazeCell3D;
import dev.xkmc.l2library.idea.infmaze.init.CellContent;
import dev.xkmc.l2library.idea.infmaze.init.LeafManager;
import dev.xkmc.l2library.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class RoomLeafManager implements LeafManager {

	public final Map<LeafType, List<CellContentEntry>> map = new TreeMap<>();

	@Nullable
	@Override
	public CellContent getLeaf(Random random, MazeCell3D cell) {
		List<CellContentEntry> list = map.get(LeafType.of(cell));
		if (list == null) return null;
		CellContentEntry entry = MathHelper.pick(list, CellContentEntry::weight, random.nextDouble());
		if (entry == null) return null;
		return entry.content();
	}

}
