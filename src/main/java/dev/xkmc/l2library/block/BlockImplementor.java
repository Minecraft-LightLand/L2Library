package dev.xkmc.l2library.block;

import dev.xkmc.l2library.block.type.BlockMethod;
import dev.xkmc.l2library.block.type.MultipleBlockMethod;
import dev.xkmc.l2library.block.type.SingletonBlockMethod;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class BlockImplementor {

	final BlockBehaviour.Properties props;
	private final List<MultipleBlockMethod> list = new ArrayList<>();
	private final HashMap<Class<?>, SingletonBlockMethod> map = new HashMap<>();

	public BlockImplementor(BlockBehaviour.Properties p) {
		props = p;
	}

	public BlockImplementor addImpls(BlockMethod... impls) {
		for (BlockMethod impl : impls) {
			if (impl instanceof MultipleBlockMethod)
				list.add((MultipleBlockMethod) impl);
			if (impl instanceof SingletonBlockMethod one) {
				List<Class<?>> list = new ArrayList<>();
				addOneImpl(one.getClass(), list);
				for (Class<?> cls : list) {
					if (map.containsKey(cls)) {
						throw new RuntimeException("class " + cls + " is implemented twice with " + map.get(cls) + " and " + impl);
					} else {
						map.put(cls, one);
					}
				}
			}
		}
		return this;
	}

	private void addOneImpl(Class<?> cls, List<Class<?>> list) {
		Class<?> sup = cls.getSuperclass();
		if (sup != null && SingletonBlockMethod.class.isAssignableFrom(sup)) {
			addOneImpl(sup, list);
		}
		for (Class<?> ci : cls.getInterfaces()) {
			if (ci == SingletonBlockMethod.class) {
				throw new RuntimeException("class " + cls + " should not implement IOneImpl directly");
			}
			if (SingletonBlockMethod.class.isAssignableFrom(ci)) {
				Class<?>[] arr = ci.getInterfaces();
				if (arr.length == 1 && arr[0] == SingletonBlockMethod.class) {
					list.add(ci);
				} else {
					addOneImpl(ci, list);
				}
			}
		}
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public <T extends MultipleBlockMethod> Stream<T> execute(Class<T> cls) {
		return list.stream().filter(cls::isInstance).map(e -> (T) e);
	}

	@SuppressWarnings("unchecked")
	public <T extends MultipleBlockMethod> void forEach(Class<T> cls, Consumer<T> cons) {
		for (MultipleBlockMethod method : list) {
			if (cls.isInstance(method)) {
				cons.accept((T) method);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends MultipleBlockMethod, U> U reduce(Class<T> cls, U init, BiFunction<U, T, U> func) {
		for (MultipleBlockMethod method : list) {
			if (cls.isInstance(method)) {
				init = func.apply(init, (T) method);
			}
		}
		return init;
	}


	@SuppressWarnings("unchecked")
	public <T extends SingletonBlockMethod> Optional<T> one(Class<T> cls) {
		return Optional.ofNullable((T) map.get(cls));
	}

}
