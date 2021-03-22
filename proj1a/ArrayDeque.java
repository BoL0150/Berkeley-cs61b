public class ArrayDeque<T> {

    private int nextFirst;
    private int nextLast;
    private T[]items;
    private int size;
    public ArrayDeque(){
        items=(T[])new Object[8];
        nextFirst=items.length-1;
        nextLast=0;
        size=0;
    }
    private void resize(int capacity){
        T[]a=(T[])new Object[capacity];
        System.arraycopy(items,0,a,0,nextLast);
        int length=items.length-1-nextFirst;
        if (nextFirst!=items.length-1)
            System.arraycopy(items,nextFirst+1,a,capacity-length,length);
        nextFirst=capacity-length-1;
        items=a;
    }
    public void addFirst(T item) {
        if (nextFirst-1==nextLast)
            resize(items.length*2);
        items[nextFirst]=item;
        size++;
        nextFirst--;
    }

    public void addLast(T item) {
        if (nextFirst-1==nextLast)
            resize(items.length*2);
        items[nextLast]=item;
        size++;
        nextLast++;
    }

    public boolean isEmpty() {
        return size==0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i=(nextFirst+1)%items.length;i!=nextLast-1;i=(i+1)%items.length)
            System.out.print(items[i]+" ");
        System.out.print(items[nextLast-1]);
    }

    public T removeFirst() {
        if (nextFirst==items.length-1)return null;
        nextFirst++;
        T temp=items[nextFirst];
        items[nextFirst]=null;
        size--;
        if (items.length>=16&&size<items.length/4)
            resize(items.length/2);
        return temp;
    }

    public T removeLast() {
        if (nextLast==0)return null;
        nextLast--;
        T temp=items[nextLast];
        items[nextLast]=null;
        size--;
        if (items.length>=16&&size<items.length/4)
            resize(items.length/2);
        return temp;
    }

    public T get(int index) {
        if (index>=size)
            return null;
        return items[(nextFirst+1+index)%items.length];
    }
}

