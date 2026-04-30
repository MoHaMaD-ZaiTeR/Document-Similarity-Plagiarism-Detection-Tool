package cp3project;
import java.io.File;
import java.util.*;

public class PlagiarismDemo {
    
    public static void main(String[] args) {
        // Create a Scanner to read user input from the console
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Plagiarism Detection System ===");
        System.out.print("Please enter the full folder path of your documents: ");
        
        // Read the line typed by the user
        String folderPath = scanner.nextLine();
        
        System.out.println("\nReading documents from: " + folderPath);
        System.out.println();
        
        // ========== STEP 1: Read and Preprocess ==========
        List<String[]> allTokens = Reader.read_docs(folderPath);
        
        if (allTokens.isEmpty()) {
            System.out.println("ERROR: No documents found or path is invalid.");
            scanner.close();
            return;
        }
        
        // Get document names for the final report
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        List<String> docNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".txt")) {
                    docNames.add(file.getName().replace(".txt", ""));
                }
            }
        }
        
        // ========== STEP 2: Shingling and Hashing ==========
        int k = 3;
        Shingler shingler = new Shingler(k);
        HashTable hashTable = new HashTable(1000);
        
        List<int[]> allHashedDocs = new ArrayList<>();
        
        for (String[] tokens : allTokens) {
            int[] hashedShingles = shingler.generateHashedShingles(tokens, hashTable);
            allHashedDocs.add(hashedShingles);
        }
        
        // ========== STEP 3: Pairwise Similarity ==========
        Merging merger = new Merging();
        List<Merging.DocPair> results = merger.compareAll(allHashedDocs, shingler);
        
        // ========== STEP 4: Merge Sort ==========
        Merging.DocPair[] resultArray = results.toArray(new Merging.DocPair[0]);
        merger.mergeSort(resultArray, 0, resultArray.length - 1);
        
        // Display Results
        System.out.println("Similarity Results (Highest to Lowest):");
        System.out.println("========================================");

        for (Merging.DocPair pair : resultArray) {
            String doc1Name = docNames.get(pair.docId1);
            String doc2Name = docNames.get(pair.docId2);
            
            String label = pair.score > 0.7 ? "HIGH" : (pair.score > 0.3 ? "MODERATE" : "LOW");
            
            System.out.println(doc1Name + " vs " + doc2Name + " similarity result = " 
                               + String.format("%.2f", pair.score) + " [" + label + "]");
        }
        
        // Print TOP 5 most similar pairs
        System.out.println("\n========== TOP 5 MOST SIMILAR PAIRS ==========");
        int topCount = Math.min(5, resultArray.length);
        for (int i = 0; i < topCount; i++) {
            Merging.DocPair pair = resultArray[i];
            String doc1Name = docNames.get(pair.docId1);
            String doc2Name = docNames.get(pair.docId2);
            String label = pair.score > 0.7 ? "HIGH" : (pair.score > 0.3 ? "MODERATE" : "LOW");
            System.out.println((i+1) + ". " + doc1Name + " vs " + doc2Name + " = " 
                               + String.format("%.2f", pair.score) + " [" + label + "]");
        }
        System.out.println("=============================================");
        
        // Save results to file for Python visualization
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter("similarity_results.txt");
            writer.println("doc1,doc2,score");
            for (Merging.DocPair pair : resultArray) {
                String doc1Name = docNames.get(pair.docId1);
                String doc2Name = docNames.get(pair.docId2);
                writer.println(doc1Name + "," + doc2Name + "," + pair.score);
            }
            writer.close();
            System.out.println("\nResults saved to similarity_results.txt");
        } catch (Exception e) {
            System.out.println("Could not save results file");
        }
        
        scanner.close();
    }
}
