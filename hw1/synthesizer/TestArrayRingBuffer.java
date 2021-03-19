package synthesizer;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests the ArrayRingBuffer class.
 *  @author Josh Hug
 */

public class TestArrayRingBuffer {
    @Test
    public void someTest() {
        ArrayRingBuffer<Integer> arb = new ArrayRingBuffer<>(10);
        assertTrue(arb.isEmpty());
        assertEquals(arb.capacity,10);
        arb.enqueue(2);
        arb.enqueue(3);
        arb.enqueue(4);
        arb.enqueue(1);
        for (Integer t:arb){
            System.out.println(t);
        }
        arb.dequeue();
        arb.dequeue();
        arb.dequeue();
        arb.dequeue();
        for (Integer i:arb){
            System.out.println(i);
        }
//        System.out.println(arb.fillCount());
//        System.out.println(arb.isFull());
//        System.out.println(arb.dequeue());
//        System.out.println(arb.dequeue());
//        System.out.println(arb.peek());
//        System.out.println(arb.fillCount());
    }

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 
