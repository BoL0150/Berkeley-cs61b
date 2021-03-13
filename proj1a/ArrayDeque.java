public class ArrayDeque<T> implements Deque<T> {

    private int nextFirst;
    private int nextLast;
    private T[]items;
    private int size;
    public ArrayDeque(){
        items=(T[])new Object[8];
        nextFirst=5;
        nextLast=6;
        size=0;
    }
    public void resize(int capacity){
        T[]a=(T[])new Object[capacity];
        nextLast=(capacity*3)/4;
        nextFirst=nextLast-1;
        items=a;
        System.arraycopy(items,0,a,0,size);
    }
    @Override
    public void addFirst(T item) {
        if (size==items.length)
            resize(2*size);
        items[nextFirst]=item;
        size++;
        while (items[nextFirst]!=null){
            nextFirst--;
            if (nextFirst<0)nextFirst=size-1;
        }
    }

    @Override
    public void addLast(T item) {
        if (size==items.length)
            resize(2*size);
        items[nextLast]=item;
        size++;
        while (items[nextLast]!=null){
            nextLast++;
            if (nextLast>=size)nextLast=0;
        }
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i=0;i<size-1;i++)
            System.out.print(items[i]+" ");
        System.out.print(items[size-1]);
    }

    @Override
    public T removeFirst() {
        if (nextFirst==(items.length*3)/4-1)return null;
        nextFirst++;
        size--;
        if (items.length>=16&&size<items.length/4)
            resize(items.length/2);
        return items[nextFirst];
    }

    @Override
    public T removeLast() {
        if (nextLast==(items.length*3)/4)return null;
        nextLast--;
        size--;
        if (items.length>=16&&size<items.length/4)
            resize(items.length/2);
        return items[nextLast];
    }

    @Override
    public T get(int index) {
        if (index>=size)
            return null;
        return items[index];
    }
}
