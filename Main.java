import java.util.*;

class LRUCache {
    private static class Node {
        int key;
        int value;
        Node prev;
        Node next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Node head;
    private final Node tail;
    private int size;

    public LRUCache(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException(
                "Capacity cannot be negative."
            );
        }
        this.capacity = capacity;
        this.size = 0;
        this.cache = new HashMap<>();
        this.head = new Node(-1, -1);
        this.tail = new Node(-1, -1);

        head.next = tail;
        tail.prev = head;
    }

    //returns value if key exists or else it returns -1
    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return -1;
        }
        //to mark node as mru
        moveToHead(node);
        return node.value;
    }
    // inserts a new key-value pair or updates an existing key
    public void put(int key, int value) {
        //a zero-capacity cache cannot store any entries
        if (capacity == 0) {
            return;
        }
        Node existingNode = cache.get(key);
        //what if key already exists
        if (existingNode != null) {
            existingNode.value = value;
            //updating an entry also makes it most recently used
            moveToHead(existingNode);
           return;
        }
        //if key does not exist and cache is full
        if (size == capacity) {

            //node immediately befor ethe tail is lru
            Node lruNode = tail.prev;
            removeNode(lruNode);
            cache.remove(lruNode.key);
            size--;
        }
        //inserting a new entry
        Node newNode = new Node(key, value);
        cache.put(key, newNode);
        addAfterHead(newNode);
        size++;
    }

    // removes a node from its current position.
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    // inserts a node immediately after head.
    // the first real node is the most recently used.
    private void addAfterHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }
    // moves an existing node to the MRU position.
    private void moveToHead(Node node) {
        removeNode(node);
        addAfterHead(node);
    }

    // Returns the current number of stored entries.
    public int size() {
        return size;
    }
    // Returns the maximum number of entries allowed.
    public int getCapacity() {
        return capacity;
    }
    // Returns whether the cache contains the given key.
    // Useful when -1 is a valid stored value.
    public boolean containsKey(int key) {
        return cache.containsKey(key);
    } 
    // Returns the keys from most recently used to
    // least recently used. Useful for testing.
    public String getRecencyOrder() {
        StringBuilder result = new StringBuilder();
        Node current = head.next;
        while (current != tail) {
          if (result.length() > 0) {
                result.append(" -> ");
            }
            result.append(current.key);
            current = current.next;
        }
        return result.toString();
    }
}
public class Main {
    // Simple test helper that fails immediately if a
    // result differs from the expected value.
    private static void check(
        String description,
        boolean condition
    ) {
        if (!condition) {
            throw new AssertionError(
                "Test failed: " + description
            );
        }
        System.out.println("PASS: " + description);
    }
    public static void main(String[] args) {
        // Test 1: Basic insertion, lookup, and eviction.
        LRUCache cache = new LRUCache(3);
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);
        check(
            "Basic lookup",
            cache.get(1) == 100
        );
        // Key 1 becomes MRU.
        check(
            "Recency after get(1)",
            cache.getRecencyOrder().equals("1 -> 3 -> 2")
        );
        // Key 2 is now LRU and should be evicted.
        cache.put(4, 400);
        check(
            "Least recently used key is evicted",
            cache.get(2) == -1
        );
        check(
            "New key is stored",
            cache.get(4) == 400
        );
        check(
            "Cache does not exceed capacity",
            cache.size() == 3
        );
        // Test 2: Updating an existing key.
        LRUCache updateCache = new LRUCache(2);

        updateCache.put(1, 10);
        updateCache.put(2, 20);
        updateCache.put(1, 99);

        check(
            "Existing value is updated",
            updateCache.get(1) == 99
        );
        check(
            "Updating a key does not increase size",
            updateCache.size() == 2
        );
        check(
            "Updated key becomes MRU",
            updateCache.getRecencyOrder().equals("1 -> 2")
        );
        // Test 3: Capacity of one.
        LRUCache singleCache = new LRUCache(1);
        singleCache.put(1, 100);
        singleCache.put(2, 200);
        check(
            "Capacity one evicts the old entry",
            singleCache.get(1) == -1
        );
        check(
            "Capacity one retains the new entry",
            singleCache.get(2) == 200
        );
        // Test 4: Zero-capacity cache.
        LRUCache zeroCache = new LRUCache(0);
        zeroCache.put(1, 100);
        check(
            "Zero capacity stores no entries",
            zeroCache.size() == 0
                && zeroCache.get(1) == -1
        );
        // Test 5: Missing key does not change recency.
        LRUCache missingCache = new LRUCache(2);
        missingCache.put(1, 10);
        missingCache.put(2, 20);
        String before = missingCache.getRecencyOrder();
        int missingValue = missingCache.get(99);
        String after = missingCache.getRecencyOrder();
        check(
            "Missing key returns -1",
            missingValue == -1
        );
        check(
            "Missing lookup does not change recency",
            before.equals(after)
        );
        // Test 6: Negative keys and values.
        LRUCache negativeCache = new LRUCache(2);
        negativeCache.put(-5, -50);
        check(
            "Negative keys and values are supported",
            negativeCache.get(-5) == -50
        );
        // Test 7: A stored value of -1.
        LRUCache minusOneCache = new LRUCache(2);
        minusOneCache.put(7, -1);
        check(
            "Stored -1 is distinguishable using containsKey",
            minusOneCache.containsKey(7)
                && minusOneCache.get(7) == -1
        );
        // Test 8: Repeated access changes recency.
        LRUCache accessCache = new LRUCache(3);
        accessCache.put(1, 10);
        accessCache.put(2, 20);
        accessCache.put(3, 30);
        accessCache.get(1);
        accessCache.get(2);
        check(
            "Repeated accesses update recency correctly",
            accessCache.getRecencyOrder().equals("2 -> 1 -> 3")
        );
        // Test 9: Invalid negative capacity.
        boolean exceptionThrown = false;
        try {
            new LRUCache(-1);
        } catch (IllegalArgumentException exception) {
            exceptionThrown = true;
        }
        check(
            "Negative capacity is rejected",
            exceptionThrown
        );
        System.out.println("\nAll tests passed!");
    }
}
