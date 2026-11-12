import static org.junit.Assert.*;
import org.junit.Test;

public class HeapTest {
    @Test
    public void testinserts() {
        Heap heap = new Heap();

        heap.insert(5);
        heap.insert(3);
        heap.insert(8);
        heap.insert(1);

        assertEquals(4, heap.getSize());
    }

    @Test
    public void testNoValues() {
        Heap heap = new Heap();

        assertEquals(0, heap.getSize());

    }

    @Test
    public void testPeek() {
    Heap heap = new Heap();

    heap.insert(5);
    heap.insert(3);
    heap.insert(8);
    heap.insert(1);

    assertEquals(1, heap.peek());
    }

    @Test
    public void testPopInOrder() {
        Heap heap = new Heap();

        heap.insert(5);
        heap.insert(3);
        heap.insert(8);
        heap.insert(1);

        assertEquals(1, heap.poll());
        assertEquals(3, heap.poll());
        assertEquals(5, heap.poll());
        assertEquals(8, heap.poll());
    }

}
