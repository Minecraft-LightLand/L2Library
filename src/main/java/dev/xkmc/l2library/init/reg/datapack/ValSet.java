package dev.xkmc.l2library.init.reg.datapack;

import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public interface ValSet<K, V> {

	@Nullable
	V get(K k);

	Stream<Pair<K, V>> getAll();

}
