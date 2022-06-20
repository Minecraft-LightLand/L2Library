package dev.xkmc.l2library.idea.magic;

import dev.xkmc.l2library.util.math.Frac;

import java.util.*;

/**
 * calculation utility
 */
class HexCalc {

	private final HexHandler hexHandler;
	final CalcCell[][] ccell;
	final List<CalcCell> list;
	final Set<Arrow> pool = new HashSet<>();
	Queue<Arrow> head = new ArrayDeque<>();
	private Arrow[] in;
	private Map<CalcCell, Map<HexDirection, FlowChart.Flow>> flowmap;

	/**
	 * complete all calculation in constructor
	 */
	public HexCalc(HexHandler hexHandler) {
		// construct calc cell array
		this.hexHandler = hexHandler;
		ccell = new CalcCell[hexHandler.cells.length][];
		boolean[][] used = new boolean[hexHandler.cells.length][];
		for (int i = 0; i < ccell.length; i++) {
			ccell[i] = new CalcCell[hexHandler.cells[i].length];
			used[i] = new boolean[hexHandler.cells[i].length];
			for (int j = 0; j < ccell[i].length; j++)
				ccell[i][j] = new CalcCell(hexHandler, i, j);
		}

		// corner list
		list = new ArrayList<>();
		list.add(ccell[hexHandler.radius][hexHandler.getCellCount(hexHandler.radius) - 1]);
		list.add(ccell[hexHandler.radius * 2][hexHandler.getCellCount(hexHandler.radius * 2) - 1]);
		list.add(ccell[hexHandler.radius * 2][0]);
		list.add(ccell[hexHandler.radius][0]);
		list.add(ccell[0][0]);
		list.add(ccell[0][hexHandler.getCellCount(0) - 1]);
		for (CalcCell cc : list)
			cc.origin = true;

		// processing queue to add all connected cells
		Queue<CalcCell> queue = new ArrayDeque<>();
		for (CalcCell cc : list)
			if (cc.exists()) {
				used[cc.row][cc.cell] = true;
				queue.add(cc);
			}
		while (queue.size() > 0) {
			CalcCell cc = queue.poll();
			for (HexDirection dir : HexDirection.values()) {
				CalcCell cx = cc.neighbor(dir);
				if (cx == null)
					continue;
				if (used[cx.row][cx.cell])
					continue;
				used[cx.row][cx.cell] = true;
				queue.add(cx);
			}
		}

		// delete unused cells
		for (int i = 0; i < used.length; i++)
			for (int j = 0; j < used[i].length; j++)
				if (!used[i][j])
					ccell[i][j] = null;

		// construct flow data
		for (CalcCell[] calcCells : ccell)
			for (CalcCell calcCell : calcCells)
				if (calcCell != null)
					calcCell.init();


		// reduce redundant cells first
		for (CalcCell[] calcCells : ccell)
			for (CalcCell calcCell : calcCells)
				if (calcCell != null && calcCell.count <= 2)
					calcCell.remove();

		// reduce flow and complete calculation
		for (CalcCell[] calcCells : ccell)
			for (CalcCell calcCell : calcCells)
				if (calcCell != null && calcCell.count > 2)
					calcCell.remove();
	}

	/**
	 * return a summary of the flow in this diagram
	 */
	public FlowChart getMatrix(boolean withFlow) {
		// construct corner flow matrix
		Frac[][] matrix = new Frac[6][6]; // the input-output matrix
		in = new Arrow[6]; // the input array
		Arrow[] out = new Arrow[6];
		for (int i = 0; i < 6; i++) {
			if (!list.get(i).exists())
				continue;
			for (Arrow v : list.get(i).output)
				if (v != null)
					in[i] = v;
			for (Arrow v : list.get(i).input)
				if (v != null)
					out[i] = v;
		}
		for (int i = 0; i < 6; i++) {
			if (out[i] == null)
				continue;
			for (int j = 0; j < 6; j++) {
				if (in[j] == null)
					continue;
				matrix[j][i] = out[i].map.get(in[j]);
			}
		}

		FlowChart ans = new FlowChart(matrix);
		if (!withFlow)
			return ans;

		// calculate flows from each corner
		// reset counter
		head.clear();
		Set<Arrow> origins = new HashSet<>();
		for (Arrow v : pool) {
			v.user.clear();
			v.rely = 0;
			if (v.src.origin)
				origins.add(v);
		}
		// reset dependency
		for (Arrow v : pool)
			for (Arrow dep : v.map.keySet())
				if (!dep.src.origin) {
					dep.user.add(v);
					v.rely++;
				}
		for (Arrow v : pool)
			if (!v.src.origin && !v.dst.origin && v.rely == 0)
				head.add(v);

		flowmap = new HashMap<>();
		while (head.size() > 0) {
			Arrow next = head.poll();
			next.remove();
			addFlow(ans, next);
		}
		for (int i = 0; i < 6; i++) {
			addFlow(ans, in[i]);
			addFlow(ans, out[i]);
		}
		HexCalcException.trycatch(ans);
		return ans;
	}

