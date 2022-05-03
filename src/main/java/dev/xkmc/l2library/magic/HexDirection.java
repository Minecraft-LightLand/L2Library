package dev.xkmc.l2library.magic;

public enum HexDirection {
	/**
	 * the right direction of a cell, with an offset of (1,0)
	 */
	RIGHT(0, 0, 1, 1),
	/**
	 * the lower right direction of a cell, with an offset of (0.5,1.732)
	 */
	LOWER_RIGHT(1, 1, 1, 0),
	/**
	 * the lower left direction of a cell, with an offset of (-0.5,1.732)
	 */
	LOWER_LEFT(2, 1, 0, -1),
	/**
	 * the left direction of a cell, with an offset of (-1,0)
	 */
	LEFT(3, 0, -1, -1),
	/**
	 * the upper left direction of a cell, with an offset of (-0.5,-1.732)
	 */
	UPPER_LEFT(4, -1, -1, 0),
	/**
	 * the upper right direction of a cell, with an offset of (0.5,-1.732)
	 */
	UPPER_RIGHT(5, -1, 0, 1);

	public final int ind;
	private final int dr, dc0, dc1;

	HexDirection(int ind, int dr, int dc0, int dc1) {
		this.ind = ind;
		this.dr = dr;
		this.dc0 = dc0;
		this.dc1 = dc1;
	}

	/**
	 * get the cell offset of this direction
	 *
	 * @param radius the origin of the row axis, can be relative
	 * @param row    the row coordinate
	 * @param cell   the cell coordinate
	 */
	public int getCellOffset(int radius, int row, int cell) {
		// if the path is above the central line, use dc0
		// if the path is below the central line, use dc1
		// for the central row, upper paths are above the central line
		// lower paths are below the central line
		return radius > row ? dc0 : radius < row ? dc1 : dr < 0 ? dc0 : dc1;
	}

	/**
	 * get the row offset of this direction
	 */
	public int getRowOffset() {
		return dr;
	}

	public byte mask() {
		return (byte) (1 << ind);
	}

	/**
	 * the next direction, positive to be clockwise
	 */
	public HexDirection next(int next) {
		if (next < 0)
			next += (-next / 6 + 1) * 6;
		return values()[(ind + next) % 6];
	}
}
