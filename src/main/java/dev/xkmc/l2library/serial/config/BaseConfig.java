package dev.xkmc.l2library.serial.config;

import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SerialClass
public class BaseConfig {

	ResourceLocation id;

	public ResourceLocation getID() {
		return id;
	}

	/** called by Config Merger after it's merged*/
	protected void postMerge() {
	}

	public static <T, C> HashSet<T> collectSet(List<C> list, Function<C, Set<T>> getter) {
		return list.stream().reduce(new HashSet<T>(), (a, c) -> {
			a.addAll(getter.apply(c));
			return a;
		}, (a, b) -> {
			a.addAll(b);
			return a;
		});
	}

	public static <T, C> ArrayList<T> collectList(List<C> list, Function<C, List<T>> getter) {
		return list.stream().reduce(new ArrayList<>(), (a, c) -> {
			a.addAll(getter.apply(c));
			return a;
		}, (a, b) -> {
			a.addAll(b);
			return a;
		});
	}

	public static <T, C, K> HashMap<K, T> collectMap(List<C> list, Function<C, Map<K, T>> getter, Supplier<T> gen, BiConsumer<T, T> merger) {
		return list.stream().reduce(new HashMap<>(), (a, c) -> {
			getter.apply(c).forEach((k, v) -> merger.accept(a.computeIfAbsent(k, e -> gen.get()), v));
			return a;
		}, (a, b) -> {
			b.forEach((k, v) -> merger.accept(a.computeIfAbsent(k, e -> gen.get()), v));
			return a;
		});
	}

	public static <C, K, T> HashMap<K, T> overrideMap(List<C> list, Function<C, HashMap<K, T>> getter) {
		HashMap<K, T> ans = new HashMap<>();
		for (C c : list) {
			ans.putAll(getter.apply(c));
		}
		return ans;
	}

}
