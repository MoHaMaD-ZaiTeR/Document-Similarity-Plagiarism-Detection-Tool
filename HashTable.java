package cp3project;

public class HashTable {
    
    // Constructor: Initializes the array with empty slots

     public HashTable(int size) {
        this.size = size;
        this.table = new Node[size];
        this.count = 0; 
    }
    // Each slot in the table holds a linked list (chain) using this Node class
    private class Node {
        String key;
        int value;
        Node next;

        Node(String key, int value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private Node[] table;
    private int size;  // Total number of slots in the array (m)
    private int count; // Total number of items currently stored (n)

    

    // Hash Function: Converts a string key into a valid array index
    public int hash(String key) {
        int h = 0;
        for (int i = 0; i < key.length(); i++) {
            // Polynomial rolling hash: multiply by 31 and add character value
            h = (h * 31 + key.charAt(i)) % size;
        }
        // Math.abs ensures the result is not negative
        return Math.abs(h);
    }

    // Insert: Adds a new key-value pair or updates an existing one
    public void insert(String key, int value) {
        int index = hash(key); 
        Node current = table[index];

        // Search the chain to check if the key is already present
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value; // Update value (e.g., student grade or word count)
                return;
            }
            current = current.next;
        }

        // Key not found: Create a new node and add it to the front of the chain
        Node newNode = new Node(key, value);
        newNode.next = table[index];
        table[index] = newNode;
        count++; 
    }

    // Search: Finds a key and returns its value, or null if not found
    public Integer search(String key) {
        int idx = hash(key); 
        Node current = table[idx];

        // Walk through the linked list at the calculated index
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value; 
            }
            current = current.next;
        }
        return null; // key not found
    }

    // Delete: Removes a key-value pair from the table
    public boolean delete(String key) {
        int index = hash(key); 
        Node current = table[index];
        Node prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    // Target is the first node in the chain
                    table[index] = current.next; 
                } 
                else {
                    // Target is in the middle or end: skip the current node
                    prev.next = current.next; 
                }
                count--; 
                return true;
            }
            prev = current; 
            current = current.next;
        }
        return false; 
    }
    // Load Factor: Calculates how full the table is (n/m)
    public double loadFactor() {
        return (double) count / size;
    }

    // Returns true if the key exists in the table
public boolean contains(String key) {
    int idx = hash(key);
    Node current = table[idx];
    while (current != null) {
        if (current.key.equals(key)){
        return true;
        }
        current = current.next;
    }
    return false;
}
// Get all unique keys currently stored in the hash table
public String[] getAllKeys() {
    String[] keys = new String[count];  // Array of exact size
    int index = 0;
    
    // Loop through every bucket in the table
    for (int i = 0; i < size; i++) {
        Node current = table[i];
        // Traverse the linked list in this bucket
        while (current != null) {
            keys[index++] = current.key;  // Add key to array
            current = current.next;
        }
    }
    return keys;
}
// Return the number of unique keys stored
public int uniqueCount() {
    return count;  // count already tracks unique keys
}

// Returns the value/count for a key, or 0 if not found
public int get(String key) {
    Integer result = search(key);
   if (result == null) {
            return 0; // Return zero if word isn't found
        } 
        else {
            return result; // Return the actual count
        }
}

}
