import java.util.Scanner;

/**
 * Interactive runner program for testing your Heap implementation.
 * 
 * Commands:
 *   - Add a value
 *   - Pop the front value
 *   - Peek at the front value
 *   - Check the size
 *   - Check whether the heap is empty
 *   - Quit
 * 
 * Wherever you see TODO comments, you will need to fill in the appropriate
 * code to construct your Heap or call one of its methods. The method names
 * and return types are up to you based on your design decisions.
 */
public class HeapRunner {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // TODO: Construct your Heap here.
        Heap heap = new Heap();

        System.out.println("=== Heap Tester ===");

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("a") || choice.equals("add")) {
                System.out.print("Enter an integer to add: ");
                String input = scanner.nextLine().trim();
                int value;
                try {
                    value = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("That wasn't a valid integer. Try again.");
                    continue;
                }
 
                // TODO: Call your "add" method on the heap, passing in 'value'.
                heap.insert(value);                
                // TODO: Print message letting user know value was added
                System.out.println(value + " was added.");
 
            } else if (choice.equals("p") || choice.equals("pop")) {
                // TODO: Call your "pop" method and print the result.
                // Properly handle the case of an empty heap!
                if (heap.isEmpty()) {
                    System.out.println("There is nothing to remove.");
                } else {
                    System.out.println("Removed: " + heap.poll());
                }
                
            } else if (choice.equals("k") || choice.equals("peek")) {
                // TODO: Call your "peek" method and print the result
                // Properly handle the case of an empty heap!
                if (heap.isEmpty()) {
                    System.out.println("There is nothing to peek at.");
                } else {
                    System.out.println("Smallest value: " + heap.peek());
                }
            } else if (choice.equals("s") || choice.equals("size")) {
                // TODO: Call your "size" method and print the result
                System.out.println("Heap size: " + heap.size());

            } else if (choice.equals("e") || choice.equals("empty")) {
                // TODO: Call your "isEmpty" method and print the result
                System.out.println("Is heap empty? " + heap.isEmpty());

            } else if (choice.equals("q") || choice.equals("quit")) {
                break;

            } else {
                System.out.println("Unknown command. Please try again.");
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    /**
     * Prints the menu of available commands.
     */
    private static void printMenu() {
        System.out.println();
        System.out.println("---- Commands ----");
        System.out.println("  a / add   - add a value to the heap");
        System.out.println("  p / pop   - pop the front value off the heap");
        System.out.println("  k / peek  - peek at the front value of the heap");
        System.out.println("  s / size  - print the size of the heap");
        System.out.println("  e / empty - check whether the heap is empty");
        System.out.println("  q / quit  - exit the program");
        System.out.print("Enter command: ");
    }
}