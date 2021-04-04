package lab9;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implementation of interface Map61B with BST as core data structure.
 *
 * @author Your name here
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class Node {
        /* (K, V) pair stored in this Node. */
        private K key;
        private V value;

        /* Children of this Node. */
        private Node left;
        private Node right;

        private Node(K k, V v) {
            key = k;
            value = v;
        }
    }
    private class Entry{
        private K key;
        private V value;
        public Entry(K k,V v){
            key=k;
            value=v;
        }
    }
    private Node root;  /* Root node of the tree. */
    private int size; /* The number of key-value pairs in the tree */
    /* Creates an empty BSTMap. */
    public BSTMap() {
        this.clear();
    }

    /* Removes all of the mappings from this map. */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /** Returns the value mapped to by KEY in the subtree rooted in P.
     *  or null if this map contains no mapping for the key.
     */
    private V getHelper(K key, Node p) {
        if(p==null)return null;
        if (key.compareTo(p.key)<0)return getHelper(key,p.left);
        else if (key.compareTo(p.key)>0)return getHelper(key, p.right);
        return p.value;
    }

    /** Returns the value to which the specified key is mapped, or null if this
     *  map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        return getHelper(key,root);
    }

    /** Returns a BSTMap rooted in p with (KEY, VALUE) added as a key-value mapping.
      * Or if p is null, it returns a one node BSTMap containing (KEY, VALUE).
     */
    private Node putHelper(K key, V value, Node p) {
        if (p==null){
            size++;
            return new Node(key, value);
        }
        if (key.compareTo(p.key)<0)p.left=putHelper(key,value,p.left);
        else if (key.compareTo(p.key)>0)p.right=putHelper(key,value,p.right);
        else p.value=value;
        return p;
    }

    /** Inserts the key KEY
     *  If it is already present, updates value to be VALUE.
     */
    @Override
    public void put(K key, V value) {
        root=putHelper(key,value,root);
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    //////////////// EVERYTHING BELOW THIS LINE IS OPTIONAL ////////////////

    /* Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet() {
        Set<K>keySet=new TreeSet<>();
        keySet(root,keySet);
        return keySet;
    }
    private void keySet(Node x,Set<K>keySet){
        if (x==null)return;
        keySet(x.left,keySet);
        keySet.add(x.key);
        keySet(x.right,keySet);
    }
//    private Set<Entry> EntrySet(){
//        Set<Entry>EntrySet=new TreeSet<>();
//        EntrySet(root,EntrySet);
//        return EntrySet;
//    }
//    private void EntrySet(Node x,Set<Entry>EntrySet){
//        if (x==null)return;
//        EntrySet(x.left,EntrySet);
//        EntrySet.add(new Entry(x.key,x.value));
//        EntrySet(x.right,EntrySet);
//    }
    /** Removes KEY from the tree if present
     *  returns VALUE removed,
     *  null on failed removal.
     */
    @Override
    public V remove(K key) {
        root=remove(key,root);
        return valueRemoved;
    }
    private V valueRemoved;
    private Node remove(K key,Node x){
        if (x==null)return null;
        if (key.compareTo(x.key)<0)x.left=remove(key,x.left);
        else if(key.compareTo(x.key)>0)x.right=remove(key,x.right);
        else{
            if (x.right==null)return x.left;
            if (x.left==null)return x.right;
            Node t=Min(x.right);
            t.right=removeMin(x.right);
            t.left=x.left;
            valueRemoved=x.value;
            return t;
        }
        return x;
    }
    private Node Min(Node x){
        if (x.left==null)return x;
        return Min(x.left);
    }
    private Node removeMin(Node x){
        if (x.left==null)return x.right;
        x.left=removeMin(x.left);
        size--;
        return x;
    }
    /** Removes the key-value entry for the specified key only if it is
     *  currently mapped to the specified value.  Returns the VALUE removed,
     *  null on failed removal.
     **/
    @Override
    public V remove(K key, V value) {
        if (get(key)!=value)return null;
        return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTIterator();
    }
    private class BSTIterator implements Iterator<K>{
        private Iterator<K> cur;
        public BSTIterator(){
            cur=keySet().iterator();
        }
        @Override
        public boolean hasNext() {
            return cur.hasNext();
        }

        @Override
        public K next() {
            return cur.next();
        }
    }
}
