package class05;

import java.util.ArrayList;

public class Code02_SizeBalancedTreeMap {

	public static class SizeBalancedTreeMap<K extends Comparable<K>, V> {
		private int root;
		private int len;
		private int[] left;
		private int[] right;
		private int[] size;
		private ArrayList<K> keys;
		private ArrayList<V> values;

		public SizeBalancedTreeMap(int init) {
			left = new int[init + 1];
			right = new int[init + 1];
			size = new int[init + 1];
			keys = new ArrayList<K>();
			values = new ArrayList<V>();
			keys.add(null);
			values.add(null);
			root = 0;
			len = 0;
		}

		private int rightRotate(int index) {
			int iLeft = left[index];
			left[index] = right[iLeft];
			right[iLeft] = index;
			size[iLeft] = size[index];
			size[index] = size[left[index]] + size[right[index]] + 1;
			return iLeft;
		}

		private int leftRotate(int index) {
			int iRight = right[index];
			right[index] = left[iRight];
			left[iRight] = index;
			size[iRight] = size[index];
			size[index] = size[left[index]] + size[right[index]] + 1;
			return iRight;
		}

		private int maintain(int index) {
			if (size[left[left[index]]] > size[right[index]]) {
				index = rightRotate(index);
				right[index] = maintain(right[index]);
				index = maintain(index);
			} else if (size[right[left[index]]] > size[right[index]]) {
				left[index] = leftRotate(left[index]);
				index = rightRotate(index);
				left[index] = maintain(left[index]);
				right[index] = maintain(right[index]);
				index = maintain(index);
			} else if (size[right[right[index]]] > size[left[index]]) {
				index = leftRotate(index);
				left[index] = maintain(left[index]);
				index = maintain(index);
			} else if (size[left[right[index]]] > size[left[index]]) {
				right[index] = rightRotate(right[index]);
				index = leftRotate(index);
				left[index] = maintain(left[index]);
				right[index] = maintain(right[index]);
				index = maintain(index);
			}
			return index;
		}

		private int findLastIndex(K key) {
			int pre = root;
			int cur = root;
			while (cur != 0) {
				pre = cur;
				if (key.compareTo(keys.get(cur)) == 0) {
					break;
				} else if (key.compareTo(keys.get(cur)) < 0) {
					cur = left[cur];
				} else {
					cur = right[cur];
				}
			}
			return pre;
		}

		private int findLastNoSmallIndex(K key) {
			int ans = 0;
			int cur = root;
			while (cur != 0) {
				if (key.compareTo(keys.get(cur)) == 0) {
					ans = cur;
					break;
				} else if (key.compareTo(keys.get(cur)) < 0) {
					ans = cur;
					cur = left[cur];
				} else {
					cur = right[cur];
				}
			}
			return ans;
		}

		private int findLastNoBigIndex(K key) {
			int ans = 0;
			int cur = root;
			while (cur != 0) {
				if (key.compareTo(keys.get(cur)) == 0) {
					ans = cur;
					break;
				} else if (key.compareTo(keys.get(cur)) < 0) {
					cur = left[cur];
				} else {
					ans = cur;
					cur = right[cur];
				}
			}
			return ans;
		}

		private int add(int index, K key, V value) {
			if (index == 0) {
				index = ++len;
				keys.add(key);
				values.add(value);
				size[index] = 1;
				left[index] = 0;
				right[index] = 0;
				return index;
			} else {
				size[index]++;
				if (key.compareTo(keys.get(index)) < 0) {
					left[index] = add(left[index], key, value);
				} else {
					right[index] = add(right[index], key, value);
				}
				return maintain(index);
			}
		}

		private int getIndex(int index, int kth) {
			if (kth == size[left[index]] + 1) {
				return index;
			} else if (kth <= size[left[index]]) {
				return getIndex(left[index], kth);
			} else {
				return getIndex(right[index], kth - size[left[index]] - 1);
			}
		}

		public int size() {
			return len;
		}

		public boolean containsKey(K key) {
			if (key == null) {
				throw new RuntimeException("invalid parameter.");
			}
			int lastIndex = findLastIndex(key);
			return lastIndex != 0 && key.compareTo(keys.get(lastIndex)) == 0 ? true : false;
		}

		public void put(K key, V value) {
			if (key == null) {
				throw new RuntimeException("invalid parameter.");
			}
			if (len == size.length - 1) {
				throw new RuntimeException("size balanced tree is full.");
			}
			int lastIndex = findLastIndex(key);
			if (lastIndex != 0 && key.compareTo(keys.get(lastIndex)) == 0) {
				values.set(lastIndex, value);
			} else {
				root = add(root, key, value);
			}
		}

		public K getIndexKey(int index) {
			if (index < 0 || index >= len) {
				throw new RuntimeException("invalid parameter.");
			}
			return keys.get(getIndex(root, index + 1));
		}

		public V getIndexValue(int index) {
			if (index < 0 || index >= len) {
				throw new RuntimeException("invalid parameter.");
			}
			return values.get(getIndex(root, index + 1));
		}

		public V get(K key) {
			if (key == null) {
				throw new RuntimeException("invalid parameter.");
			}
			int lastIndex = findLastIndex(key);
			if (lastIndex != 0 && key.compareTo(keys.get(lastIndex)) == 0) {
				return values.get(lastIndex);
			} else {
				return null;
			}
		}

		public K firstKey() {
			int cur = root;
			while (left[cur] != 0) {
				cur = left[cur];
			}
			return cur == 0 ? null : keys.get(cur);
		}

		public K lastKey() {
			int cur = root;
			while (right[cur] != 0) {
				cur = right[cur];
			}
			return cur == 0 ? null : keys.get(cur);
		}

		public K floorKey(K key) {
			if (key == null) {
				throw new RuntimeException("invalid parameter.");
			}
			int lastNoBigIndex = findLastNoBigIndex(key);
			return lastNoBigIndex == 0 ? null : keys.get(lastNoBigIndex);
		}

		public K ceilingKey(K key) {
			if (key == null) {
				throw new RuntimeException("invalid parameter.");
			}
			int lastNoSmallIndex = findLastNoSmallIndex(key);
			return lastNoSmallIndex == 0 ? null : keys.get(lastNoSmallIndex);
		}
	}

	public static void main(String[] args) {
		SizeBalancedTreeMap<String, Integer> sbt = new SizeBalancedTreeMap<String, Integer>(10000);

		sbt.put("pos", 512);
		sbt.put("zyp", 7123);
		sbt.put("unz", 542);
		sbt.put("abc", 5113);
		sbt.put("yzk", 665);
		sbt.put("fgi", 38776);
		sbt.put("bke", 2500540);
		sbt.put("lmn", 44334);
		sbt.put("abc", 11);
		sbt.put("abc", 111);

		for (int i = 0; i < sbt.size(); i++) {
			System.out.println(sbt.getIndexKey(i) + " , " + sbt.getIndexValue(i));
		}
		System.out.println(sbt.get("abc"));
		System.out.println(sbt.firstKey());
		System.out.println(sbt.lastKey());
		System.out.println(sbt.floorKey("bke"));
		System.out.println(sbt.ceilingKey("bke"));
		System.out.println(sbt.floorKey("ooo"));
		System.out.println(sbt.ceilingKey("ooo"));
		System.out.println(sbt.floorKey("aaa"));
		System.out.println(sbt.ceilingKey("aaa"));
		System.out.println(sbt.floorKey("zzz"));
		System.out.println(sbt.ceilingKey("zzz"));

	}

}
