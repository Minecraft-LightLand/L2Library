package dev.xkmc.l2library.idea.infmaze.init;

import dev.xkmc.l2library.idea.infmaze.dim3d.MazeCell3D;
import dev.xkmc.l2library.idea.infmaze.pos.BasePos;
import dev.xkmc.l2library.idea.infmaze.pos.CellPos;

public class CellLoaderChain {

	private final MazeCell3D[] list;
	private final int end;
	private final BasePos pos;

	public CellLoaderChain(MazeCell3D root, BasePos pos) {
		end = root.pos.scale();
		this.pos = pos;
		list = new MazeCell3D[end + 1];
		list[end] = root;
	}

	public MazeCell3D load() {
		for (int s = end; s >= 0; s--) {
			if (list[s] == null) {
				int index = locate(pos, s);
				list[s] = list[s + 1].loadChild(index);
			}
			if (list[s].isLeaf()){
				return list[s];
			}
		}
		return list[0];
	}

	private static int locate(BasePos pos, int scale) {
		long x = (pos.x() >> scale) & 1;
		long y = (pos.y() >> scale) & 1;
		long z = (pos.z() >> scale) & 1;
		return (int) (x | y << 1 | z << 2);
	}

}
