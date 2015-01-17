package huffman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * A class that will contain static methods for performing functions related to Huffman's Algorithm.
 * TODO: make the class static
 * 
 * @author Dan Blossom
 *
 */
public class HuffmanUtilities {
	
	/**
	 * Prevent construction since all methods will be static
	 */
	private HuffmanUtilities() {}
	
	/**
	 * Given a map, it will convert it to an array list of Huffman which is a wrapper class
	 * that contains a char, its length and its binary representation
	 * This will sort the tree in reverse order. The expected tree is bottom up which would 
	 * put the last character first, however here we put the last character last.
	 * Reason being - sanity while coding, I have adjusted for this throughout. 
	 * 
	 * @param unSortedMap the map to be sorted and converted to ArrayList<Huffman>
	 * @return a sorted ArrayList<Huffman> to perform Huffman Algorithms on
	 */
	protected static ArrayList<Huffman> sortInputMap(Map<Character, Integer> unSortedMap){
		
		// the array that we are going to fill and return
		ArrayList<Huffman> returnArray = new ArrayList<>();
		
		// first we need to fill the list
		for(Map.Entry<Character,Integer> map: unSortedMap.entrySet()){
			returnArray.add(new Huffman(map.getKey(), map.getValue()));
		}
		
		// pass our list into our sort-method and then return it!
		return sortHuffmanList(returnArray);
        
 /////////////////// WE HAVE FACTORED OUT SOME CODE HERE ///////////////////////////////
 /////////////////// IF OUR RETURN FAILS - COPY PASTE   ///////////////////////////////
 /////////////////// FROM OUR sortHuffmanList(list)    ////////////////////////////////
	}
	
	/**
	 * This will sort an array of our wrapper class <Huffman> this used
	 * to be contained within sortInputMap(Map<Character, Integer>) but
	 * seems we will need it for Encode as well as Decode, so rather then
	 * have two methods contain a sorting algorithm we factored it out
	 * 
	 * Based on our sorting rules:
	 *     Shorter lengths to right
	 *     Tie breaker is char smaller char to left
	 *     
	 * @params list<Huffman> our unsorted wrapper class
	 * @return list<Huffman> our sorted wrapper class    
	 */
	protected static ArrayList<Huffman> sortHuffmanList(ArrayList<Huffman> unSortedHuffman){

		// we will sort based on characters
        Collections.sort(unSortedHuffman, new Comparator<Huffman>(){

			@Override
			public int compare(Huffman one, Huffman two) {
				
				// first we need to know if the lengths are different
				if(one.length < two.length){
					return 1;
				}else if(one.length > two.length){
					return -1;
				}else{ // else we have a tie, break with char value
				    if(one.character < two.character){
				    	return -1;
				    }else if(one.character > two.character){
				    	return 1;
				    }
				}
				// of course if nothing else ...
				return 0;
			}
        });
        // well that is misleading since it is now sorted :)
        return unSortedHuffman;
	}
	
	/**
	 * This will pad a binary string with zeros. It appends the zero to the front
	 * so not sure if "pad left" is a better name or not?
	 * @param toPad the sting we want to pad
	 * @param length how long the string should be
	 * @return the string padded to the length desired OR the original string if equal or less than length
	 */
	protected static String padRight(String toPad, int length){
		
		// simple loop that puts 0 in front of the incoming string
		for(int i = toPad.length(); i < length; i ++){
			toPad = "0" + toPad;
		}
		// the string to return
		return toPad;
		
	}
	
	/**
	 * Given List<Huffman> it will canonicalize that list. This is based off the Huffman Algorithm
	 * if the List<Huffman> is sorted according to these rules but reversed: 
	 *        - Longer codes will be to the left of the shorter codes
	 *        - Matching codes the character will decided with a coming before b
	 *        
	 * Our incoming list must meet the following criteria based off the sortInputMap(Map<Character, Integer>) method
	 * provided within this class. That sorts the Huffman Tree in such a way that bottom up is reversed IE:
	 *        - Given sample0.huf with equal lengths and input null, a, b, c
	 *        - A bottom up approach would sort this list c, b, a, null however we our bottom up is actually top-down
	 *        - I did this for readability (and sanity) while sorting and creating the Canonical Huffman code here.
	 *        
	 * TODO: maybe use the node class and put those into a list rather than a separate class Huffman.
	 *        
	 * @param list the list to canonicalize
	 * @return a canonicalized list of Huffman codes
	 */
	protected static List<Huffman> canonicalize(List<Huffman> list){
		
		//  our starting point: 0
		int number = 0;
		// loop through our list assigning binary values to characters
		for(int i = 0; i < list.size(); i++){
			// the binaryString to assign
			String binaryString = Integer.toBinaryString(number);
			// the length of this char (IE: how long binary)
			int length = list.get(i).length;
			// pad the binary string with 0's as needed to get the true number
			binaryString = HuffmanUtilities.padRight(binaryString, length);
			// assign the binary value
			list.get(i).binary = binaryString;
			
			// first we need to ensure the next loop will happen
			// if so, we can shift (or add) to the binary number
			// TODO: I do not like this check -- must be a better way but I keep throwing nulls because
			//       I am checking the next element which could not exist.
			if(i + 1 < list.size()){
				int nextLen = list.get(i + 1).length;
				number = (number + 1) >> (length - nextLen);
			}
		}
		// finally return the canonicalized list
		return list;
	}
	
