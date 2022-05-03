package dev.xkmc.l2library.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import java.util.function.Consumer;

public class DelegateBlockProperties {

	public static final DelegateBlockProperties ORE_0 = new DelegateBlockProperties(Material.STONE, 3, 3).setTool(0);

	public static DelegateBlockProperties copy(Block b) {
		return new DelegateBlockProperties(BlockBehaviour.Properties.copy(b));
	}

	private final Block.Properties props;

	private DelegateBlockProperties(Material mat, float hard, float rest) {
		this(Block.Properties.of(mat), hard, rest);
	}

	private DelegateBlockProperties(Block.Properties mat) {
		props = mat;
	}

	private DelegateBlockProperties(Block.Properties mat, float hard, float rest) {
		props = mat;
		props.strength(hard, rest);
	}

	public Block.Properties getProps() {
		return props;
	}

	private DelegateBlockProperties setTool(int level) {
		//props.harvestTool(tool);
		//props.harvestLevel(level);
		return this;
	}

	public DelegateBlockProperties make(Consumer<BlockBehaviour.Properties> cons) {
		cons.accept(props);
		return this;
	}

}
