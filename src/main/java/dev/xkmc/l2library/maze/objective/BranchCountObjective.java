package dev.xkmc.l2library.maze.objective;

public class BranchCountObjective extends MazeCellData<BranchCountObjective, MazeGeneralData> {

	/**
	 * branch count
	 */
	public int count;

	@Override
	public void fillData(MazeGeneralData global, BranchCountObjective[] children) {
		if (children.length == 0)
			count = 1;
		else if (children.length == 1)
			count = children[0].count;
		else {
			count = 0;
			for (BranchCountObjective obj : children) {
				count += obj.count;
			}
		}
	}

	@Override
	public double getResult() {
		return count;
	}

}
