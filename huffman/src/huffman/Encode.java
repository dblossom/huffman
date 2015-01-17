package huffman;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to encode a file using Huffman's Algorithm
 * 
 * @author Dan Blossom
 */
public class Encode {
	
	private String source;
	private String destination;
	
	/**
	 * Constructor that sets the source and destination files
	 * 
	 * @param source the file to encode
	 * @param destination the encoded file
	 */
	public Encode(String source, String destination){
		this.source = source;
		this.destination = destination;
	}
	
	public static void main(String args[]){
		// create an object
		Encode encode = new Encode(args[0], args[1]);
		// call our "wrapper method" that encapsulates all the gross pieces
		encode.encode();
		//  yep say we are done.
		System.out.println("DONE");
	}
	
	/**
	 * A method that runs all the pieces to make the plain text file be encoded
	 */
	public void encode(){
		// first we need to create a frequency map
		Map<Character, Integer> frequencyMap = this.readInputMap(this.source);
		// now we need to build our tree
		Node root = HuffmanUtilities.huffmanTree(frequencyMap);
		// we are going to build our tree in an array list given our wrapper class <Huffman>
		ArrayList<Huffman> huffmanWrapper = new ArrayList<>();
		// we are going to walk the tree putting the nodes into our wrapper ArrayList with Binary
		HuffmanUtilities.inorder(root, "", huffmanWrapper);
		// for easy lookups it would be best if we sorted the list
		HuffmanUtilities.sortHuffmanList(huffmanWrapper);
		// now we are going to canonicalize the huffman codes
		HuffmanUtilities.canonicalize(huffmanWrapper);
		// finally we are going to write the file.
		HuffmanUtilities.writeEncodedFile(huffmanWrapper, this.destination, this.source);
	}
	
	/**
	 * This will read over the file adding characters to a map if the
	 * character already exists, it will increase the frequency.
	 * 
	 * @param source the source file to read
	 * @return the source file converted to a frequency map
	 */
	private Map<Character, Integer> readInputMap(String source){
		// first create our return map
		Map<Character, Integer> returnFrequencyMap = new HashMap<>();
		// add the EOF character and its frequency ... better only be one.
		returnFrequencyMap.put((char)0x00,1);
		
		// Java 7 changes how to close the streams by allowing auto-close.
		// We pass the inputstreamreader here to utilize the character-set constructor
		// UTF-8: Eight-bit UCS Trasformation Format (https:docs.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html)
		try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(source), "UTF-8"))){
            
			// so we loop over the entire file adding our chars to the map
			int current;
			while((current = bufferedReader.read()) != -1){
				// first convert the int to char ... might be smarter to use Character class here.
				char readChar = (char) current;
				// does the character already exist ? if so inc the frequency
				if(returnFrequencyMap.containsKey(readChar)){
					returnFrequencyMap.put(readChar, (returnFrequencyMap.get(readChar) + 1));
			    // no? let us add it with frequency of one.
				}else{
					returnFrequencyMap.put(readChar, 1);
				}
			}
	    // file does not exist
		} catch (FileNotFoundException e) {
            System.out.println("File does not exist...");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Error has occured...");
			e.printStackTrace();
		}
		// and we finally return the map.
		return returnFrequencyMap;
	}
}
