package dev.xkmc.l2library.idea.infmaze.pos;

import org.jetbrains.annotations.NotNull;

public interface IBasePos extends Comparable<IBasePos> {

	long x();

	long y();

	long z();

	IBasePos offset(MazeDirection axis, long amount);

	IBasePos offset(long x, long y, long z);

	@Override
	default int compareTo(@NotNull IBasePos o) {
		int dx = Long.compare(x(), o.x());
		if (dx != 0) return dx;
		int dy = Long.compare(y(), o.y());
		if (dy != 0) return dy;
		return Long.compare(z(), o.z());
	}

	default long l1dist(IBasePos pos) {
		return Math.abs(pos.x() - x()) + Math.abs(pos.y() - y()) + Math.abs(pos.z() - z());
	}

}
