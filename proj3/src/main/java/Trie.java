import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Trie<Value> {
    private static int R = 256;
    private Node root;

    private static class Node {
        private Object val;
        private Map<Character, Node> next = new HashMap<>();
    }

    public Value get(String key) {
        Node x = get(key, root, 0);
        if (x==null) return null;
        return (Value) x.val;
    }

    private Node get(String key, Node x,int index) {
        if (x == null) return null;
        //index是字符串中下一个要比较的下标，也就是说，当前要比较的下标就是字符串的末尾
        //此时字符串除了最后一个字符，其他的都已经比较完了
        //现在，这个字符存在两种状态：是或不是
        //如果是，就返回该字符对应的值，如果不是，就返回null，所以不需要进行判断
        if (index == key.length()) {
            return x;
        }
        return get(key, x.next.get(key.charAt(index)), index + 1);
    }

    public void put(String key, Value value) {
        root = put(key, value, root, 0);
    }
    //put先查找，如果沿途中的字符与key中的字符一一对应，那么把value赋值给最后一个节点
    //如果在查找时遇到了空节点，那么将key中剩下的字符继续接下去，然后再将value赋值给最后一个节点
    private Node put(String key, Value value, Node x, int index) {
        if (x==null) x = new Node();
        if (index == key.length()) {
            x.val = value;
            return x;
        }
        char c=key.charAt(index);
        x.next.put(c, put(key, value, x.next.get(c), index + 1));
        return x;
    }

    public Iterable<String> keys() {
        return keyWithPrefix("");
    }
    public Iterable<String> keyWithPrefix(String s) {
        Queue<String> queue = new LinkedList<>();
        collect(get(s, root, 0), s, queue);
        return queue;
    }
    private void collect(Node x, String pre, Queue<String> queue) {
        if (x == null) return;
        if (x.val != null) queue.offer(pre);
        for (Map.Entry<Character, Node> entry : x.next.entrySet()) {
            collect(entry.getValue(), pre + entry.getKey(), queue);
        }
    }

    public String longestPrefixOf(String s) {
        int length = search(root, s, 0, 0);
        return s.substring(0, length);
    }

    private int search(Node x, String s, int index, int length) {
        if (x == null ) return length;
        if (x.val != null) length = index;
        if (index == s.length()) return length;
        return search(x.next.get(s.charAt(index)), s, index + 1, length);
    }

    public void delete(String key) {
        root = delete(root, key, 0);
    }

    private Node delete(Node x, String key, int index) {

        if (x==null) return null;
        //index实际上是下一次的下标,也就是字符串到了最后一个字符，查找到了所在位置，将值置为空
        if (index == key.length()) {
            x.val = null;
        }
        //如果字符串还没有匹配完，继续对下一个字符进行匹配
        else{
            //index指向下一次要匹配的字符下标
            char c = key.charAt(index);
            //将链接重置
            x.next.put(c, delete(x.next.get(c), key, index + 1));
        }
        //匹配完成，将当前节点的值置为null之后，对多余的节点进行删除
        //要求：树的叶结点的val必须存在
        //如果本身的val非空，则不需要再删除，直接返回自己
        if (x.val != null)
            return x;
        //如果本身的val为空，那么是否需要删除则取决于是不是叶结点
        //也就是说，如果该节点有非空链接，就可以不用删除
        //如果该节点全部都是空链接，就需要删除
        if (!x.next.isEmpty()) return x;
        return null;
    }
    public static void main(String[] args) {

    }
}