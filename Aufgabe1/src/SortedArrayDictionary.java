import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public class SortedArrayDictionary<K,V> implements IDictionary<K,V> {
    
    private final static int INIT_CAPACITY = 32;

    private int size;
    private Entry<K,V>[] data;
    private Comparator<? super K> cmp;

    @SuppressWarnings("unchecked")
    public SortedArrayDictionary() {
        data =  new Entry[INIT_CAPACITY];
        cmp = (x,y) -> ((Comparable<? super K>) x).compareTo(y);
    }

    @SuppressWarnings("unchecked")
    public SortedArrayDictionary(java.util.Comparator<? super K> comparator) {
        if (comparator == null) {
            cmp = (x,y) -> ((Comparable<? super K>) x).compareTo(y);
        } else {
            cmp = comparator;
        }
        data = new Entry[INIT_CAPACITY];
    }

    @Override
    public V insert(K key, V value) {
        int i = searchKey(key);

        if (i != -1) {
            V old = data[i].getValue();
            data[i].setValue(value);
            return old;
        }
        if (size == data.length) {
            data = Arrays.copyOf(data, data.length * 2);
        }
        int j = size - 1;
        while (j >= 0 && cmp.compare(data[j].getKey(), key) < 0) {
            data[j+1] = data[j];
            j--;
        }
        data[j+1] = new Entry<K,V>(key, value);
        size++;
        return null;
    }

    @Override
    public Iterator<IDictionary.Entry<K, V>> iterator() {
        return new SortedArrayDictionaryIterator();
    }

    @Override
    public V remove(K key) {
        int i = searchKey(key);
        if (i == -1) {
            return null;
        }
        V old = data[i].getValue();
        for (int j = i; j < size - 1; j++) {
            data[j] = data[j+1];
        }
        data[size] = null;
        size--;
        return old;
    }

    @Override
    public V search(K key) {
        int i = searchKey(key);
        return i >= 0 ? data[i].getValue() : null;
    }

    // Binary Search: O(log n)
    private int searchKey(K key) {
        int li = 0;
        int re = size - 1;
        while (re >= li) {
            int m = (li + re) / 2;
            if (cmp.compare(data[m].getKey(), key) < 0) {
                re = m - 1;
            } else if (cmp.compare(data[m].getKey(), key) > 0) {
                li = m + 1;
            } else {
                return m;
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void wipe() {
        this.data =  new Entry[INIT_CAPACITY];
        size = 0;
    }

    private class SortedArrayDictionaryIterator implements Iterator<Entry<K,V>> {

        int current_pos = 0;

        @Override
        public boolean hasNext() {
            return current_pos < size;
        }

        @Override
        public Entry<K, V> next() {
            return data[current_pos++];
        }

    }

}
