import java.util.Comparator;
import java.util.Iterator;

public class BinaryTreeDictionary<K,V> implements IDictionary<K,V> {

    private class Node<K,V> {
        int height;
        Entry<K,V> entry;

        Node<K,V> parent;
        Node<K,V> left;
        Node<K,V> right;

        private Node(K key, V value) {
            this.height = 0;
            this.entry = new Entry<K,V>(key,value);
            this.parent = null;
            this.left = null;
            this.right = null;
        }

    }

    private int size;
    private Node<K,V> root;
    private V old;
    private Comparator<? super K> cmp;

    @SuppressWarnings("unchecked")
    public BinaryTreeDictionary() {
        size = 0;
        root = null;
        old = null;
        cmp = (x,y) -> ((Comparable<? super K>) y).compareTo(x);
    }

    @SuppressWarnings("unchecked")
    public BinaryTreeDictionary(java.util.Comparator<? super K> comparator) {
        if (comparator == null) {
            this.cmp = (x,y) -> ((Comparable<? super K>) y).compareTo(x);
        } else {
            this.cmp = comparator;
        }
        size = 0;
        root = null;
        old = null;
    }

    @Override
    public V insert(K key, V value) {
        root = insertR(key,value,root);
        if (root != null) {
            root.parent = null;
        }
        return old;
    }

    private Node<K,V> insertR(K key, V value, Node<K,V> p) {
        if (p == null) {
            p = new Node<K,V>(key, value);
            old = null;
            size++;
        } else if (cmp.compare(p.entry.getKey(), key) < 0) {
            p.left = insertR(key,value, p.left);
            if (p.left != null) {
                p.left.parent = p;
            }
        } else if (cmp.compare(p.entry.getKey(), key) > 0) {
            p.right = insertR(key, value, p.right);
            if (p.right != null) {
                p.right.parent = p;
            }
        } else {
            old = p.entry.getValue();
            p.entry.setValue(value);
        }

        p = balance(p);
        return p;
    }

    @Override
    public Iterator<IDictionary.Entry<K, V>> iterator() {
        return new BinaryTreeDictionaryIterator();        
    }

    private Node<K,V> leftMostDescendant(Node<K,V> p) {
        assert p != null;
        while (p.left != null) {
            p = p.left;
        }
        return p;
    }

    private Node<K,V> parentOfLeftMostAncestor(Node<K,V> p) {
        assert p != null;
        while (p.parent != null && p.parent.right == p) {
            p = p.parent;
        }
        return p.parent;
    }

    @Override
    public V remove(K key) {
        root = removeR(key, root);
        if (root != null) {
            root.parent = null;
        }
        return old;
    }

    private Node<K,V> removeR(K key, Node<K,V> p) {
        if (p == null) {
            old = null;
        } else if (cmp.compare(p.entry.getKey(), key) < 0) {
            p.left = removeR(key, p.left);
            if (p.left != null) {
                p.left.parent = p;
            }
        } else if (cmp.compare(p.entry.getKey(), key) > 0) {
            p.right = removeR(key, p.right);
            if (p.right != null) {
                p.right.parent = p;
            }
        } else if (p.right == null || p.left == null) {
            old = p.entry.getValue();
            p = p.right != null ? p.right : p.left;
            size--;
        } else {
            Node<K,V> val = leftMostDescendant(p.right);
            old = p.entry.getValue();
            p.entry = new Entry<K,V>(val.entry.getKey(), val.entry.getValue());
            p.right = removeR(p.entry.getKey(), p.right);
            if (p.right != null) {
                p.right.parent = p;
            }            
        }

        p = balance(p);
        return p;
    }

    // Funktioniert
    @Override
    public V search(K key) {
        return searchR(key, root);
    }

    private V searchR(K key, Node<K,V> p) {
        if (p == null) {
            return null;
        } else if (cmp.compare(p.entry.getKey(), key) < 0) {
            return searchR(key, p.left);
        } else if (cmp.compare(p.entry.getKey(), key) > 0) {
            return searchR(key, p.right);
        } else {
            return p.entry.getValue();
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void wipe() {
        root = null;
        old = null;
        size = 0;
    }

    private Node<K,V> balance(Node<K,V> p) {
        if (p == null) {
            return null;
        }
        p.height = Math.max(getHeight(p.left), getHeight(p.right)) + 1;
        if (getBalance(p) == -2) {
            if (getBalance(p.left) <= 0) {
                p = rotateRight(p);
            } else {
                p = rotateLeftRight(p);
            }
        } else if (getBalance(p) == 2) {
            if (getBalance(p.right) >= 0) {
                p = rotateLeft(p);
            } else {
                p = rotateRightLeft(p);
            }
        }
        return p;
    }

    private Node<K,V> rotateRight(Node<K,V> p) {
        assert p.left != null;
        Node<K,V> q = p.left;
        p.left = q.right;
        if (p.left != null) {
            p.left.parent = p;
        }
        q.right = p;
        if (q.right != null) {
            q.right.parent = q;
        }
        p.height = Math.max(getHeight(p.left), getHeight(p.right)) + 1;
        q.height = Math.max(getHeight(q.left), getHeight(q.right)) + 1;
        return q;
    }

    private Node<K,V> rotateLeft(Node<K,V> p) {
        assert p.right != null;
        Node<K,V> q = p.right;
        p.right = q.left;
        if (p.right != null) {
            p.right.parent = p;
        }
        q.left = p;
        if (q.left != null) {
            q.left.parent = q;
        }
        p.height = Math.max(getHeight(p.left), getHeight(p.right)) + 1;
        q.height = Math.max(getHeight(q.left), getHeight(q.right)) + 1;
        return q;
    }

    private Node<K,V> rotateRightLeft(Node<K,V> p) {
        assert p.right != null;
        p.right = rotateRight(p.right);
        if (p.right != null) {
            p.right.parent = p;
        }        
        return rotateLeft(p);
    }

    private Node<K,V> rotateLeftRight(Node<K,V> p) {
        assert p.left != null;
        p.left = rotateLeft(p.left);
        if (p.left != null) {
            p.left.parent = p;
        }        
        return rotateRight(p);
    }


    private int getHeight(Node<K,V> p) {
        if (p == null) {
            return -1;
        } else {
            return p.height;
        }
    }

    private int getBalance(Node<K,V> p) {
        if (p == null) {
            return 0;
        } else {
            return getHeight(p.right) - getHeight(p.left);
        }
    }

    public void prettyPrint() {
        printR(0, root);
    }

    private void printR(int level, Node<K, V> p) {
        printLevel(level);
        if (p == null) {
            System.out.println("#");
        } else {
            System.out.println(p.entry.getKey() + " " + p.entry.getValue() + "^" + ((p.parent == null) ? "null" : p.parent.entry.getKey().toString()));
            if (p.left != null || p.right != null) {
                printR(level + 1, p.left);
                printR(level + 1, p.right);
            }
        }
    }

    private static void printLevel(int level) {
        if (level == 0) {
            return;
        }
        for (int i = 0; i < level - 1; i++) {
            System.out.print("   ");
        }
        System.out.print("|__");
    }

    private class BinaryTreeDictionaryIterator implements Iterator<Entry<K,V>> {

        int count = 0;
        Node<K,V> current = null;

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public Entry<K, V> next() {
            if (count == 0) {
                current = leftMostDescendant(root);
                count++;
            } else {
                if (current != null && current.right != null) {
                    current = leftMostDescendant(current.right);
                } else {
                    current = parentOfLeftMostAncestor(current);
                }
                count++;
            }

            return current.entry;
        }

    }
}