	/**
	 * This creates a look-up map from our List<Huffman> which could also be looked at like our list of nodes.
	 * Should probably just use nodes rather than Huffman, however we never actually build a tree Per Se.
	 * Anyway, given the binary representation to create a key, we assign that a code. Using a lookup map is faster.
	 * 
	 * @param list to be placed in a look-up map.
	 * @return A map to be used to look up codes
	 */
	protected static Map<String, Character> lookupMap(List<Huffman> list){
		// the map we are going to return with the binary ==> code 
		Map<String, Character> returnMap = new HashMap<>();
		
		// simply loop through the list putting each element into the map
		for(Huffman huffman: list){
			returnMap.put(huffman.binary, huffman.character);
		}
		
		// return the map
		return returnMap;
	}
	
	/**
	 * This creates an encoding map to provide a key value store of Map<Char, String>
	 * @param ArrayList<Huffman> the wrapper class to convert to a lookupMap for encodings
	 */
	protected static Map<Character, String> lookupMapEncodings(List<Huffman> list){
		// the map we are going to return with the binary ==> code 
		Map<Character, String> returnMap = new HashMap<>();
		
		// simply loop through the list putting each element into the map
		for(Huffman huffman: list){
			returnMap.put(huffman.character, huffman.binary);
		}
		
		// return the map
		return returnMap;
	}
	
	/**
	 * This method will create a huffman tree (not canonized).
	 * This is based off the algorithm found on page 431 of Intro to Algorithms by Cormen, Leiserson, Rivest, Stein
	 * 
	 * Direct quote:
	 * 
	 * The algorithm uses a min-priority queue Q, keyed on the freq attribute, to identify the two least-frequent objects to
     * merge together. When we merge two objects, the result is a new object whose frequency is the sum of the frequencies 
     * of the two objects that were merged.
     * 
     * @param input - the map we created while reading the file which contains our frequencies and characters
     * @return a root node to a huffman tree that has yet to be canonized.
	 */
	protected static Node huffmanTree(Map<Character, Integer> input){
		
		// first let us put our input map into a list
		ArrayList<Node> list = new ArrayList<>();
		// iterate through the map creating Nodes and adding them to our list
		for(Map.Entry<Character, Integer> entry: input.entrySet()){
			list.add(new Node(entry.getKey(), entry.getValue()));
		}
		// now we follow the given althorithm from page 431
		// we use a priority queue == "min-priority queue Q"
		PriorityQueue<Node> pQueue = new PriorityQueue<>(list);
		// for i = 1 to n - 1 (n being our list of nodes)
		for(int i = 1; i < list.size(); i++){
			// allocate new node z
			Node z = new Node();
			// z.left = x = Extract-Min(q);
			Node x = pQueue.poll();
			z.left = x;
			// z.right = y = Extraxt-Min(q);
			Node y = pQueue.poll();
			z.right = y;
			//  z.frequency = x.frequency + y.frequency
			z.frequency = x.frequency + y.frequency;

			// represent that it is interior node.
			// setting it in nodes constructor did not work here. ugh.
			z.character = (char) 0x01;
			
			//Insert(q, z)
			pQueue.add(z);
		}
		// return Extract-Min(q) // the room
		return pQueue.poll();
	}
	
	/**
	 * Performs an in-order walk on a huffman tree given a root and the list
	 * of huffmans to build
	 * @param root - the root node of a huffman tree
	 * @param binary - the binary representation we are build
	 * @param list - the list to build our huffman wrapper class with 
	 */
	protected static void inorder(Node node, String binary, ArrayList<Huffman> huffmanWrapperList){
		// our base case, we passed a null node
		if(node == null){
			return;
		}
		
		// for our lefty nodes
		inorder(node.left, (binary + "0"), huffmanWrapperList);
		
		// are we an interior node? if not add it to the list
		if(node.character != (char) 0x01){
			huffmanWrapperList.add(new Huffman(node.character, binary, binary.length()));
		}
		
		// for our righty nodes
		inorder(node.right, (binary + "1"), huffmanWrapperList);
	}
	
