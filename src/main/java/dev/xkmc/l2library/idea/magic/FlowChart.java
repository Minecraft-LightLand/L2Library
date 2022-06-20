package dev.xkmc.l2library.idea.magic;

import dev.xkmc.l2library.util.math.Frac;

import java.util.ArrayList;
import java.util.List;

/**
 * the summary of the flow in a diagram <br>
 * each {@code Frac[]} represents the output formula <br>
 * each {@code Frac} represents the coefficient of each input <br>
 * the order is the same as direction: starts from the right, clockwise <br>
 * <hr>
 * {@code matrix} is the output flow in the diagram <br>
 * {@code flows} is the intermediate flow in the diagram
 */
public class FlowChart {

	public final List<Flow> flows = new ArrayList<>();
	public final Frac[][] matrix;

	public FlowChart(Frac[][] matrix) {
		this.matrix = matrix;
	}

	public class Flow {
		public final ArrowResult arrow;
		public Frac[] forward, backward;

		Flow(ArrowResult arrow) {
			flows.add(this);
			this.arrow = arrow;
		}

		public boolean flawed() {
			for (Frac fr : forward) {
				if (fr != null && fr.isFrac && fr.den == 0)
					return true;
			}
			for (Frac fr : backward) {
				if (fr != null && fr.isFrac && fr.den == 0)
					return true;
			}
			return false;
		}

	}

}
