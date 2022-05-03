package dev.xkmc.l2library.maze.objective;

public class LeafMarker extends MazeCellData<LeafMarker, LeafMarker.LeafSetData> {

	public static class LeafSetData extends MazeGeneralData {

		public int current_color = 0;

	}

	private int color = 0;

	public int level = 0;
	public LeafMarker parent;

	@Override
	public void fillData(LeafSetData global, LeafMarker[] children) {
		for (LeafMarker c : children)
			c.parent = this;
		if (children.length == 0) {
			level = 0;
		} else if (children.length == 1) {
			level = children[0].level;
		} else {
			for (LeafMarker c : children) {
				level = Math.max(level, c.level + 1);
				global.current_color++;
				c.color = global.current_color;
			}
		}
	}

	/**
	 * color == 0 means it's leave
	 */
	public boolean isLeaf(int[] colors) {
		return level == 0 && colors[getColor()] != 0;
	}

	public int getColor() {
		if (parent == null) {
			return color;
		}
		if (color != 0) {
			return color;
		}
		return parent.getColor();
	}

	@Override
	public double getResult() {
		return level;
	}
}
