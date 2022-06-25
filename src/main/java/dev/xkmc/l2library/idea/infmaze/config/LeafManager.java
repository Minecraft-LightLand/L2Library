package dev.xkmc.l2library.idea.infmaze.config;

public class LeafManager {

	public double leafChance(int scale) {
		return scale < 3 ? 1 : 0;
	}

}
