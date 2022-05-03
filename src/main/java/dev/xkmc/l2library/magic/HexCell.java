package dev.xkmc.l2library.magic;

import dev.xkmc.l2library.util.Frac;

public class HexCell {

	private final HexHandler hexHandler;
	public int row, cell;

	public HexCell(HexHandler hexHandler, int row, int cell) {
		this.hexHandler = hexHandler;
		this.row = row;
		this.cell = cell;
	}

	public boolean canWalk(HexDirection dir) {
		int dr = dir.getRowOffset();
		int dc = dir.getCellOffset(hexHandler.radius, row, cell);
		return row + dr >= 0 && row + dr < hexHandler.getRowCount() && cell + dc >= 0 && cell + dc < hexHandler.getCellCount(row + dr);
	}

	public boolean exists() {
		return hexHandler.cells[row][cell] > 0;
	}

	public SubHex getSubHex() {
		return hexHandler.subhex[hexHandler.getInd(row, cell)];
	}

	public double getX() {
		return hexHandler.getX(row, cell);
	}

	public double getY() {
		return hexHandler.getY(row, cell);
	}

	public boolean isConnected(HexDirection dir) {
		return (hexHandler.cells[row][cell] & dir.mask()) != 0;
	}

	public boolean isCorner() {
		return row % hexHandler.radius == 0 && (cell == 0 || cell == hexHandler.cells[row].length - 1);
	}

	public boolean isCorner(HexDirection dir) {
		int nr = row + dir.getRowOffset();
		int nc = cell + dir.getCellOffset(hexHandler.radius, row, cell);
		return nr % hexHandler.radius == 0 && (nc == 0 || nc == hexHandler.cells[nr].length - 1);
	}

	/**
	 * ans[i][j] means input at i will add ans[i][j] to output at j
	 */
	public Frac[][] matrix() {
		if (hexHandler.subhex[hexHandler.getInd(row, cell)] != null)
			return hexHandler.subhex[hexHandler.getInd(row, cell)].getMatrix();
		Frac[][] ans = new Frac[6][];
		for (int i = 0; i < 6; i++) {
			HexDirection dir = HexDirection.values()[i];
			if (isConnected(dir)) {
				ans[i] = new Frac[6];
				HexDirection opo = dir.next(3);
				if (isConnected(opo)) {
					ans[i][opo.ind] = new Frac(1, 1);
					continue;
				}
				HexDirection ccw = dir.next(2);
				HexDirection cw = dir.next(4);
				boolean bc0 = isConnected(ccw);
				boolean bc1 = isConnected(cw);
				if (bc0 || bc1) {
					if (bc0 && bc1)
						ans[i][ccw.ind] = ans[i][cw.ind] = new Frac(1, 2);
					else if (bc0)
						ans[i][ccw.ind] = new Frac(1, 1);
					else
						ans[i][cw.ind] = new Frac(1, 1);
					continue;
				}
				HexDirection bccw = dir.next(1);
				HexDirection bcw = dir.next(5);
				bc0 = isConnected(bccw);
				bc1 = isConnected(bcw);
				if (bc0 || bc1) {
					if (bc0 && bc1)
						ans[i][bccw.ind] = ans[i][bcw.ind] = new Frac(1, 2);
					else if (bc0)
						ans[i][bccw.ind] = new Frac(1, 1);
					else
						ans[i][bcw.ind] = new Frac(1, 1);
					continue;
				}
				ans[i][i] = new Frac(1, 1);
			}
		}
		return ans;
	}

	public void set(HexHandler.SubHexCore sub, int i, boolean b) {
		hexHandler.subhex[hexHandler.getInd(row, cell)] = new SubHex(sub, i, b);
	}

	/**
	 * no effect if the operation is out of bound
	 */
	public void toggle(HexDirection dir) {
		if (!canWalk(dir))
			return;
		int val = hexHandler.cells[row][cell];
		if (isCorner() && val != 0 && val != dir.mask()) {
			hexHandler.cells[row][cell] = 0;
			for (HexDirection d : HexDirection.values())
				if ((val & d.mask()) != 0) {
					int dr = d.getRowOffset();
					int dc = d.getCellOffset(hexHandler.radius, row, cell);
					hexHandler.cells[row + dr][cell + dc] ^= d.next(3).mask();
				}
		}
		int dr = row + dir.getRowOffset();
		int dc = cell + dir.getCellOffset(hexHandler.radius, row, cell);
		int dval = hexHandler.cells[dr][dc];
		if (isCorner(dir) && dval != 0 && dval != dir.next(3).mask()) {
			hexHandler.cells[dr][dc] = 0;
			for (HexDirection d : HexDirection.values())
				if ((dval & d.mask()) != 0) {
					int xr = d.getRowOffset();
					int xc = d.getCellOffset(hexHandler.radius, dr, dc);
					hexHandler.cells[dr + xr][dc + xc] ^= d.next(3).mask();
				}
		}
		hexHandler.cells[row][cell] ^= dir.mask();
		hexHandler.cells[dr][dc] ^= dir.next(3).mask();
	}

	public void walk(HexDirection dir) {
		if (!canWalk(dir))
			return;
		int dr = dir.getRowOffset();
		int dc = dir.getCellOffset(hexHandler.radius, row, cell);
		row += dr;
		cell += dc;
	}

	public void walk(HexDirection dir, int n) {
		for (int i = 0; i < n; i++)
			walk(dir);
	}

	boolean isInvalid() {
		for (HexDirection dir : HexDirection.values()) {
			if (canWalk(dir)) {
				int dr = dir.getRowOffset();
				int dc = dir.getCellOffset(hexHandler.radius, row, cell);
				boolean b0 = (hexHandler.cells[row][cell] & dir.mask()) != 0;
				boolean b1 = (hexHandler.cells[row + dr][cell + dc] & dir.next(3).mask()) != 0;
				if (b0 != b1)
					return true;
			} else if ((hexHandler.cells[row][cell] & dir.mask()) != 0)
				return true;
		}
		SubHex sub = hexHandler.subhex[hexHandler.getInd(row, cell)];
		return sub != null && sub.isInvalid(this) != 0;
	}

	public HexDirection getCorner() {
		if (row == 0)
			if (cell == 0)
				return HexDirection.UPPER_LEFT;
			else if (cell == hexHandler.cells[row].length - 1)
				return HexDirection.UPPER_RIGHT;
			else return null;
		else if (row == hexHandler.radius)
			if (cell == 0)
				return HexDirection.LEFT;
			else if (cell == hexHandler.cells[row].length - 1)
				return HexDirection.RIGHT;
			else return null;
		else if (row == hexHandler.radius * 2)
			if (cell == 0)
				return HexDirection.LOWER_LEFT;
			else if (cell == hexHandler.cells[row].length - 1)
				return HexDirection.LOWER_RIGHT;
			else return null;
		else return null;
	}

	public void toCorner(HexDirection selected) {
		row = hexHandler.radius;
		cell = hexHandler.radius;
		walk(selected, hexHandler.radius);
	}
}
