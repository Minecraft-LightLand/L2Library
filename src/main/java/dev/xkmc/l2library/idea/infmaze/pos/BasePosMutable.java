package dev.xkmc.l2library.idea.infmaze.pos;

public class BasePosMutable implements IBasePos {

	public long x, y, z;

	public BasePosMutable(long x, long y, long z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BasePosMutable(IBasePos pos) {
		this(pos.x(), pos.y(), pos.z());
	}

	@Override
	public long x() {
		return x;
	}

	@Override
	public long y() {
		return y;
	}

	@Override
	public long z() {
		return z;
	}

	@Override
	public BasePosMutable offset(MazeDirection axis, long amount) {
		return offset(axis.x * amount, axis.y * amount, axis.z * amount);
	}

	@Override
	public BasePosMutable offset(long dx, long dy, long dz) {
		x += dx;
		y += dy;
		z += dz;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IBasePos pos) {
			return compareTo(pos) == 0;
		}
		return false;
	}

}
