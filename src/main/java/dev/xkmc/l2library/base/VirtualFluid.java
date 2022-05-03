package dev.xkmc.l2library.base;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class VirtualFluid extends ForgeFlowingFluid {

	public VirtualFluid(Properties properties) {
		super(properties);
	}

	@Override
	public Fluid getSource() {
		return super.getSource();
	}

	@Override
	public Fluid getFlowing() {
		return this;
	}

	@Override
	public Item getBucket() {
		return Items.AIR;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public boolean isSource(FluidState state) {
		return false;
	}

	@Override
	public int getAmount(FluidState state) {
		return state.getValue(LEVEL);
	}

}