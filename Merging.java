package cp3project;

import java.util.*;

public class Merging {
	//storing two docs and the similarity score
    public static class DocPair {
        int docId1;
        int docId2;
        double score;

        public DocPair(int id1, int id2, double score) {
            this.docId1 = id1;
            this.docId2 = id2;
            this.score = score;
        }

        @Override
        public String toString() {
            String label = score > 0.7 ? "High Similarity" : (score > 0.3 ? "Moderate Similairty" : "Low Similarity");
            return "Doc " + docId1 + " vs Doc " + docId2 + " -> " + String.format("%.2f", score) + " [" + label + "]";
        }
    }

    //calculate pairs using :DocPair from above,Shingler from classmate
    public List<DocPair> compareAll(List<int[]> allHashedDocs, Shingler shingler) { 
        List<DocPair> results = new ArrayList<>();
        
        //comparison in O(n2), n number of docs
        for (int i = 0; i < allHashedDocs.size(); i++) {
            for (int j = i + 1; j < allHashedDocs.size(); j++) {
                double score = shingler.jaccardSimilarity(allHashedDocs.get(i), allHashedDocs.get(j));
                results.add(new DocPair(i, j, score));
            }
        }
        return results;
    }

    
    //mergeSort helper
    private void merge(DocPair[] arr, int l, int m, int r) {
    	int n1 =m-l+1;
    	int n2=r-m;
    	
        DocPair[] L = new DocPair[n1];
        DocPair[] R = new DocPair[n2];
        for(int i=0;i<n1;i++) {
        	L[i]=arr[l+i];
        }
        for(int i = 0;i<n2;i++) {
        	R[i]=arr[m+1+i];
        }
        int  i = 0;
        int j=0;
        int k=l;
        while(i<n1&&j<n2) {
        	if(L[i].score >=R[j].score) { //high to low (descending order)
        		arr[k]=L[i];
        		i++;
        	}else {
        		arr[k]=R[j];
        		j++;
        	}
        	k++;
        		
        }

        while(i<n1) {
        	arr[k]=L[i];
        	i++;k++;
        }
        while(j<n2) {
        	arr[k]=R[j];
        	j++;k++;
        }
    }
    //mergeSort
    public void mergeSort(DocPair[] arr,int l,int r) {
    	if(l<r) {
    		int m = l+(r-l)/2;
    		mergeSort(arr,l,m);
    		mergeSort(arr,m+1,r);
    		merge(arr,l,m,r);
    	}
    }
}
