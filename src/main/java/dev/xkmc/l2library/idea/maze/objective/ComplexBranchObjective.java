package dev.xkmc.l2library.idea.maze.objective;

public class ComplexBranchObjective extends MazeCellData<ComplexBranchObjective, MazeGeneralData> {

	/**
	 * branch count
	 */
	public int count;
	public boolean isLeaf;

	@Override
	public void fillData(MazeGeneralData global, ComplexBranchObjective[] children) {
		if (children.length == 0) {
			isLeaf = true;
			count = 0;
		} else if (children.length == 1) {
			isLeaf = children[0].isLeaf;
			count = children[0].count;
		} else {
			count = 0;
			int non_leaf = 0;
			for (ComplexBranchObjective obj : children) {
				count += obj.count;
				if (!obj.isLeaf)
					non_leaf++;
			}
			if (count == 0)
				count = 1;
			else
				count += non_leaf - 1;
		}
	}

	@Override
	public double getResult() {
		return count;
	}

}
