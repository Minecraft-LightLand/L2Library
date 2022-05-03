package dev.xkmc.l2library.maze.objective;

public class FarpointObjective extends MazeCellData<FarpointObjective, MazeGeneralData> {

	/**
	 * maximum distance to the root node
	 */
	public int to_root;
	/**
	 * maximum distance between known nodes
	 */
	public int internal;

	@Override
	public void fillData(MazeGeneralData global, FarpointObjective[] children) {
		int count = children.length;
		if (count == 0) {
			to_root = 1;
			internal = 1;
		} else if (count == 1) {
			to_root = children[0].to_root + 1;
			internal = Math.max(to_root, children[0].internal);
		} else {
			int max_to_root = 0;
			int max_internal = 0;
			int second_to_root = 0;
			for (FarpointObjective obj : children) {
				if (obj.to_root >= max_to_root) {
					second_to_root = max_to_root;
					max_to_root = obj.to_root;
				} else if (obj.to_root > second_to_root) {
					second_to_root = obj.to_root;
				}
				max_internal = Math.max(max_internal, obj.internal);
			}
			to_root = max_to_root + 1;
			internal = Math.max(max_internal, max_to_root + second_to_root + 1);
		}
	}

	@Override
	public double getResult() {
		return internal;
	}

}
