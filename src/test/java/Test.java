import java.util.Random;

public class Test {

	public static void main(String[] args) {
		int t = 100;
		long t0 = time_b(t, 1000000, 100, 10);
		System.out.println(t0);
		long t1 = time_b(t, 1000000, 100, 1000);
		System.out.println(t1);
		long t2 = time_b(t, 1000000, 100, 100000);
		System.out.println(t2);

		long t3 = time_b(t, 1000, 100, 100);
		System.out.println(t3);
		long t4 = time_b(t, 10000, 100, 100);
		System.out.println(t4);
		long t5 = time_b(t, 100000, 100, 100);
		System.out.println(t5);
	}

	private static void test_all(int n, int m, int k) {
		int[] sorted = new int[n];
		int[] input = new int[n];
		generate_input(n, m, sorted, input);
		{
			int[] output = input.clone();
			quick_sort(0, n - 1, output);
			boolean pass = true;
			for (int i = 0; i < n; i++) {
				pass &= output[i] == sorted[i];
			}
			System.out.println("QuickSort pass test: " + pass);
		}
		{
			boolean pass = true;
			for (int i = 0; i < n; i++) {
				int val = quick_select(i, 0, n - 1, input.clone());
				pass &= val == sorted[i];
			}
			System.out.println("QuickSelect pass test: " + pass);
		}
		{
			boolean pass = true;
			int[] output = new int[n / k];
			for (int i = 0; i < k; i++) {
				part_a(n, k, i, input.clone(), output);
				quick_sort(0, n / k - 1, output);
				for (int j = 0; j < n / k; j++) {
					pass &= output[j] == sorted[i * n / k + j];
				}
			}
			System.out.println("Part A pass test: " + pass);
		}
		{
			boolean pass = true;
			int[] output = new int[k];
			part_b(n, k, input.clone(), output);
			for (int i = 0; i < k; i++) {
				pass &= output[i] == sorted[i * n / k];
			}
			System.out.println("Part B pass test: " + pass);
		}
	}

	private static long time_b(int t, int n, int m, int k) {
		long time = 0;
		for (int i = 0; i < t; i++) {

			int[] sorted = new int[n];
			int[] input = new int[n];
			generate_input(n, m, sorted, input);
			int[] output = new int[k];
			long t0 = System.nanoTime();
			part_b(n, k, input.clone(), output);
			long t1 = System.nanoTime();
			time += t1 - t0;
		}
		time /= t;
		return (long) (time * 1e6 / (n * Math.log(k)));
	}

	private static void generate_input(int n, int m, int[] sorted, int[] input) {
		Random random = new Random();
		int cur = 0;
		for (int i = 0; i < n; i++) {
			cur += random.nextInt(m) + 1;
			input[i] = sorted[i] = cur;
		}
		for (int i = 0; i < n; i++) {
			int x = random.nextInt(n);
			int t = input[i];
			input[i] = input[x];
			input[x] = t;
		}
	}

	private static void part_a(int n, int k, int i, int[] input, int[] output) {
		int l = quick_select(i * n / k, 0, n - 1, input.clone());
		int r = quick_select((i + 1) * n / k - 1, 0, n - 1, input.clone());
		int ind = 0;
		for (int j = 0; j < n; j++) {
			if (input[j] >= l && input[j] <= r) {
				output[ind++] = input[j];
			}
		}
	}

	private static void part_b(int n, int k, int[] input, int[] output) {
		multi_select(0, n - 1, n / k, 0, k - 1, input, output);
	}

	private static void quick_sort(int l, int r, int[] input) {
		if (l >= r) {
			return;
		}
		if (r - l == 1) {
			if (input[l] > input[r]) {
				swap(l, r, input);
			}
			return;
		}
		int p = partition(l, r, input);
		quick_sort(l, p - 1, input);
		quick_sort(p + 1, r, input);
	}

	private static int quick_select(int index, int l, int r, int[] data) {
		if (l == r) {
			return data[l];
		}
		int p = partition(l, r, data);
		if (p == index) {
			return data[p];
		} else if (index < p) {
			return quick_select(index, l, p - 1, data);
		} else {
			return quick_select(index, p + 1, r, data);
		}
	}

	private static void multi_select(int l, int r, int x, int kl, int kr, int[] data, int[] output) {
		if (l > r) {
			return;
		}
		if (l == r) {
			if (l % x == 0) {
				output[l / x] = data[l];
			}
			return;
		}
		int p = partition(l, r, data);
		int ikp = p / x;
		if (p % x == 0) {
			// index ikp handled
			output[ikp] = data[p];
			// handle index kl to ikp-1
			if (kl <= ikp - 1) {
				multi_select(l, p - 1, x, kl, ikp - 1, data, output);
			}
			// handle index ikp+1 to kr
			if (ikp + 1 <= kr) {
				multi_select(p + 1, r, x, ikp + 1, kr, data, output);
			}
		} else {
			if (kl <= ikp)
				multi_select(l, p - 1, x, kl, ikp, data, output);
			if (ikp < kr)
				multi_select(p + 1, r, x, ikp + 1, kr, data, output);
		}
	}

	private static int partition(int l, int r, int[] data) {
		int pivot = data[r];
		int x = l;
		for (int i = l; i < r; i++) {
			if (data[i] < pivot) {
				swap(i, x, data);
				x++;
			}
		}
		swap(x, r, data);
		return x;
	}

	private static void swap(int a, int b, int[] data) {
		int x = data[a];
		data[a] = data[b];
		data[b] = x;
	}

}