	/**
	 * This will write a file and decode at the same time. Given a lookup map, a list of binary codes to
	 * decode and the file to write, it will search each binary bit and determine if a code exists, if not
	 * it will increment a counter and try a longer piece of the binary byte until it finds a code. It will
	 * then chop and continue. Since we are in 8 bit chunks but the code could be across two, we do preserve
	 * any endings and add it to the next incoming binary byte. Simple example:
	 * 
	 *     - Given 01101100 in sample0.huf to tell us we have a, b, c and null to write to the file.
	 *     - Given a look up map that contains a=01, b=10, c=11, null=00
	 *       - We would start at 0 and check the map to determine there is no result.
	 *       - We would then inc and try 01 to determine there is a key and write 'a' to the file
	 *       - We would then substring and start decoding with 101100
	 *       - Reset our counter we try 1 to find no keys
	 *       - Inc our counter we try 10 to find 'b' and write that to file
	 *       - Reset our counter and trim our binary string to return 1100
	 *       - The process continues to get c then the null terminator
	 *       
	 *      - If we had remaining bits but no more look ups, it would append that string to the front of the next
	 *        incoming binary byte.
	 *        
	 * @param map The lookup map needed to determine the codes.
	 * @param bits The bits of binary needed to decode.
	 * @param file The file to write-out too.
	 */
 	protected static void writeFile(Map<String, Character> map, List<String> bits, String file){
		
		try{
			// First we need to create an output stream.
			FileOutputStream outFile = new FileOutputStream(new File(file));
		    
			// our pointer of where we are in the binary string
		    int pointer = 1;
		    // our tested set of bits
            String testingBits = "";
            
            // outer loop goes through each binary byte
		    for(String bit: bits){
		    	
		    	// append any "left-over" bits to our new incoming bits
		    	bit = testingBits + bit;
			
		    	// inner loop tests the bits and writes the chars to the file when
		    	// we find matches
			    while(true){
			    	
			    	try{
			    		// first we assign the bits to test
			    		testingBits = bit.substring(0, pointer);
			    		
			    		// do we have a key that matches out bits ?
			    		if(map.containsKey(testingBits)){
			    	        
			    			// is that our end of file bit string ? If so .. bail!
			    			if(map.get(testingBits) == '\u0000')
			    		        break;
			    			// if not, we need to write that output file
			    			outFile.write(map.get(testingBits));
			    			// we need to trim our bit and keep going...could be more codes in them bits
			    			bit = bit.substring(pointer);
			    			// just in case we need to hold onto bit - will be overwritten if not.
			    			testingBits = bit;
			    			// reset our pointer
				            pointer = 1;
			    		}else{
			        	// no match, just in our pointer 
				        pointer++;
			    		}	
			    	}catch(IndexOutOfBoundsException ex){
			    		// yep, I hate every last bit of this
			    		break;
			    	}
			    }
		    }
		    // close the stream
		    outFile.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    
 	/**
 	 * This will write an encoded file
 	 */
 	protected static void writeEncodedFile(ArrayList<Huffman> huffList, String outFile, String inFile){
 		// first let us create a lookupMap for faster time
 		Map<Character, String> lookup = HuffmanUtilities.lookupMapEncodings(huffList);
 		
 		try{
 			// create a stream for our output file
 			FileOutputStream fileWriter = new FileOutputStream(new File(outFile));
 			
 			// first write the length of our alphabet
 			String binLen = HuffmanUtilities.padRight(Integer.toBinaryString(huffList.size()), 8);
 			fileWriter.write(Integer.parseInt(binLen, 2));
 			
 			// now we continue through the alphabet adding charcodes and lengths
 			for(int i = 0; i < huffList.size(); i++){
 				String binaryChar = HuffmanUtilities.padRight(Integer.toBinaryString(huffList.get(i).character), 8);
 				String binaryLen = HuffmanUtilities.padRight(Integer.toBinaryString(huffList.get(i).length), 8);
 				fileWriter.write(Integer.parseInt(binaryChar, 2));
 				fileWriter.write(Integer.parseInt(binaryLen, 2));
 			}
 			
 			// now we have to go back over our file converting it to 8-bit binary encodings
 			try(BufferedReader buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "UTF-8"))){
 			    // our string to build we will write
 				String eightBit = "";
 				// as previously we are just reading the input file
 				int activeByte;
 				while((activeByte = buffReader.read()) != -1){
 					// so let us look up and get the binary of the char
 					eightBit = eightBit + lookup.get((char) activeByte);
 					// if (or once) the binary is greater than eight
 					if(eightBit.length() > 8){
 						// trim it to 8 (not this might leave some remaining bits
 		 		        String write = eightBit.substring(0, 8);
 		 		        // write it
 		 				fileWriter.write(Integer.parseInt(write,2));
 		 				// and trim and return our string
 		 				eightBit = eightBit.substring(8);
 		 			}
 				}
 				// now we pad any bits that are remaining plus add the end of file.
 				eightBit = eightBit + lookup.get((char) 0x00);
 				
 				// are we in 8 bit chunks? (keeping in mind we might be over 8 at this point)
 				while(eightBit.length() % 8 != 0){
 				    eightBit = eightBit + "0";		
 				}
 				
 				// now we can add the ending part to our file
 				while(eightBit.length() > 0){
 					// again grabbing 8 bit chunks
 					String write = eightBit.substring(0, 8);
 					// writing it
 					fileWriter.write(Integer.parseInt(write,2));
 					// trimming for next round
 					eightBit = eightBit.substring(8);
 				}
 			}
 			fileWriter.close();
 		}catch(IOException e){
 			e.printStackTrace();
 		}
 	}
}