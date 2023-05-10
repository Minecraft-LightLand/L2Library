package dev.xkmc.l2library.init.events.attack;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;

public interface DamageModifier {

	static DamageModifier nonlinearPre(int priority, Float2FloatFunction func) {
		return new Nonlinear(Order.PRE_NONLINEAR, priority, func);
	}

	static DamageModifier multPre(float val) {
		return new Multiplicative(Order.PRE_MULTIPLICATIVE, val);
	}

	static DamageModifier addPre(float val) {
		return new Additive(Order.PRE_ADDITIVE, val);
	}

	static DamageModifier multPost(float val) {
		return new Multiplicative(Order.POST_MULTIPLICATIVE, val);
	}

	static DamageModifier nonlinearPost(int priority, Float2FloatFunction func) {
		return new Nonlinear(Order.POST_NONLINEAR, priority, func);
	}

	static DamageModifier addPost(float val) {
		return new Additive(Order.POST_ADDITIVE, val);
	}

	enum Time {
		CRIT,
		ATTACK,
		HURT,
		DAMAGE,
	}

	enum Type {
		ADDITIVE,
		MULTIPLICATIVE,
		NONLINEAR
	}

	/**
	 * Damage modification type <ul>
	 * <li>PRE_NONLINEAR: nonlinear modification of damage before everything else.</li>
	 * <li>PRE_MULTIPLICATIVE: linear multiplicative modification of damage before additives.
	 * Usually used for damage scaling based on original damage only. </li>
	 * <li>PRE_ADDITIVE: nonlinear modification of damage before everything else.
	 * Regular additive damage should be here.</li>
	 * <li>POST_MULTIPLICATIVE: nonlinear modification of damage before everything else.
	 * Usually used for things you want to scale on top of additive damages.</li>
	 * <li>POST_NONLINEAR: default choice for nonlinear modification. It's after all regular modifications.</li>
	 * <li>POST_ADDITIVE: additive linear modification of damage at the end.
	 * Usually used for things you don't want to be scaled by others, such as additional flat damage.</li>
	 * <li>END_NONLINEAR: nonlinear modification of damage after everything else.</li>
	 * </ul>
	 */
	enum Order {
		PRE_NONLINEAR(Type.NONLINEAR),
		PRE_MULTIPLICATIVE(Type.MULTIPLICATIVE),
		PRE_ADDITIVE(Type.ADDITIVE),
		POST_MULTIPLICATIVE(Type.MULTIPLICATIVE),
		POST_NONLINEAR(Type.NONLINEAR),
		POST_ADDITIVE(Type.ADDITIVE),
		END_NONLINEAR(Type.NONLINEAR);

		public final Type type;

		Order(Type type) {
			this.type = type;
		}
	}

	float modify(float val);

	int priority();

	Order order();

}

record Additive(Order order, float n) implements DamageModifier {

	@Override
	public float modify(float val) {
		return val + n;
	}

	@Override
	public int priority() {
		return 0;
	}

}


record Multiplicative(Order order, float n) implements DamageModifier {

	@Override
	public float modify(float val) {
		return val * n;
	}

	@Override
	public int priority() {
		return 0;
	}

}

record Nonlinear(Order order, int priority, Float2FloatFunction func) implements DamageModifier {

	@Override
	public float modify(float val) {
		return func.get(val);
	}

}