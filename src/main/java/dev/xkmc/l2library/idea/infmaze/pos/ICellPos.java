package dev.xkmc.l2library.idea.infmaze.pos;

import org.jetbrains.annotations.NotNull;

public interface ICellPos extends Comparable<ICellPos> {

	IBasePos pos();

	int scale();

	default long getXMax() {
		return pos().x() + (1L << scale());
	}

	default long getYMax() {
		return pos().y() + (1L << scale());
	}

	default long getZMax() {
		return pos().z() + (1L << scale());
	}

	ICellPos offsetChunk(MazeDirection axis, long amount);

	IWallPos getWallPos(MazeDirection axis);

	@Override
	default int compareTo(@NotNull ICellPos o) {
		int s = Integer.compare(scale(), o.scale());
		if (s != 0) return s;
		return pos().compareTo(o.pos());
	}

}
