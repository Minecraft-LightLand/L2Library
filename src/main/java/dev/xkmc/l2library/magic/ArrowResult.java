package dev.xkmc.l2library.magic;

public class ArrowResult extends LocateResult {

	public final int row, cell;
	public final HexDirection dir;
	private final HexHandler hex;

	ArrowResult(int row, int cell, HexDirection dir, HexHandler hex) {
		this.row = row;
		this.cell = cell;
		this.dir = dir;
		this.hex = hex;
	}

	public static LocateResult get(int row, int cell, HexDirection dir, HexHandler hex) {
		if (row < 0 || row >= hex.getRowCount())
			return null;
		if (cell < 0 || cell >= hex.getCellCount(row))
			return null;
		int dr = dir.getRowOffset();
		int dc = dir.getCellOffset(hex.radius, row, cell);
		if (row + dr < 0 || row + dr >= hex.getRowCount())
			return null;
		if (cell + dc < 0 || cell + dc >= hex.getCellCount(row + dr))
			return null;
		return new ArrowResult(row, cell, dir, hex);
	}

	public boolean equals(LocateResult loc) {
		if (loc instanceof ArrowResult) {
			ArrowResult arr = (ArrowResult) loc;
			return arr.row == row && arr.cell == cell && arr.dir == dir;
		}
		return false;
	}

	@Override
	public ResultType getType() {
		return ResultType.ARROW;
	}

	@Override
	public double getX() {
		int dr = dir.getRowOffset();
		int dc = dir.getCellOffset(hex.radius, row, cell);
		double x0 = hex.getX(row, cell);
		double x1 = hex.getX(row + dr, cell + dc);
		return (x0 + x1) / 2;
	}

	@Override
	public double getY() {
		int dr = dir.getRowOffset();
		int dc = dir.getCellOffset(hex.radius, row, cell);
		double y0 = hex.getY(row, cell);
		double y1 = hex.getY(row + dr, cell + dc);
		return (y0 + y1) / 2;
	}

	@Override
	public String toString() {
		return "(" + row + "," + cell + ") to " + dir.ind;
	}

}
