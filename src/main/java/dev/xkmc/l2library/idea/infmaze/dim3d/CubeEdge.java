package dev.xkmc.l2library.idea.infmaze.dim3d;

import dev.xkmc.l2library.idea.infmaze.pos.MazeAxis;

public record CubeEdge(int x, int y, int z, int a, int b, int faceIndex, MazeAxis axis) {

	public static final CubeEdge[] EDGES;

	static {
		EDGES = new CubeEdge[12];
		int index = 0;
		for (int i = 0; i < 8; i++) {
			int dx = i & 1;
			int dy = (i >> 1) & 1;
			int dz = (i >> 2) & 1;
			if (dx == 0) {
				EDGES[index++] = new CubeEdge(dx, dy, dz, i, i | 1, dy | dz << 1, MazeAxis.X);
			}
			if (dy == 0) {
				EDGES[index++] = new CubeEdge(dx, dy, dz, i, i | 2, dx | dz << 1, MazeAxis.Y);
			}
			if (dz == 0) {
				EDGES[index++] = new CubeEdge(dx, dy, dz, i, i | 4, dx | dy << 1, MazeAxis.Z);
			}
		}
	}

	public int cubeIndex() {
		return axis.ordinal() << 2 | faceIndex;
	}
}
