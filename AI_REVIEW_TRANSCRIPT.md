# AI Code Review Transcript: Heap Implementation

**Tool:** Claude Code (Claude Opus 5.5), VS Code extension
**Date:** 2026-11-11
**Scope:** Review only. The student asked that the code not be rewritten for them. The assistant edited no source files; the student made every code change.

Notes:
- In two places the assistant ran read-only shell commands to search the project's source files (for example, to find callers of renamed methods). Those commands are summarized in brackets and not reproduced.
- The student's first two messages included the full contents of `Heap.java`. Both versions are reproduced below.

---

## 1. Student

> I am a Java student learning about heaps and wanting to practice good OOP. WITHOUT JUST REWRITING THE CODE FOR ME, please let me know what parts of this are and are not idiomatic, and help me spot any bugs:

```java
import java.util.ArrayList;
import java.util.List;

/**
 * A min-heap.
 * (assignment description omitted, unchanged from starter file)
 */
public class Heap {

    List<Integer> items = new ArrayList<>();

    public void swap(int indexOne , int indexTwo){
        int item = items.get(indexOne);
        items.set(indexOne, items.get(indexTwo));
        items.set(indexTwo, item);

    }
    public void addingValue( int item){
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
    public int getSize(){
        return items.size();
    }

    public boolean  checkIfEmpty(){
        return items.isEmpty();
    }

    public int popping(){
        if(items.isEmpty()) throw new IllegalStateException();
        int rootItem = items.get(0);
        int lastElement = items.get(items.size()-1);
        items.set(0, lastElement);
        items.remove(items.size() -1);

        int currentIdex = 0;


        while(true){
            int leftChild = 2 *currentIdex + 1;
            int rightChild = 2 *currentIdex + 2;

            if (leftChild >= items.size()) {
                break;
    }
            int smallerChild = leftChild;


            if (rightChild < items.size() && items.get(rightChild) < items.get(leftChild)) {
            smallerChild = rightChild;
        }
            if (items.get(currentIdex) <= items.get(smallerChild)) {
            break;
        }
            swap(currentIdex, smallerChild);
            currentIdex = smallerChild;
        }


        return rootItem;
    }
}
```

## 1. Assistant

*[Searched the project to check whether any other class uses `swap` or `items` directly. None did.]*

Your heap logic is correct. I traced the edge cases (adding to an empty heap, popping a heap with one element, popping down to empty, ties) and didn't find a crash or a wrong result. The real problems are about design and encapsulation, plus a couple of near-misses worth knowing about. I'll point at each issue and leave the fixes to you.

### Real problems (OOP / encapsulation)

