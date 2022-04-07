import java.util.Iterator;

public interface IDictionary<K,V> extends Iterable<IDictionary.Entry<K,V>> {

    static class Entry<K,V> {

        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key; 
            this.value = value;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(V v) {
            V old = this.getValue();
            this.value = v;
            return old;
        }

        @Override
        public String toString() {
            return key.toString() + " -> " + value.toString();
        }
    }

    V insert(K key, V value);

    Iterator<IDictionary.Entry<K,V>> iterator();

    V remove(K key);

    V search(K key);

    int size();

    void wipe();

}