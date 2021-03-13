public class ArrayDeque<T>{

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
    private void resize(int capacity){
        T[]a=(T[])new Object[capacity];
        nextLast=(capacity*3)/4;
        nextFirst=nextLast-1;
        System.arraycopy(items,0,a,0,size);
        items=a;

    }
    public void addFirst(T item) {
        if (size==items.length-1)
            resize(2*items.length);
        items[nextFirst]=item;
        size++;
        while (items[nextFirst]!=null){
            nextFirst--;
            if (nextFirst<0)nextFirst=items.length-1;
        }
    }

    public void addLast(T item) {
        if (size==items.length-1)
            resize(2*items.length);
        items[nextLast]=item;
        size++;
        while (items[nextLast]!=null){
            nextLast++;
            if (nextLast>=items.length)nextLast=0;
        }
    }

    public boolean isEmpty() {
        return size==0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i=0;i<items.length-1;i++){
            if (items[i]!=null)
            System.out.print(items[i]+" ");
        }
        if (items[items.length-1]!=null)
            System.out.print(items[size-1]);
    }

    public T removeFirst() {
        if (nextFirst==(items.length*3)/4-1)return null;
        nextFirst++;
        T temp=items[nextFirst];
        items[nextFirst]=null;
        size--;
        if (items.length>=16&&size<items.length/4)
            resize(items.length/2);
        return temp;
    }

    public T removeLast() {
        if (nextLast==(items.length*3)/4)return null;
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
        return items[index];
    }
    public static void main(String[] args){
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addLast(5);
        a.addLast(6);
        a.addLast(7);
        a.addFirst(4);

        a.addLast(8);
        a.addLast(9);
        a.addLast(10);
        a.addFirst(3);
        a.addFirst(2);
        a.addFirst(1);

        a.printDeque();

        a.removeFirst();
        a.removeLast();
        a.removeLast();
        a.removeLast();
        a.removeLast();
        a.removeLast();
        a.printDeque();
        System.out.println();
    }
}
