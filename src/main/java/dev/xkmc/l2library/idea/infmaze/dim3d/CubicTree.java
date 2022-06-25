package dev.xkmc.l2library.idea.infmaze.dim3d;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public class CubicTree {

	public static final int[] CUBES;

	static {
		IntArrayList list = new IntArrayList();
		for (int i = 0; i < 4096; i++) {
			CubicTree cube = new CubicTree(i);
			if (cube.validate()) {
				list.add(i);
			}
		}
		CUBES = list.toIntArray();
	}

	private final int[] dots = new int[8];
	private final int conn;

	public CubicTree(int conn) {
		for (int i = 0; i < 8; i++) {
			dots[i] = i;
		}
		this.conn = conn;
	}

	private boolean validate() {
		int count = 0;
		for (int i = 0; i < 12; i++) {
			if (((conn >> i) & 1) != 0) {
				count++;
				CubeEdge edge = CubeEdge.EDGES[i];
				if (!add(edge.a(), edge.b()))
					return false;
			}
		}
		if (count != 7) {
			return false;
		}
		int root = root(0);
		for (int i = 1; i < 8; i++) {
			if (root(i) != root)
				return false;
		}
		return true;
	}

	private boolean add(int a, int b) {
		if (root(a) == root(b))
			return false;
		dots[root(a)] = root(b);
		return true;
	}

	private int root(int i) {
		if (dots[i] == i) {
			return i;
		}
		return root(dots[i]);
	}

}
