package cp3project;

public class Shingler {

    // size of each shingle (k words together)
    private int k;

    // constructor to set initial k value
    public Shingler(int k) {
        this.k = k;
    }

    // change k later if needed
    public void setK(int k) {
        this.k = k;
    }

    // get current k value
    public int getK() {
        return k;
    }

    // create shingles (k-word sequences) from tokens
    public String[] generateShingles(String[] tokens) {
        int n = tokens.length;

        // if not enough words, return empty
        if (n < k) {
            return new String[0];
        }

        String[] shingles = new String[n - k + 1];

        // slide window of size k
        for (int i = 0; i <= n - k; i++) {
            shingles[i] = createShingle(tokens, i);
        }

        return shingles;
    }

    // build one shingle starting from a position
    private String createShingle(String[] tokens, int start) {
        StringBuilder shingle = new StringBuilder();

        // combine k words together
        for (int j = 0; j < k; j++) {
            shingle.append(tokens[start + j]);
            if (j != k - 1) {
                shingle.append(" "); // add space between words
            }
        }

        return shingle.toString();
    }

    // convert shingles into hash values using custom hash table
    public int[] generateHashedShingles(String[] tokens, HashTable hashTable) {
        String[] shingles = generateShingles(tokens);
        int[] hashed = new int[shingles.length];

        for (int i = 0; i < shingles.length; i++) {
            // 1. Use Java's hashCode or a custom rolling hash to get a unique integer
            int fingerprint = Math.abs(shingles[i].hashCode()); 
            
            // 2. Store it in your teammate's HashTable
            hashTable.insert(shingles[i], fingerprint);
            
            // 3. Assign it to your hashed array
            hashed[i] = fingerprint;
        }

        return hashed;
    }

    // print shingles (useful for testing)
    public void printShingles(String[] shingles) {
        for (String s : shingles) {
            System.out.println(s);
        }
    }

    // calculate similarity between two documents
    public double jaccardSimilarity(int[] doc1, int[] doc2) {

    HashTable set1 = new HashTable(1000);
    for (int hash : doc1) {
        set1.insert(String.valueOf(hash), hash);
    }
    
    HashTable set2 = new HashTable(1000);
    for (int hash : doc2) {
        set2.insert(String.valueOf(hash), hash);
    }
    
    // Get keys from set1 to iterate
    String[] keys1 = set1.getAllKeys();
    
    // Count intersections using HashTable's contains() method
    int intersection = 0;
    for (String key : keys1) {
        if (set2.contains(key)) {
            intersection++;
        }
    }
    
    // Union formula: |A| + |B| - |A∩B|
    int union = set1.uniqueCount() + set2.uniqueCount() - intersection;
    
    if (union == 0){ return 0.0;
    }
    return (double) intersection / union;
}
 }

