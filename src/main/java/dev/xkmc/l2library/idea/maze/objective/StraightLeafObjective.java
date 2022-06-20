package dev.xkmc.l2library.idea.maze.objective;

public class StraightLeafObjective extends MazeCellData<StraightLeafObjective, MazeGeneralData> {

	/**
	 * branch count
	 */
	public int count;
	public boolean isLeaf;

	@Override
	public void fillData(MazeGeneralData global, StraightLeafObjective[] children) {
		if (children.length == 0) {
			isLeaf = true;
			count = 1;
		} else if (children.length == 1) {
			count = children[0].count;
			if (isLeaf = children[0].isLeaf && this.access_direction == children[0].access_direction)
				count++;
		} else {
			count = 0;
			for (StraightLeafObjective obj : children)
				count += obj.count;
		}
	}

	@Override
	public double getResult() {
		return count;
	}

}
