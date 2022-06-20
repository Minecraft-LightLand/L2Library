package dev.xkmc.l2library.base.tile;

import com.mojang.datafixers.util.Pair;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * from Create
 */
public class CombinedTankWrapper implements IFluidHandler {

	public enum Type {
		INSERT, EXTRACT, ALL
	}

	private final List<Pair<IFluidHandler, Type>> list = new ArrayList<>();

	protected int[] baseIndex;
	protected int tankCount;
	protected boolean enforceVariety;

	public CombinedTankWrapper build() {
		this.baseIndex = new int[list.size()];
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			index += list.get(i).getFirst().getTanks();
			baseIndex[i] = index;
		}
		this.tankCount = index;
		return this;
	}

	public CombinedTankWrapper add(Type type, IFluidHandler... handlers) {
		for (IFluidHandler handler : handlers) {
			list.add(Pair.of(handler, type));
		}
		return this;
	}


	protected Iterable<IFluidHandler> fillable() {
		return list.stream().filter(e -> e.getSecond() != Type.EXTRACT).map(Pair::getFirst).toList();
	}

	protected Iterable<IFluidHandler> drainable() {
		return list.stream().filter(e -> e.getSecond() != Type.INSERT).map(Pair::getFirst).toList();
	}

	public CombinedTankWrapper enforceVariety() {
		enforceVariety = true;
		return this;
	}

	@Override
	public int getTanks() {
		return tankCount;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		int index = getIndexForSlot(tank);
		IFluidHandler handler = getHandlerFromIndex(index);
		tank = getSlotFromIndex(tank, index);
		return handler.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		int index = getIndexForSlot(tank);
		IFluidHandler handler = getHandlerFromIndex(index);
		int localSlot = getSlotFromIndex(tank, index);
		return handler.getTankCapacity(localSlot);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		int index = getIndexForSlot(tank);
		IFluidHandler handler = getHandlerFromIndex(index);
		int localSlot = getSlotFromIndex(tank, index);
		return handler.isFluidValid(localSlot, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.isEmpty())
			return 0;

		int filled = 0;
		resource = resource.copy();

		boolean fittingHandlerFound = false;
		Outer:
		for (boolean searchPass : new boolean[]{true, false}) {
			for (IFluidHandler iFluidHandler : fillable()) {

				for (int i = 0; i < iFluidHandler.getTanks(); i++)
					if (searchPass && iFluidHandler.getFluidInTank(i)
							.isFluidEqual(resource))
						fittingHandlerFound = true;

				if (searchPass && !fittingHandlerFound)
					continue;

				int filledIntoCurrent = iFluidHandler.fill(resource, action);
				resource.shrink(filledIntoCurrent);
				filled += filledIntoCurrent;

				if (resource.isEmpty() || fittingHandlerFound || enforceVariety && filledIntoCurrent != 0)
					break Outer;
			}
		}

		return filled;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty())
			return resource;

		FluidStack drained = FluidStack.EMPTY;
		resource = resource.copy();

		for (IFluidHandler iFluidHandler : drainable()) {
			FluidStack drainedFromCurrent = iFluidHandler.drain(resource, action);
			int amount = drainedFromCurrent.getAmount();
			resource.shrink(amount);

			if (!drainedFromCurrent.isEmpty() && (drained.isEmpty() || drainedFromCurrent.isFluidEqual(drained)))
				drained = new FluidStack(drainedFromCurrent.getFluid(), amount + drained.getAmount(),
						drainedFromCurrent.getTag());
			if (resource.isEmpty())
				break;
		}

		return drained;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		FluidStack drained = FluidStack.EMPTY;

		for (IFluidHandler iFluidHandler : drainable()) {
			FluidStack drainedFromCurrent = iFluidHandler.drain(maxDrain, action);
			int amount = drainedFromCurrent.getAmount();
			maxDrain -= amount;

			if (!drainedFromCurrent.isEmpty() && (drained.isEmpty() || drainedFromCurrent.isFluidEqual(drained)))
				drained = new FluidStack(drainedFromCurrent.getFluid(), amount + drained.getAmount(),
						drainedFromCurrent.getTag());
			if (maxDrain == 0)
				break;
		}

		return drained;
	}

	protected int getIndexForSlot(int slot) {
		if (slot < 0)
			return -1;
		for (int i = 0; i < baseIndex.length; i++)
			if (slot - baseIndex[i] < 0)
				return i;
		return -1;
	}

	protected IFluidHandler getHandlerFromIndex(int index) {
		if (index < 0 || index >= list.size())
			return (IFluidHandler) EmptyHandler.INSTANCE;
		return list.get(index).getFirst();
	}

	protected int getSlotFromIndex(int slot, int index) {
		if (index <= 0 || index >= baseIndex.length)
			return slot;
		return slot - baseIndex[index - 1];
	}
}
