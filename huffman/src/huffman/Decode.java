package huffman;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to decode a file using Huffman's Algorithm
 * 
 * @author Dan Blossom
 */

public class Decode {
	
	// the input file to decode
	private String input;
	
	// the output file after decoded
	private String output;
	
	// the number of characters in input file
	private int characterLength;
	
	// The binary bits to be decoded
	private List<String> binaryStrings = new ArrayList<>();
	
	protected static boolean EOF;
	
	/**
	 * The constructor, just sets the input and output file names.
	 * @param input
	 * @param output
	 */
	public Decode(String input, String output){
		this.input = input;
		this.output = output;
	}
	
	public static void main(String[] args){
		// create our object
		Decode decode = new Decode(args[0], args[1]);
		// call our method that does the decoding
		decode.decode();
		// yep we are done.
		System.out.println("DONE");
	}
	
	/**
	 * A simple method that puts all the pieces together in one location
	 * It will decode an encrypted huffman file.
	 */
	public void decode(){
		// a map that will contain chars and their values after the input is read
		Map<Character, Integer> inputMap = this.readInputFile(this.input);
		// we put those into a list for easy sorting by overriding the compare method
		List<Huffman> huffmanList = HuffmanUtilities.sortInputMap(inputMap);
		// after that we can now canonicalize the list and create a lookup map
		huffmanList = HuffmanUtilities.canonicalize(huffmanList);
		// a lookup map for quick lookup
		Map<String, Character> lookup = HuffmanUtilities.lookupMap(huffmanList);
		// givin the lookupmap, the array of binarystrings to decode (done during readInputFile)
		// and an output file, decode the file.
		HuffmanUtilities.writeFile(lookup, this.binaryStrings, this.output);
	}
	
	/**
	 * Given an input file it will create a map of which the key will be a character length
	 * And the value will be a list of characters with that length
	 * @param inputFile
	 * @return
	 */
	private HashMap<Character, Integer> readInputFile(String inputFile){
		
		// the map to return with the read file
		HashMap<Character, Integer> returnMap = new HashMap<>();
		
		try{
			// to handle the file stream
		    FileInputStream fileIn = new FileInputStream(inputFile);
		    DataInputStream dataIn = new DataInputStream(fileIn);
		    
		    // first byte tells us the number of characters to decode.
		    this.setFileLength(dataIn);
		    
		    // now let us fill our map with the characters
		    for(int i = 0; i < this.characterLength; i++){
		    	
		    	// first byte is the char
		    	char cValue = (char) dataIn.read();
		    	// second byte is the length
		    	int length = dataIn.read();  	
		    	// finally put this in the map.
		    	returnMap.put(cValue, length);
		    }
		    
		    // now let us fill the binary array that keeps the decoding binary string
		    int data;
		    
	        while((data = dataIn.read()) != -1){
	        	String paddedBitString = HuffmanUtilities.padRight(Integer.toBinaryString(data), 8);
	        	binaryStrings.add(paddedBitString);
	        }
		}catch(Exception exception){	
			if(exception instanceof EOFException){
				EOF = true;
			}else{
				System.out.println("Error: " + exception);
			}
		}
		return returnMap;
	}
	
	/**
	 * Sets the number of characters in the input file.
	 * TODO: Not fully happy with this yet ... 
	 * @param ds the datastream to read from
	 * @throws IOException if data scream is null
	 */
	private void setFileLength(DataInputStream ds) throws IOException{
		this.characterLength = ds.readByte();
	}	
}