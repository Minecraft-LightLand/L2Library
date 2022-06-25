package dev.xkmc.l2library.idea.infmaze.pos;

import org.jetbrains.annotations.NotNull;

public interface IWallPos extends Comparable<IWallPos> {

	IBasePos pos();

	MazeAxis normal();

	int scale();

	default long getXMax() {
		return pos().x() + ((1L - normal().x) << scale());
	}

	default long getYMax() {
		return pos().y() + ((1L - normal().y) << scale());
	}

	default long getZMax() {
		return pos().z() + ((1L - normal().z) << scale());
	}

	default BasePos getMaxEnd() {
		return new BasePos(getXMax(), getYMax(), getZMax());
	}

	IWallPos offset(long du, long dv, int ds);

	IWallPos offset(long dx, long dy, long dz, int ds);

	@Override
	default int compareTo(@NotNull IWallPos o) {
		int comp = pos().compareTo(o.pos());
		if (comp != 0) return comp;
		int s = Integer.compare(scale(), o.scale());
		if (s != 0) return s;
		return Integer.compare(normal().ordinal(), o.normal().ordinal());
	}

}
