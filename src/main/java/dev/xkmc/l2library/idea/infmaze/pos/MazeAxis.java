package dev.xkmc.l2library.idea.infmaze.pos;

public enum MazeAxis {
	X(1, 0, 0), Y(0, 1, 0), Z(0, 0, 1);

	public final int x, y, z;

	MazeAxis(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}



}
