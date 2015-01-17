package huffman;

/**
 * A wrapper class that contains a character and its binary representation
 * @author blossom
 *
 */

public class Huffman {
	
	// the character
	protected char character;
	
	// the character's binary representation
	protected String binary = "0"; // will be set later this is just a holder
	
	// this chars length
	protected int length;
	
	/**
	 * The constructor that sets the character and its length
	 * @param character the character
	 * @param length the characters length
	 */
	public Huffman(char character, int length){
		this.character = character;
		this.length = length;
	}
	
	/**
	 * The constructor that sets the character and its binary representation
	 * @param character to represent
	 * @param binary the characters binary value
	 */
	public Huffman(char character, String binary){
		this.character = character;
		this.binary = binary;
	}
	
	public Huffman(char character, String binary, int length){
		this.character = character;
		this.binary = binary;
		this.length = length;
	}

}
