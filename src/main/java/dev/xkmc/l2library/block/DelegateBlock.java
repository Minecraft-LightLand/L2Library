package dev.xkmc.l2library.block;

import dev.xkmc.l2library.block.one.BlockEntityBlockMethod;
import dev.xkmc.l2library.block.type.BlockMethod;
import net.minecraft.world.level.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DelegateBlock extends Block {

	public static DelegateBlock newBaseBlock(DelegateBlockProperties p, BlockMethod... impl) {
		for (BlockMethod m : impl) {
			if (m instanceof BlockEntityBlockMethod<?>) {
				return new DelegateEntityBlockImpl(p, impl);
			}
		}
		return new DelegateBlockImpl(p, impl);
	}

	protected DelegateBlock(Properties props) {
		super(props);
	}

}