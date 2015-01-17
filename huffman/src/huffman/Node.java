package huffman;

public class Node implements Comparable<Node> {
	
	// pointer to left node
	protected Node left = null;
	// pointer to right node
	protected Node right = null;
	// the frequency of this character
	protected int frequency;
	// this character
	protected char character;
	
	/**
	 * Empty constructor ... mainly for our "Z" when building the tree.
	 */
	public Node() {}
	
	/**
	 * Creates a node from a given character and frequency
	 * Sets left and right to null
	 * 
	 * @param character the character
	 * @param frequency the frequency
	 */
	public Node(char character, int frequency){
		this.character = character;
		this.frequency = frequency;
	}
	
	/**
	 * Create an interior node, do not set character
	 * @param frequency
	 */
	public Node(int frequency){;
		this.frequency = frequency;
		// we cannot use null since it is our EOF so try "SOH" or start of heading.
		// also setting here does not work ... (hmm)!
		this.character = (char) 0x01;
	}
	
	/**
	 * Create a comparator
	 */
	@Override
	public int compareTo(Node node){
		if(this.frequency < node.frequency){
			return -1;
		}
		if(this.frequency > node.frequency){
			return 1;
		}
		return 0;
	}
}