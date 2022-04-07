import java.util.Iterator;
import java.util.LinkedList;

public class HashDictionary<K,V> implements IDictionary<K,V>{

    private static final int INIT_CAPACITY = 31;
    private static final float MAX_LOADFACTOR = 2.0f;

    private int size;
    private float loadFactor;
    private int currentCap;
    private LinkedList<Entry<K,V>>[] table;

    @SuppressWarnings("unchecked")
    public HashDictionary() {
        size = 0;
        loadFactor = 0;
        table = new LinkedList[INIT_CAPACITY];
        currentCap = INIT_CAPACITY;
    }

    @SuppressWarnings("unchecked")
    public HashDictionary(int n) {
        size = 0;
        loadFactor = 0;
        table = new LinkedList[nextLargestPrime(n)];
        currentCap = table.length;
    }  

    @Override
    public V insert(K key, V value) {
        checkSize();

        int i = generateIndex(key);
        V old = search(key);

        if (old == null) {

            if (table[i] == null) {
                table[i] = new LinkedList<Entry<K,V>>();
            }
            table[i].add(new Entry<K,V>(key, value));
            size++;
            return null;
        } 
        for (var e : table[i]) {
            if (e.getKey().equals(key)) {
                e.setValue(value);
                break;
            }
        }
        return old;
    }

    @Override
    public Iterator<IDictionary.Entry<K, V>> iterator() {
        return new HashDictionaryIterator();
    }

    @Override
    public V remove(K key) {
        int i = generateIndex(key);
        V old = search(key);

        if (search(key) != null) {
            for (var e : table[i]) {
                if (e.getKey().equals(key)) {
                    table[i].remove(e);
                    size--;
                    return old;
                }
            }
        }

        return null;
    }

    @Override
    public V search(K key) {
        int i = generateIndex(key);
        if (table[i] != null) {
            for (var e : table[i]) {
                if (e.getKey().equals(key)) {
                    return e.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    private void checkSize() {
        loadFactor = size / currentCap;
        if (loadFactor > MAX_LOADFACTOR) {
            currentCap = nextLargestPrime(currentCap * 2);
            table = increaseSize(currentCap);
        }
    }

    @SuppressWarnings("unchecked")
    private LinkedList<Entry<K,V>>[] increaseSize(int n) {
        LinkedList<Entry<K,V>>[] newTable = new LinkedList[n];

            for (int i = 0; i < table.length; i++) {
                if (table[i] != null) {
                    for (var e : table[i]) {
                        int k = generateIndex(e.getKey());
                        if (newTable[k] == null) {
                            newTable[k] = new LinkedList<Entry<K,V>>();
                        }
                        newTable[k].add(new Entry<K,V>(e.getKey(), e.getValue()));
                    }
                }
            }
            
        return newTable;
    }

    private int generateIndex(K key) {
        int h = key.hashCode(); // hashcode generieren
        h = h % currentCap;  // Index ermitteln
        
        // Sicherstellen, das index > 0
        return h >= 0 ? h : -h;
    } 

    private int nextLargestPrime(int n) {
        while (!isPrime(n)) {
            n++;
        }
        return n;
    }

    private boolean isPrime(int n) {
        for (int i = 2; i < Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void wipe() {
        size = 0;
        loadFactor = 0;
        table = new LinkedList[INIT_CAPACITY];   
        currentCap = INIT_CAPACITY;  
    }

    private class HashDictionaryIterator implements Iterator<Entry<K,V>> {

        int lIndex = 0;
        int tIndex = 0;
        
        int count = 0;

        @Override
        public boolean hasNext() {
            if (count < size) {
                return true;
            }
            return false;
        }

        @Override
        public Entry<K,V> next() {
            while (table[tIndex] == null && tIndex < table.length) {
                tIndex++;
            }
            if (lIndex < table[tIndex].size()) {
                count++;
                return table[tIndex].get(lIndex++);                
            } else {
                lIndex = 0;
                tIndex++;
                return next();
            }
        }
    }
}
