public class ArrayDeque<T> {

    private int nextFirst;
    private int nextLast;
    private int capacity;
    private T[]items;
    private int size;
    public ArrayDeque(){
        items=(T[])new Object[8];
        this.capacity=items.length;
        nextFirst=capacity-1;
        nextLast=0;
        size=0;
    }
    private void resize(int capacity){
        T[]a=(T[])new Object[capacity];
        //从nextFirst右边的第一个点开始复制
        //到nextLast左边的第一个点复制结束
        for (int i=1;i<=size;i++)
            a[i]=items[(++nextFirst)%this.capacity];
        this.capacity=capacity;
        nextFirst=0;
        nextLast=size+1;
        items=a;
    }
    public void addFirst(T item) {
        if (size==capacity)
            resize(capacity*2);
        items[nextFirst]=item;
        size++;
        nextFirst=nextFirst==0?capacity-1:nextFirst-1;
    }

    public void addLast(T item) {
        if (size==capacity)
            resize(capacity*2);
        items[nextLast]=item;
        size++;
        nextLast=(nextLast+1)%capacity;
    }

    public boolean isEmpty() {
        return size==0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i=(nextFirst+1)%capacity;i!=nextLast-1;i=(i+1)%capacity)
            System.out.print(items[i]+" ");
        System.out.print(items[nextLast-1]);
    }

    public T removeFirst() {
        if (size==0)return null;
        nextFirst=(nextFirst+1)%capacity;
        T temp=items[nextFirst];
        items[nextFirst]=null;
        size--;
        if (capacity>=16&&size<capacity/4)
            resize(capacity/2);
        return temp;
    }

    public T removeLast() {
        if (size==0)return null;
        nextLast=nextLast==0?capacity-1:nextLast-1;
        T temp=items[nextLast];
        items[nextLast]=null;
        size--;
        if (capacity>=16&&size<capacity/4)
            resize(capacity/2);
        return temp;
    }

    public T get(int index) {
        if (index>=size)
            return null;
        return items[(nextFirst+1+index)%capacity];
    }
}

