package dev.xkmc.l2library.idea.infmaze.pos;

import org.apache.http.util.Asserts;

import static dev.xkmc.l2library.idea.infmaze.pos.MazeAxis.*;

public enum MazeDirection {
	EAST(X, 1), WEST(X, -1),
	UP(Y, 1), DOWN(Y, -1),
	SOUTH(Z, 1), NORTH(Z, -1);

	public final MazeAxis axis;
	public final int x, y, z, factor;

	MazeDirection(MazeAxis axis, int factor) {
		this.axis = axis;
		this.factor = factor;
		this.x = axis.x * factor;
		this.y = axis.y * factor;
		this.z = axis.z * factor;
	}

	public static MazeDirection getDirection(MazeAxis axis, int factor) {
		Asserts.check(factor == 1 || factor == -1, "factor can only be 1 or -1");
		return values()[(axis.ordinal() << 1) | ((1 - factor) >> 1)];
	}

}
