package dev.xkmc.l2library.idea.infmaze.pos;

public record BoundBox(BasePos p0, BasePos p1) {

	public BoundBox intersect(BoundBox other) {
		return new BoundBox(new BasePos(
				Math.max(p0.x(), other.p0.x()),
				Math.max(p0.y(), other.p0.y()),
				Math.max(p0.z(), other.p0.z())
		), new BasePos(
				Math.min(p1.x(), other.p1.x()),
				Math.min(p1.y(), other.p1.y()),
				Math.min(p1.z(), other.p1.z())
		));
	}

	public long size() {
		return Math.max(0, p1.x() - p0.x()) * Math.max(0, p1.y() - p0.y()) * Math.max(0, p1.z() - p0.z());
	}

	public BoundBox inflate(int factor) {
		return new BoundBox(p0.scale(factor), p1.scale(factor));
	}

	public BoundBox inflate(int x, int y, int z) {
		return new BoundBox(p0.offset(-x, -y, -z), p1.offset(x, y, z));
	}

	public BoundBox inflate(MazeDirection dire, int x) {
		return new BoundBox(p0.offset(dire, -x), p1.offset(dire, x));
	}

}
