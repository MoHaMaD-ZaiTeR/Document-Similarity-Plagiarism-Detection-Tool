package cp3project;

public class ResultBST {
    // Standard Node structure for BST
    class Node {
        Merging.DocPair data;
        Node left, right;

        Node(Merging.DocPair d) {
            this.data = d;
            this.left = this.right = null;
        }
    }

    private Node root;

    // Insert a pair based on its similarity score
    public void insert(Merging.DocPair pair) {
        root = insertRec(root, pair);
    }

    private Node insertRec(Node root, Merging.DocPair pair) {
        if (root == null) {
            return new Node(pair);
        }
        // If current score is less than root, go left. Otherwise, go right.
        if (pair.score < root.data.score) {
            root.left = insertRec(root.left, pair);
        } else {
            root.right = insertRec(root.right, pair);
        }
        return root;
    }

    // In-order traversal modified to print Right-Root-Left (Descending Order)
    public void printRankedReport() {
        System.out.println("\n--- BST High-Similarity Ordered Report ---");
        printDescending(root);
    }

    private void printDescending(Node node) {
        if (node != null) {
            printDescending(node.right);
            System.out.println(node.data.toString());
            printDescending(node.left);
        }
    }
}
