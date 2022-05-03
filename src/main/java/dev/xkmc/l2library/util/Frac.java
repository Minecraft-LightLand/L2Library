package dev.xkmc.l2library.util;

/**
 * represents a positive fraction
 *
 * @author arthur
 * @Date 2020-9-24
 */
public class Frac implements Comparable<Frac> {

	public long num, den;
	public double val;
	public boolean isFrac = true;

	public Frac(long num, long den) {
		this.num = num;
		this.den = den;
		validate();
	}

	private Frac(double val) {
		this.val = val;
		isFrac = false;
	}

	public static Frac mult(Frac f0, Frac f1) {
		if (!f0.isFrac || !f1.isFrac)
			return new Frac(f0.getVal() * f1.getVal());
		if (f0.den == 0 || f1.den == 0)
			return new Frac(1, 0);
		try {
			long gcd0 = gcd(f0.num, f1.den);
			long gcd1 = gcd(f1.num, f0.den);
			long num = Math.multiplyExact(f0.num / gcd0, f1.num / gcd1);
			long den = Math.multiplyExact(f0.den / gcd1, f1.den / gcd0);
			return new Frac(num, den);
		} catch (Exception e) {
			return new Frac(f0.getVal() * f1.getVal());
		}
	}

	private static long gcd(long a, long b) {
		long max = Math.max(a, b);
		long min = Math.min(a, b);
		return min == 0 ? max : gcd(min, max % min);
	}

	public void add(Frac o) {
		if (!isFrac || !o.isFrac) {
			isFrac = false;
			val = getVal() + o.getVal();
			num = den = 0;
			return;
		}
		if (den == 0) {
			return;
		}
		if (o.den == 0) {
			den = 0;
			num = 1;
			return;
		}
		double val = getVal();
		try {
			long gcd = gcd(den, o.den);
			long v0 = Math.multiplyExact(num, o.den / gcd);
			long v1 = Math.multiplyExact(o.num, den / gcd);
			num = Math.addExact(v0, v1);
			den = Math.multiplyExact(den, o.den / gcd);
		} catch (Exception e) {
			isFrac = false;
			num = den = 0;
			this.val = val + o.getVal();
		}
		validate();
	}

	@Override
	public int compareTo(Frac o) {
		if (equals(o))
			return 0;
		return Double.compare(getVal(), o.getVal());

	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Frac) {
			Frac f = (Frac) o;
			if (!isFrac || !((Frac) o).isFrac)
				return false;
			return f.num == num && f.den == den;
		}
		return false;
	}

	public double getVal() {
		if (!isFrac)
			return val;
		if (den == 0)
			return Double.POSITIVE_INFINITY;
		return 1.0 * num / den;
	}

	public void times(Frac base) {
		if (!isFrac || !base.isFrac) {
			isFrac = false;
			val = getVal() * base.getVal();
			num = den = 0;
			return;
		}
		if (den == 0)
			return;
		double val = getVal();
		try {
			long gcd0 = gcd(num, base.den);
			long gcd1 = gcd(base.num, den);
			num = Math.multiplyExact(num / gcd0, base.num / gcd1);
			den = Math.multiplyExact(den / gcd1, base.den / gcd0);
			validate();
		} catch (Exception e) {
			isFrac = false;
			num = den = 0;
			this.val = val * base.getVal();
		}
	}

	@Override
	public String toString() {
		if (!isFrac || num > 100 || den > 100)
			return "" + ((double) Math.round(getVal() * 100d) / 100d);
		return num + "/" + den;
	}

	private void validate() {
		if (!isFrac)
			return;
		if (den == 0) {
			num = 1;
			return;
		}
		long gcd = gcd(num, den);
		num /= gcd;
		den /= gcd;
	}

	public Frac revert() {
		if (isFrac)
			return new Frac(den, den - num);
		else return new Frac(1 / (1 - val));
	}

}