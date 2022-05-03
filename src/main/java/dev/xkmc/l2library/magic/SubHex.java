package dev.xkmc.l2library.magic;

import dev.xkmc.l2library.util.Frac;

public class SubHex {

	public final HexHandler.SubHexCore core;
	public int rotation;
	public boolean flip;

	public SubHex(HexHandler.SubHexCore core, int rotation, boolean flip) {
		this.core = core;
		this.rotation = (rotation % 6 + 6) % 6;
		this.flip = flip;
	}

	public Frac[][] getMatrix() {
		Frac[][] ans = new Frac[6][];
		for (int i = 0; i < 6; i++) {
			int ni = ((flip ? 6 - i : i) + rotation) % 6;
			for (int j = 0; j < 6; j++) {
				int nj = ((flip ? 6 - j : j) + rotation) % 6;
				if (core.otho[i][j] != null) {
					if (ans[nj] == null)
						ans[nj] = new Frac[6];
					ans[nj][ni] = core.otho[i][j];
				}
			}
		}
		return ans;
	}

	/**
	 * invalid sides: bit mask
	 */
	public int isInvalid(HexCell c) {
		int ans = 0;
		for (int i = 0; i < 6; i++) {
			int ni = ((flip ? 6 - i : i) + rotation) % 6;
			HexDirection dir = HexDirection.values()[ni];
			boolean set = (core.exist & 1 << i) != 0;
			if (!c.canWalk(dir)) {
				if (set)
					ans |= 1 << ni;
				continue;
			}
			if (c.isConnected(dir) != set)
				return ans |= 1 << ni;
		}
		return ans;
	}

}