**1. `items` isn't `private`.** With no access modifier it's package-private, so any class in the same package (`HeapRunner`, `HeapTest`) can call `heap.items.add(-999)` and break the heap ordering. The whole point of the class is to protect that ordering. Ask yourself: what modifier keeps outsiders out, and does this reference ever need to be reassigned? (If not, there's a second keyword for that.)

**2. `swap` is `public`.** That's the same problem through a different door: `heap.swap(0, 3)` corrupts the heap in one call. `swap` is an internal helper. Callers should only see the operations your Javadoc lists. Nothing else in your project calls it, so changing its visibility won't break anything.

### Near-misses: correct now, but fragile

**3. `parent = (newValueIndex - 1) / 2` when the index is 0.** That's `-1 / 2`. In Java this gives `0`, because integer division rounds toward zero, so `items.get(parent)` doesn't crash. You're relying on that by accident, though. In Python, `-1 // 2` is `-1`. Look at the order in your loop: you fetch `parentValue` before checking `newValueIndex > 0`. Could you rearrange it so you never compute a parent for the root?

**4. `items.remove(items.size() - 1)`.** This works because the argument is an `int`, so Java calls `remove(int index)`. `List<Integer>` also has `remove(Object o)`, which removes by value. If you ever pass an `Integer` there, you'll remove the wrong element with no error. It's a classic `List<Integer>` trap.

**5. Comparing boxed values.** `items.get(a) < items.get(b)` is fine because `<` and `<=` unbox to `int`. But `items.get(a) == items.get(b)` would compare object references, and it only appears to work for values between -128 and 127. You don't do this today. Just never compare two `get()` results with `==`.

### Idiomatic Java: naming and conventions

| Yours | What Java programmers expect | Why |
|---|---|---|
| `addingValue` | a plain verb (`add`, `insert`, `offer`) | Methods are imperative verbs. "-ing" reads like a status check. |
| `popping` | `pop` / `poll` / `remove` | Same reason. |
| `checkIfEmpty` | `isEmpty` | Boolean methods use `is…` / `has…`. Every Java collection uses `isEmpty()`. |
| `getSize` | `size` | `getSize` is fine, but the Collections API uses `size()`, and matching it makes your class feel familiar. |
| `currentIdex` | (typo) | |

**Exceptions:** `IllegalStateException` isn't wrong, but look at what `java.util.Queue` throws from `remove()` and `element()` when it's empty. That's the convention for "nothing to return." Whichever you choose, give it a message so the stack trace explains itself.

**Javadoc:** the class has a Javadoc comment, but the public methods don't. What does `peek()` do when the heap is empty? Document it with `@throws`.

### Readability (not required, but good practice)

- **Repeated index math.** You write `(i-1)/2`, `2*i+1` and `2*i+2` inline in several places. Small private helper methods named after what they compute would make both loops read like the textbook description.
- **`childValue` in `addingValue`.** The value moving up never changes, so you already know it before the loop starts. Do you need to re-fetch it every iteration?
- **Asymmetry.** `popping` contains the whole sift-down loop inline, and `addingValue` contains the sift-up loop inline. Many implementations pull each loop into its own private method. Then `add` and `pop` are each a few lines, and each loop can be read and tested on its own.
- **Formatting.** The braces in `popping` are misaligned, and the spacing is inconsistent (`boolean  checkIfEmpty`, `( int item)`, `indexOne ,`). Your IDE's auto-format (Shift+Option+F in VS Code) fixes all of that.

### Good choices you made

- You declared the field as `List<Integer>` and created it as `new ArrayList<>()`. Programming to the interface is idiomatic.
- `peek` and `popping` check for an empty heap before doing anything.
- `popping` breaks out of the loop when there's no left child and only compares the right child after checking it exists. That's where many students go wrong, and yours is right.
- Using `<=` in the sift-down stop condition means equal values don't cause pointless swaps.

If it helps, a good test to add to `HeapTest`: insert values in descending order, pop them all, and check they come out ascending. Then repeat with duplicates.

---

## 2. Student

> how is it now

*The student shared an updated `Heap.java`. Changes from version 1: `addingValue` → `insert`, `popping` → `poll`, `getSize` → `size`, `checkIfEmpty` → `isEmpty`, `currentIdex` → `currentIndex`. Everything else was unchanged.*

## 2. Assistant

*[Searched the project for calls to the old method names. `HeapRunner.java` and `HeapTest.java` still used them.]*

The renames are a real improvement. `insert`, `poll`, `size` and `isEmpty` match what Java's own collections use, and the `currentIndex` typo is fixed. `poll` is a good fit, since it's what `Queue` calls "remove the front."

**Your project doesn't compile right now.** `HeapRunner.java` and `HeapTest.java` still call the old names: `addingValue`, `popping`, `checkIfEmpty` and `getSize`. That's about 25 call sites. In VS Code, renaming with **F2** instead of retyping the name updates every caller at once.

**Still open from last time, most important first:**

1. **`items` still has no access modifier.** Any class in the package can reach in and change it. This is the biggest OOP issue left. Remember there are two keywords to think about.
2. **`swap` is still `public`.** That's the same problem through a method.
3. **The empty-heap exceptions still have no message.** Also compare your choice with what `Queue.remove()` throws. Since you named the method `poll`, there's a wrinkle: in `java.util.Queue`, `poll()` returns `null` on an empty queue instead of throwing. Your method returns `int`, so it can't return `null`. Either keep the name and document in Javadoc that it throws, or pick a name whose Java convention is to throw. Both are defensible, as long as it's a deliberate choice.
4. **`insert` still computes a parent for index 0**, which only works because of how Java rounds `-1 / 2`.
5. **The Javadoc on public methods, the inline index math, and the formatting** haven't changed. Auto-format (Shift+Option+F) fixes the braces and spacing in one keystroke.

Start by getting the callers compiling and running your tests. Then fix the two access modifiers. After that, the rest is polish.

---

## 3. Student

> how do all the names look now

## 3. Assistant

*[Read the current `Heap.java`, `HeapRunner.java` and `HeapTest.java`.]*

The method names look good, and `HeapRunner.java` is fully updated. There are still a few naming issues left:

**Still won't compile:** `HeapTest.java` lines 14 and 21 still call `heap.getSize()`.

**Test method names:** `testinserts` should be camelCase, like `testInserts`, to match `testNoValues` and `testPeek`. Better still, name each test after what it checks. That test checks that the size goes up after you insert, so a name like `testSizeAfterInserts` fits.

**Local variables in `Heap`:** this is the subtle one. Some variables hold an **index** and some hold a **value**, and the names don't always tell you which:

- `newValueIndex` and `currentIndex` clearly say they're indexes. Good.
- `parent`, `leftChild`, `rightChild` and `smallerChild` are also indexes, but they read like values. A reader could reasonably think `items.get(leftChild)` is a mistake, or that `smallerChild` holds the smaller number.
- `childValue` and `parentValue` clearly say they're values. Good.

Choose one convention, such as a suffix on every index variable, and apply it everywhere. In heap code, mixing up an index and a value is one of the most common bugs, and good names make that mistake much harder to make.

**Minor:** `item` in `swap` is a temporary variable, so `temp` describes its role better. `indexOne` and `indexTwo` are fine (`i` and `j` are also common for a swap). `rootItem` is fine.

Once you fix `getSize`, run the tests and you'll be back to a compiling project. After that, the two access modifiers from my first review are the most important thing left.

---

## 4. Student

> can i save this chat to submit it with my homework?

## 4. Assistant

Yes. Here are a few ways to do it:

1. **`/export`:** type `/export` in the chat box. It saves the conversation as a text file or copies it to your clipboard. This command definitely works in the Claude Code terminal. I'm not sure it's available in the VS Code extension panel, so if it doesn't show up there, use one of the options below.
2. **Copy and paste:** select the conversation in the panel and paste it into a document. It's simple, and you can check exactly what you're submitting.
3. **I can write it to a file for you:** for example, a Markdown file in the project with each of your questions and my answers in order. Since I'd be reconstructing it, read it over before you submit it.

Either way, check your instructor's AI-use policy for how they want help like this cited. Your submission is in good shape here: the record shows you asked me not to rewrite your code and that you made every change yourself.

Do you want me to create that file?

## 5. Student

> yes

## 5. Assistant

*[Created this file.]*
