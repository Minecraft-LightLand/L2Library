package dev.xkmc.l2library.idea.infmaze.dim3d;

import dev.xkmc.l2library.idea.infmaze.init.CellContent;
import dev.xkmc.l2library.idea.infmaze.init.GenerationConfig;
import dev.xkmc.l2library.idea.infmaze.pos.BasePos;
import dev.xkmc.l2library.idea.infmaze.pos.MazeAxis;

import javax.annotation.Nullable;
import java.util.Random;

public class GenerationHelper {

	private static final long SEED = -0x6895d6420d877b11L;

	private static final long[] SEEDS;

	static {
		int n = 15;
		SEEDS = new long[n];
		Random r = new Random(SEED);
		for (int i = 0; i < n; i++) {
			SEEDS[i] = shift(r.nextLong(), (i * 3 + 7) % 64);
		}
	}

	private static long shift(long seed, int n) {
		return seed >>> n | seed << (64 - n);
	}

	private final GenerationConfig config;

	public int cellCount, wallCount;

	public GenerationHelper(GenerationConfig config) {
		this.config = config;
	}

	public long getRootCellSeed(long seed, BasePos raw) {
		long sx = (shift(seed, 6) + raw.x()) * (shift(seed + raw.x(), 12) + SEEDS[0]) + SEEDS[1];
		long sy = (shift(seed, 8) + raw.y()) * (shift(seed + raw.y(), 14) + SEEDS[2]) + SEEDS[3];
		long sz = (shift(seed, 10) + raw.z()) * (shift(seed + raw.z(), 16) + SEEDS[4]) + SEEDS[5];
		return seed ^ sx ^ sy ^ sz;
	}

	public long getRootWallSeed(long seed, BasePos raw, MazeAxis axis) {
		long sx = (shift(seed, 7) + raw.x()) * (shift(seed + raw.x(), 15) + SEEDS[6]) + SEEDS[7];
		long sy = (shift(seed, 9) + raw.y()) * (shift(seed + raw.y(), 17) + SEEDS[8]) + SEEDS[9];
		long sz = (shift(seed, 11) + raw.z()) * (shift(seed + raw.z(), 19) + SEEDS[10]) + SEEDS[11];
		long sf = (shift(seed, 13) + axis.ordinal()) * (shift(seed + axis.ordinal(), 21) + SEEDS[12]) + SEEDS[13];
		return seed ^ sx ^ sy ^ sz ^ sf;
	}

	public int randomizeWallState(long seed) {
		Random r = new Random(shift(seed, 23));
		int pass = r.nextInt(4);
		int ans = 1 << pass;
		for (int i = 0; i < 4; i++) {
			if (r.nextDouble() < config.wallExtra()) {
				ans |= 1 << i;
			}
		}
		return ans;
	}

	public int randomizeCellInternalState(long seed) {
		Random r = new Random(shift(seed, 25));
		int tree = CubicTree.CUBES[r.nextInt(CubicTree.CUBES.length)];
		int ans = 0;
		for (int i = 0; i < 12; i++) {
			if (((tree >> i) & 1) != 0) {
				CubeEdge edge = CubeEdge.EDGES[i];
				ans |= 1 << edge.cubeIndex();
			}
		}
		for (int i = 0; i < 12; i++) {
			if (r.nextDouble() < config.cellExtra()) {
				ans |= 1 << i;
			}
		}
		return ans;
	}

	public void getChildrenSeeds(long seed, long[] toFill) {
		Random r = new Random(seed);
		for (int i = 0; i < toFill.length; i++) {
			toFill[i] = shift(r.nextLong(), (i * 3 + 7) % 64);
		}
	}

	@Nullable
	public CellContent getLeaf(MazeCell3D cell, long seed) {
		return config.getLeaf(new Random(shift(seed, 27) ^ SEEDS[14]), cell);
	}
}
