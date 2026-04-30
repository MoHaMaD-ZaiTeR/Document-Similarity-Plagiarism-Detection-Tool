package cp3project;
import java.io.File;//helps with working on file
import java.util.*;

public class Reader {

	public static List<String[]> read_docs(String path_of_folder){ 
		List<String[]> document=new ArrayList<>();// create arralist to store final words with the number of document
		File big_folder = new File(path_of_folder);//represent the big_file that are inside it the txt docs

		if (!big_folder.exists()) {
			System.out.println("Error: Folder does not exist - " + path_of_folder);
			return document;//see if the file exists by referencing to the path
		}
		File[] allFiles = big_folder.listFiles();//return array of the files in the big_file
		List<File> txt_Files = new ArrayList<>();// create arraylist to store just txt files

		if (allFiles == null) {
			System.out.println("Error file not readed");
			return document;
		}

		for (File file : allFiles) {//go for files in the big_file
			if (file.getName().endsWith(".txt")) {//if file endwith txt continue
				txt_Files.add(file);//put every file we have it txt in  the arraylist
			}
		}
		for (File txt_file : txt_Files) {//go for every file in array list
			String content;//content of every file
			try {
				content = new String(java.nio.file.Files.readAllBytes(txt_file.toPath()));// read every file and make them one string to be able to work on them(spliting,lowercase,removing what do should ignore)
			} catch (Exception e) {
				System.out.println("  Error reading file: " + e.getMessage());//if there is an error give it and continue
				continue;
			}
			String lowercased = content.toLowerCase();//lowercase every word in a file
			String preprocessing = lowercased.replaceAll("[^a-z]", " ");//replace everything that we lowercased into just spaces and words from a to z 
			//"[^a-z]", " "  replace every character that is not a letter (a–z) with a space
			String[] final_words = preprocessing.trim().split("\\s+");//split the code on every space,tab,new line \\s+ to split any space big or small 2 or more
			Set<String> stopwords = new HashSet<>(Arrays.asList("the", "and", "in", "of", "to", "a", "is", "for", "on", "with"));
			//put this words as a list 
			List<String> filtered = new ArrayList<>();//create new aaray lsit
			for (String word : final_words) { //for words in our final output that is cleaned 
				if (!stopwords.contains(word)) {//is the the word not from the ignored list add it to the filtered
					filtered.add(word);
				}
			}
			String[] filteredWords = filtered.toArray(new String[0]);//coverting list filtered to array,we did not make it from the begining as array since we dont know how many words we have
			document.add(filteredWords);//adding the words to the array list document
		}


		return document;
		
	}
	
}

//to continue with second role call this line:
//List<String[]> all_Documents = Reader.read_docs("path/to/folder");

//total complexity=O(n*l) where n is the number of documents and l=characters of all docs/n
