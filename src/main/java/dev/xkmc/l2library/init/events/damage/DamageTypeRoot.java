package dev.xkmc.l2library.init.events.damage;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.*;

public class DamageTypeRoot implements DamageTypeWrapper {

	public static final TreeMap<ResourceKey<DamageType>, DamageTypeRoot> ROOTS = new TreeMap<>();

	private static final TreeMap<String, GenConfig> GEN = new TreeMap<>();
	private static boolean generated = false;

	public static void configureGeneration(Set<String> support, String modid, List<DamageTypeWrapper> list) {
		GEN.put(modid, new GenConfig(support, modid, list));
	}

	public static void generateAll() {
		if (generated) return;
		generated = true;
		for (GenConfig config : GEN.values()) {
			for (DamageTypeRoot val : ROOTS.values()) {
				val.generate(config);
			}
		}
	}

	private final ResourceKey<DamageType> type;
	private final Set<DamageState> states;

	private boolean frozen = false;
	private Object2IntArrayMap<DamageState> keys;
	private DamageTypeWrapper[] wrapper;

	public DamageTypeRoot(ResourceKey<DamageType> type) {
		this.type = type;
		this.states = DamageState.newSet();
		ROOTS.put(type, this);
	}

	public ResourceKey<DamageType> type() {
		return type;
	}

	public void add(DamageState state) {
		if (frozen) {
			throw new IllegalStateException("Registration already frozen");
		}
		states.add(state);
	}

	private void freeze() {
		if (frozen) return;
		frozen = true;
		int size = 1 << states.size();
		keys = new Object2IntArrayMap<>();
		int index = 0;
		for (DamageState state : states) {
			keys.put(state, index);
			index++;
		}
		wrapper = new DamageTypeWrapper[size];
	}

	protected DamageTypeWrapper get(int index, DamageState state) {
		return wrapper[index | 1 << keys.getInt(state)];
	}

	protected boolean isEnabled(int index, DamageState state) {
		return (index & 1 << keys.getInt(state)) != 0;
	}

	@Override
	public boolean validState(DamageState state) {
		return keys.containsKey(state);
	}

	@Override
	public boolean isEnabled(DamageState state) {
		return false;
	}

	@Override
	public DamageTypeWrapper enable(DamageState state) {
		return get(0, state);
	}

	public void generate(GenConfig config) {
		freeze();
		List<DamageState> list = new ArrayList<>(states);
		generate(new GenContext(config, DamageState.newSet(), list), Generate.DEFAULT, 0, 0);
	}

	private void generate(GenContext ctx, Generate gen, int key, int i) {
		if (i == ctx.list().size()) {
			if (wrapper[key] != null) return;
			TreeSet<DamageState> copy = DamageState.newSet();
			copy.addAll(ctx.set());
			DamageTypeWrapper ans = new DamageTypeVariant(ctx.config().modid(), this, key, ctx.set());
			wrapper[key] = ans;
			if (gen == Generate.ALLOW) {
				ctx.config().gen().add(ans);
			}
			return;
		}
		DamageState st = ctx.list().get(i);
		generate(ctx, gen, key, i + 1);
		if (!ctx.config().support().contains(st.getId().getNamespace())) {
			gen = Generate.DENY;
		}
		if (gen == Generate.DEFAULT && st.getId().getNamespace().equals(ctx.config().modid())) {
			gen = Generate.ALLOW;
		}
		ctx.set().add(st);
		generate(ctx, gen, key | 1 << i, i + 1);
		ctx.set().remove(st);
	}

	record GenConfig(Set<String> support, String modid, List<DamageTypeWrapper> gen) {

	}

	record GenContext(GenConfig config, TreeSet<DamageState> set, List<DamageState> list) {

	}

	enum Generate {
		DEFAULT, DENY, ALLOW
	}

}
