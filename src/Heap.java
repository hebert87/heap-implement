
import java.util.ArrayList;
import java.util.List;

/**
 * A min-heap. 
 * 
 * Must contain functionality for the following: (n = # of elements in heap)
 * 
 * Adding a new value to the heap  O(log(n))
 * Popping the front of the heap O(log(n))
 * Peeking at the front of the heap without removing the element O(1)
 * Getting the size of the heap O(1)
 * Checking whether the heap is empty O(1)
 * 
 * 
 * The Heap must hold ints
 * 
 * Use List/ArrayList as a backing array, but DO NOT use the 
 * PriorityQueue class. Do not make Node objects, use array
 * indexing instead.
 * 
 * It is up to you to make design decisions about how to:
 *  - name methods
 *  - determine return value and argument types
 *  - hold private instance variables
 */
public class Heap {

    List<Integer> items = new ArrayList<>();

    public void swap(int indexOne , int indexTwo){
        int item = items.get(indexOne);
        items.set(indexOne, items.get(indexTwo));
        items.set(indexTwo, item);

    }
    public void insert( int item){
        items.add(item);
        int newValueIndex = items.size()-1;
        int parent = (newValueIndex-1) /2;

        int childValue = items.get(newValueIndex);
        int parentValue = items.get(parent);

        while (newValueIndex > 0 && childValue<parentValue) {
                swap(newValueIndex, parent);
                newValueIndex = parent;
                parent = (newValueIndex-1)/2;
                childValue = items.get(newValueIndex);
                parentValue = items.get(parent);

        }
    }
    public int peek(){
        if(items.isEmpty()) throw new IllegalStateException();
        return items.get(0);

    }
    public int size(){
        return items.size();
    }

    public boolean isEmpty(){
        return items.isEmpty();
    }

    public int poll(){
        if(items.isEmpty()) throw new IllegalStateException();
        int rootItem = items.get(0);
        int lastElement = items.get(items.size()-1);
        items.set(0, lastElement);
        items.remove(items.size() -1);

        int currentIndex = 0;
        

        while(true){
            int leftChild = 2 *currentIndex + 1;
            int rightChild = 2 *currentIndex + 2;

            if (leftChild >= items.size()) {
                break;
    }
            int smallerChild = leftChild;


            if (rightChild < items.size() && items.get(rightChild) < items.get(leftChild)) {
            smallerChild = rightChild;
        }
            if (items.get(currentIndex) <= items.get(smallerChild)) {
            break;
        }
            swap(currentIndex, smallerChild);
            currentIndex = smallerChild;
        }


        return rootItem;
    }
}