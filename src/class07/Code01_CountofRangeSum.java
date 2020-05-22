package class07;

import java.util.HashSet;

public class Code01_CountofRangeSum {

	public static int countRangeSum1(int[] nums, int lower, int upper) {
		int n = nums.length;
		long[] sums = new long[n + 1];
		for (int i = 0; i < n; ++i)
			sums[i + 1] = sums[i] + nums[i];
		return countWhileMergeSort(sums, 0, n + 1, lower, upper);
	}

	private static int countWhileMergeSort(long[] sums, int start, int end, int lower, int upper) {
		if (end - start <= 1)
			return 0;
		int mid = (start + end) / 2;
		int count = countWhileMergeSort(sums, start, mid, lower, upper)
				+ countWhileMergeSort(sums, mid, end, lower, upper);
		int j = mid, k = mid, t = mid;
		long[] cache = new long[end - start];
		for (int i = start, r = 0; i < mid; ++i, ++r) {
			while (k < end && sums[k] - sums[i] < lower)
				k++;
			while (j < end && sums[j] - sums[i] <= upper)
				j++;
			while (t < end && sums[t] < sums[i])
				cache[r++] = sums[t++];
			cache[r] = sums[i];
			count += j - k;
		}
		System.arraycopy(cache, 0, sums, start, t - start);
		return count;
	}

	public static class SBTNode {
		public long key;
		public SBTNode l;
		public SBTNode r;
		public long size; // 不同key的size
		public long all; // 总的size

		public SBTNode(long k) {
			key = k;
			size = 1;
			all = 1;
		}
	}

	public static class SizeBalancedTreeSet {
		private SBTNode root;
		private HashSet<Long> set = new HashSet<>();

		private SBTNode rightRotate(SBTNode cur) {
			long same = cur.all - (cur.l != null ? cur.l.all : 0) - (cur.r != null ? cur.r.all : 0);
			SBTNode leftNode = cur.l;
			cur.l = leftNode.r;
			leftNode.r = cur;
			leftNode.size = cur.size;
			cur.size = (cur.l != null ? cur.l.size : 0) + (cur.r != null ? cur.r.size : 0) + 1;
			// all modify
			leftNode.all = cur.all;
			cur.all = (cur.l != null ? cur.l.all : 0) + (cur.r != null ? cur.r.all : 0) + same;
			return leftNode;
		}

		private SBTNode leftRotate(SBTNode cur) {
			long same = cur.all - (cur.l != null ? cur.l.all : 0) - (cur.r != null ? cur.r.all : 0);
			SBTNode rightNode = cur.r;
			cur.r = rightNode.l;
			rightNode.l = cur;
			rightNode.size = cur.size;
			cur.size = (cur.l != null ? cur.l.size : 0) + (cur.r != null ? cur.r.size : 0) + 1;
			// all modify
			rightNode.all = cur.all;
			cur.all = (cur.l != null ? cur.l.all : 0) + (cur.r != null ? cur.r.all : 0) + same;
			return rightNode;
		}

		private SBTNode matain(SBTNode cur) {
			if (cur == null) {
				return null;
			}
			if (cur.l != null && cur.l.l != null && cur.r != null && cur.l.l.size > cur.r.size) {
				cur = rightRotate(cur);
				cur.r = matain(cur.r);
				cur = matain(cur);
			} else if (cur.l != null && cur.l.r != null && cur.r != null && cur.l.r.size > cur.r.size) {
				cur.l = leftRotate(cur.l);
				cur = rightRotate(cur);
				cur.l = matain(cur.l);
				cur.r = matain(cur.r);
				cur = matain(cur);
			} else if (cur.r != null && cur.r.r != null && cur.l != null && cur.r.r.size > cur.l.size) {
				cur = leftRotate(cur);
				cur.l = matain(cur.l);
				cur = matain(cur);
			} else if (cur.r != null && cur.r.l != null && cur.l != null && cur.r.l.size > cur.l.size) {
				cur.r = rightRotate(cur.r);
				cur = leftRotate(cur);
				cur.l = matain(cur.l);
				cur.r = matain(cur.r);
				cur = matain(cur);
			}
			return cur;
		}

		private SBTNode add(SBTNode cur, long key, boolean contains) {
			if (cur == null) {
				return new SBTNode(key);
			} else {
				cur.all++;
				if (key == cur.key) {
					return cur;
				} else { // 还在左滑或者右滑
					if (!contains) {
						cur.size++;
					}
					if (key < cur.key) {
						cur.l = add(cur.l, key, contains);
					} else {
						cur.r = add(cur.r, key, contains);
					}
					return matain(cur);
				}
			}
		}

		public void add(long sum) {
			boolean contains = set.contains(sum);
			root = add(root, sum, contains);
			set.add(sum);
		}

		public long lessKeySize(long key) {
			SBTNode cur = root;
			long ans = 0;
			while (cur != null) {
				if (key == cur.key) {
					return ans + (cur.l != null ? cur.l.all : 0);
				} else if (key < cur.key) {
					cur = cur.l;
				} else {
					ans += cur.all - (cur.r != null ? cur.r.all : 0);
					cur = cur.r;
				}
			}
			return ans;
		}

		// > 7 8...
		// <8 ...<=7
		public long moreKeySize(long key) {
			return root != null ? (root.all - lessKeySize(key + 1)) : 0;
		}

	}

	public static int countRangeSum2(int[] nums, int lower, int upper) {
		SizeBalancedTreeSet treeSet = new SizeBalancedTreeSet();
		long sum = 0;
		int ans = 0;
		treeSet.add(0);
		for (int i = 0; i < nums.length; i++) {
			sum += nums[i];
			// sum = x [a,b] start > x-a -start < -x+a x-start < a
			// [a,b]
			// < a > b
			// i + 1 <a - <b
			long lessLowers = treeSet.moreKeySize(sum - lower);
			// sum = x [a,b] start < x-b -start > -x+b x-start > b
			long moreUppers = treeSet.lessKeySize(sum - upper);
			ans += i + 1 - lessLowers - moreUppers;
			treeSet.add(sum);
		}
		return ans;
	}

	// for test
	public static void printArray(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
	}

	// for test
	public static int[] generateArray(int len, int varible) {
		int[] arr = new int[len];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = (int) (Math.random() * varible);
		}
		return arr;
	}

	public static void main(String[] args) {
		int len = 200;
		int varible = 50;
		for (int i = 0; i < 10000; i++) {
			int[] test = generateArray(len, varible);
			int lower = (int) (Math.random() * varible) - (int) (Math.random() * varible);
			int upper = lower + (int) (Math.random() * varible);
			int ans1 = countRangeSum1(test, lower, upper);
			int ans2 = countRangeSum2(test, lower, upper);
			if (ans1 != ans2) {
				printArray(test);
				System.out.println(lower);
				System.out.println(upper);
				System.out.println(ans1);
				System.out.println(ans2);
			}
		}

	}

}
