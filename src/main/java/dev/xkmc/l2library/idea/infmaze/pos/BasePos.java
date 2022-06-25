package dev.xkmc.l2library.idea.infmaze.pos;

public record BasePos(long x, long y, long z) implements IBasePos {

	public BasePos(IBasePos pos) {
		this(pos.x(), pos.y(), pos.z());
	}

	@Override
	public BasePos offset(MazeDirection axis, long amount) {
		return offset(axis.x * amount, axis.y * amount, axis.z * amount);
	}

	@Override
	public BasePos offset(long dx, long dy, long dz) {
		return new BasePos(x + dx, y + dy, z + dz);
	}

	public BasePos scale(int scale) {
		return new BasePos(x * scale, y * scale, z * scale);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IBasePos pos) {
			return compareTo(pos) == 0;
		}
		return false;
	}

}
