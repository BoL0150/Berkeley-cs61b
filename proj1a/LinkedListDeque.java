public class LinkedListDeque<T> {

    private class Node{
        Node prev;
        T item;
        Node next;
        Node(Node prev,T item,Node next){
            this.item=item;
            this.prev=prev;
            this.next=next;
        }
    }
    private int size;
    private Node sentinel;
    public LinkedListDeque(){
        sentinel=new Node(null,null,null);
        sentinel.next=sentinel;
        sentinel.prev=sentinel;
        size=0;
    }
    public void addFirst(T item) {
        sentinel.next=new Node(sentinel,item,sentinel.next);
        sentinel.next.next.prev=sentinel.next;
        size++;
    }
    public void addLast(T item) {
        sentinel.prev=new Node(sentinel.prev,item,sentinel);
        sentinel.prev.prev.next=sentinel.prev;
        size++;
    }

    public boolean isEmpty() {
        return size==0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node pos=sentinel.next;
        for (int i=0;i<size-1;i++){
            System.out.print(pos.item+" ");
            pos=pos.next;
        }
        System.out.print(pos.item);
    }

    public T removeFirst() {
        if (sentinel.next==sentinel)
            return null;
        Node temp=sentinel.next;
        sentinel.next.next.prev=sentinel;
        sentinel.next=sentinel.next.next;
        size--;
        return temp.item;
    }

    public T removeLast() {
        if (sentinel.prev==sentinel)
            return null;
        Node temp=sentinel.prev;
        sentinel.prev.prev.next=sentinel;
        sentinel.prev=sentinel.prev.prev;
        size--;
        return temp.item;
    }

    public T get(int index) {
        Node pos=sentinel.next;
        if (index>=size)
            return null;
        for (int i=0;i<index;i++){
            pos=pos.next;
        }
        return pos.item;
    }
    public T getRecursive(int index){
        if (index>=size)
            return null;
        return getRecursive(0,index,sentinel.next);
    }
    private T getRecursive(int pos,int index,Node x){
        if (pos==index)
            return x.item;
        return getRecursive(pos+1,index,x.next);
    }
}
