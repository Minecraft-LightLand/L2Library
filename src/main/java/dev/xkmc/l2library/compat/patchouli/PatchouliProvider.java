package dev.xkmc.l2library.compat.patchouli;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.RegistrateProvider;
import dev.xkmc.l2library.serial.network.RecordDataProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.fml.LogicalSide;

import java.util.function.BiConsumer;

public class PatchouliProvider extends RecordDataProvider implements RegistrateProvider, BiConsumer<String, Record> {

	private final AbstractRegistrate<?> owner;

	private BiConsumer<String, Record> map;

	public PatchouliProvider(AbstractRegistrate<?> owner, DataGenerator gen) {
		super(gen, "Patchouli Provider");
		this.owner = owner;
	}

	public void accept(String path, Record rec) {
		if (map == null) {
			throw new IllegalStateException("Cannot accept recipes outside of a call to registerRecipes");
		}
		map.accept(path, rec);
	}

	@Override
	public LogicalSide getSide() {
		return LogicalSide.SERVER;
	}

	@Override
	public void add(BiConsumer<String, Record> map) {
		this.map = map;
		owner.genData(PatchouliHelper.PATCHOULI, this);
		this.map = null;
	}

}
