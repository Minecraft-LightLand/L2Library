package dev.xkmc.l2library.idea.maze.generator;

import java.util.Random;

public class MazeConfig {

	private static final int[] PATH = {3, 6, 51, 0, 0, 0, 0};
	private static final int[] LOOP = {5, 55, 0, 0, 0, 0, 0};
	private static final double PATH_FAC = 0.35;
	private static final double LOOP_FAC = 0.65;
	private static final double CONN_PRI = 0.05;
	private static final double CONN_SEC = 0.75;
	public int[] path, loop;
	public double path_fac, loop_fac, conn_pri, conn_sec;

	public int invariant = 0, survive = 0;
	public int[][] INVARIANCE_RIM = {};

	public MazeConfig() {
		path = PATH;
		loop = LOOP;
		path_fac = PATH_FAC;
		loop_fac = LOOP_FAC;
		conn_pri = CONN_PRI;
		conn_sec = CONN_SEC;
	}

	public MazeConfig(int[] p, int[] l, double pf, double lf, double c0, double c1) {
		path = p;
		loop = l;
		path_fac = pf;
		loop_fac = lf;
		conn_pri = c0;
		conn_sec = c1;
	}

	public boolean testConn(Random r, boolean b) {
		return b ? r.nextDouble() < conn_pri : r.nextDouble() < conn_sec;
	}

	int randLoop(int i, MazeGen.StateRim rim, Random r) {
		if (i < invariant) return 0;
		int len = (int) Math.ceil(rim.aviLoop() * loop_fac);
		return randSel(r, loop, rim.path == 0, len);
	}

	int randPath(int i, MazeGen.StateRim rim, Random r, int c) {
		if (i <= invariant) return i <= 1 ? 1 : i == 2 ? 4 : 1;
		int len = (int) Math.ceil(rim.aviPath() * path_fac);
		return randSel(r, path, i < survive || c == 1 || !rim.state.isRoot(), len);
	}

	private int randSel(Random r, int[] arr, boolean beg, int len) {
		int a = 0, b = 0;
		for (int i = 0; i < arr.length; i++)
			b += arr[i];
		if (beg)
			a += arr[0];
		for (int i = len + 1; i < arr.length; i++)
			b -= arr[i];
		int v = a + r.nextInt(b - a);
		for (int i = 0; i < arr.length; i++) {
			if (v < arr[i])
				return i;
			v -= arr[i];
		}
		return arr.length - 1;
	}

}