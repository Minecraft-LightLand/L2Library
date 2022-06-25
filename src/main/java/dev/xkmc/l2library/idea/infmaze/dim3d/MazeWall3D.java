package dev.xkmc.l2library.idea.infmaze.dim3d;

import dev.xkmc.l2library.idea.infmaze.pos.WallPos;

public class MazeWall3D {

	public final WallPos pos;
	public final long seed;
	public final boolean open;

	private final GenerationHelper helper;

	private Internal internal;

	public MazeWall3D(GenerationHelper helper, WallPos pos, boolean open, long seed) {
		this.helper = helper;
		this.pos = pos;
		this.seed = seed;
		this.open = open;
		helper.wallCount++;
	}

	public MazeWall3D loadChild(int index) {
		if (internal == null) {
			internal = new Internal(this);
		}
		return internal.children[index];
	}

	private static class Internal {

		private final MazeWall3D[] children;

		private Internal(MazeWall3D holder) {
			long[] seeds = new long[5];
			holder.helper.getChildrenSeeds(holder.seed, seeds);
			int wallState = holder.open ? holder.helper.randomizeWallState(seeds[4]) : 0;
			children = new MazeWall3D[4];
			for (int i = 0; i < 4; i++) {
				int du = (i & 1) << holder.pos.scale() - 1;
				int dv = ((i >> 1) & 1) << holder.pos.scale() - 1;
				boolean state = (wallState & (1 << i)) != 0;
				children[i] = new MazeWall3D(holder.helper, holder.pos.offset(du, dv, -1), state, seeds[i]);
			}
		}
	}

}
