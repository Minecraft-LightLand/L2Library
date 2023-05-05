package dev.xkmc.l2library.init.events.damage;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class DamageTypeRoot implements DamageTypeWrapper {

	public static final TreeMap<ResourceKey<DamageType>, DamageTypeRoot> ROOTS = new TreeMap<>();

	public static DamageTypeRoot of(ResourceKey<DamageType> key) {
		return ROOTS.get(key);
	}

	private static final TreeMap<String, GenConfig> GEN = new TreeMap<>();
	private static boolean generated = false;

	public synchronized static void configureGeneration(Set<String> support, String modid, List<DamageTypeWrapper> list) {
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

	private final String source;
	private final ResourceKey<DamageType> type;
	protected final List<TagKey<DamageType>> inherent;
	protected final Set<DamageState> states;
	protected final Function<DamageTypeWrapper, DamageType> sup;

	private boolean frozen = false;
	private Object2IntArrayMap<DamageState> keys;
	private DamageTypeWrapper[] wrapper;

	public DamageTypeRoot(String source, ResourceKey<DamageType> type, List<TagKey<DamageType>> inherent, Function<DamageTypeWrapper, DamageType> sup) {
		this.source = source;
		this.inherent = inherent;
		this.type = type;
		this.states = DamageState.newSet();
		this.sup = sup;
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
		wrapper[0] = this;
	}

	@Nullable
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

	@Nullable
	@Override
	public DamageTypeWrapper enable(DamageState state) {
		return get(0, state);
	}

	@Override
	public DamageTypeWrapper toRoot() {
		return this;
	}

	@Override
	public DamageType getObject() {
		return sup.apply(this);
	}

	@Override
	public Set<DamageState> states() {
		return Set.of();
	}

	public void generate(GenConfig config) {
		freeze();
		boolean typeCompatible = config.support().contains(source);
		if (!typeCompatible) return;
		List<DamageState> list = new ArrayList<>(states);
		generate(new GenContext(config, DamageState.newSet(), list), Generate.DEFAULT, 0, 0);
	}

	private void generate(GenContext ctx, Generate gen, int key, int i) {
		if (i == ctx.list().size()) {
			TreeSet<DamageState> copy = DamageState.newSet();
			copy.addAll(ctx.set());
			DamageTypeWrapper ans = new DamageTypeVariant(ctx.config().modid(), this, key, ctx.set());
			boolean shouldGen = gen == Generate.ALLOW;
			if (copy.size() > 0 && ctx.config.modid.equals(source)) {
				shouldGen |= gen == Generate.DEFAULT;
			}
			if (shouldGen) {
				ctx.config().gen().add(ans);
				wrapper[key] = ans;
			}
			return;
		}
		DamageState st = ctx.list().get(i);
		generate(ctx, gen, key, i + 1);
		for (DamageState old : ctx.set()) {
			if (old.overrides(st) || st.overrides(old)) {
				gen = Generate.DENY;
			}
		}
		boolean stateCompatible = ctx.config().support().contains(st.getId().getNamespace());
		boolean stateOrigin = ctx.config.modid.equals(st.getId().getNamespace());
		boolean typeOrigin = ctx.config.modid.equals(source);
		if (!stateCompatible) {
			gen = Generate.DENY;
		}
		if (gen == Generate.DEFAULT && (typeOrigin || stateOrigin)) {
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
