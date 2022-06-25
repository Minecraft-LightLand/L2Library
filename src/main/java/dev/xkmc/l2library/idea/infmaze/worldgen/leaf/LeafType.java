package dev.xkmc.l2library.idea.infmaze.worldgen.leaf;

import dev.xkmc.l2library.idea.infmaze.dim3d.MazeCell3D;
import dev.xkmc.l2library.idea.infmaze.pos.MazeDirection;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public record LeafType(GateType type, int scale) implements Comparable<LeafType> {

	private static final Comparator<LeafType> COMP = Comparator.comparingInt(LeafType::scale).thenComparingInt(e -> e.type.ordinal());

	public static LeafType of(MazeCell3D cell) {
		GateType gate = cell.getWall(MazeDirection.DOWN).open ? GateType.DOWN :
				cell.getWall(MazeDirection.UP).open ? GateType.UP : GateType.SIDE;
		return new LeafType(gate, cell.pos.scale());
	}

	@Override
	public int compareTo(@NotNull LeafType o) {
		return COMP.compare(this, o);
	}
}