	private void addFlow(FlowChart ans, Arrow next) {
		if (next == null)
			return;
		Frac[] formula = new Frac[6];
		for (int i = 0; i < 6; i++)
			if (in[i] != null)
				if (in[i] == next)
					formula[i] = new Frac(1, 1);
				else
					formula[i] = next.map.get(in[i]);
		CalcCell src = next.src;
		HexDirection dir = next.dir;
		if (dir.ind >= 3) {
			src = next.dst;
			dir = dir.next(3);
		}
		Map<HexDirection, FlowChart.Flow> sub;
		if (flowmap.containsKey(src))
			sub = flowmap.get(src);
		else
			flowmap.put(src, sub = new HashMap<>());
		FlowChart.Flow flow;
		if (sub.containsKey(dir))
			flow = sub.get(dir);
		else
			sub.put(dir, flow = ans.new Flow(new ArrowResult(src.row, src.cell, dir, hexHandler)));
		if (src == next.src)
			flow.forward = formula;
		else
			flow.backward = formula;
	}

	/**
	 * represents a flow from src to dst
	 */
	class Arrow {

		/**
		 * flow expression as linear sums, to be reduced
		 */
		final Map<Arrow, Frac> map = new HashMap<>();

		/**
		 * dependent of this flow, to be eliminated
		 */
		final Set<Arrow> user = new HashSet<>();
		CalcCell src, dst;
		HexDirection dir;
		int rely = 0;

		Arrow(CalcCell src, HexDirection dir) {
			this.src = src;
			this.dir = dir;
		}

		@Override
		public String toString() {
			return "from " + src + " to " + dst;
		}

		/**
		 * clear looping flow
		 */
		void clear() {
			Frac frac = map.remove(this);
			user.remove(this);
			if (frac == null)
				return;
			Frac base = frac.revert();
			for (Frac f : map.values())
				f.times(base);
		}

		/**
		 * attach flow source
		 */
		void put(Arrow var, Frac frac) {
			if (map.containsKey(var))
				frac.add(map.get(var));
			map.put(var, frac);
			var.user.add(this);
		}

		/**
		 * remove this arrow from network
		 */
		void remove() {
			clear();
			for (Arrow v : user) {
				Frac base = v.map.remove(this);
				for (Map.Entry<Arrow, Frac> ent : map.entrySet())
					v.put(ent.getKey(), Frac.mult(base, ent.getValue()));
				v.rely--;
				if (v.rely == 0)
					head.add(v);
			}
			for (Arrow v : map.keySet())
				v.user.remove(this);
		}

	}

	/**
	 * represents a cell during calculation
	 */
	class CalcCell extends HexCell {

		int count;
		boolean origin;
		Arrow[] input, output;

		/**
		 * construct the output flow
		 */
		CalcCell(HexHandler hex, int row, int cell) {
			super(hex, row, cell);
			output = new Arrow[6];
			for (int i = 0; i < 6; i++) {
				HexDirection dir = HexDirection.values()[i];
				if (isConnected(dir)) {
					output[i] = new Arrow(this, dir);
					count++;
				}
			}
		}

		@Override
		public String toString() {
			return (origin ? "origin " : "") + "(r = " + row + ", c = " + cell + ")";
		}

		/**
		 * connect the input flow
		 */
		void init() {
			for (Arrow a : output)
				if (a != null)
					pool.add(a);
			input = new Arrow[6];
			Frac[][] vars = this.matrix();
			// find input arrows from neighboring cells and attach them to output
			for (int i = 0; i < 6; i++) {
				HexDirection dir = HexDirection.values()[i];
				if (vars[i] != null) {
					input[i] = neighbor(dir).output[dir.next(3).ind];
					input[i].dst = this;
					if (!origin)
						for (int j = 0; j < 6; j++)
							if (vars[i][j] != null)
								output[j].put(input[i], vars[i][j]);
				}
			}
		}

		CalcCell neighbor(HexDirection dir) {
			if (!canWalk(dir))
				return null;
			int nr = dir.getRowOffset();
			int nc = dir.getCellOffset(hexHandler.radius, row, cell);
			return ccell[row + nr][cell + nc];
		}

		/**
		 * finalize all arrows
		 */
		void remove() {
			if (origin)
				return;
			for (Arrow v : output)
				if (v != null && !v.dst.origin)
					v.remove();
		}

	}

}
