/*
 * Title: HuffmanTree
 * Author: Matthew Boyette
 * Date: 6/5/2013
 * 
 * This is a special type of binary tree that implements a Huffman Code data compression routine.
 */

package api.util.datastructures;

import java.io.File;
import java.util.PriorityQueue;
import java.util.Scanner;

import api.util.Support;

/*
 * Although we were only concerned with characters A-G for this assignment, I designed the
 * program to be usable with other contiguous subsets of the alphabet. The alphabetString
 * field is where this is controlled. The string MUST be in ascending order by the integer
 * value of each character according to the ASCII table and there cannot be any gaps. I've
 * built a method to do this automatically thus avoiding tedious human typo errors.
 * 
 * ALPHABET_ASSIGNMENT_5 is the 'A'-'G' alphabet specified by the instructions on Blackboard.
 * 
 * ALPHABET_ASCII allows the processing of spaces and all printable ASCII characters. I used
 * it to test my program with the example data given in the book on pages 416-421.
 */

public class HuffmanTree<T> extends Tree<T>
{
	public static enum DataFormats
	{
		COMPRESSED, ORIGINAL, UNCOMPRESSED
	}
	
	public static final String	ALPHABET_ASCII			= Support.constructAlphabetString(32, 126);
	public static final String	ALPHABET_ASSIGNMENT_5	= Support.constructAlphabetString('A', 'G');
	private Character			alphabetFirst			= null;
	private int					alphabetSize			= 0;
	private String				alphabetString			= null;
	private String[]			codeTable				= new String[this.getAlphabetSize()];
	private int[]				frequencies				= new int[this.getAlphabetSize()];
	private String				inputString				= "";
	
	public HuffmanTree()
	{
		this(HuffmanTree.ALPHABET_ASCII);
	}
	
	public HuffmanTree(final String alphabet)
	{
		super();
		this.reset(alphabet);
	}
	
	@SafeVarargs
	public HuffmanTree(final String alphabet, final T... args)
	{
		super(args);
		this.reset(alphabet);
	}
	
	@SafeVarargs
	public HuffmanTree(final T... args)
	{
		this(HuffmanTree.ALPHABET_ASCII, args);
	}
	
	protected String compressInput(final String input)
	{
		String retVal = "";
		
		for (int i = 0; i < input.length(); i++)
		{
			char character = input.charAt(i);
			int codeIndex = (character - this.getAlphabetFirst().charValue());
			
			retVal = (retVal + this.getCodeTable()[codeIndex]);
		}
		
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	protected void createCodeTable(final HuffmanNode<T> x, final String s)
	{
		if (!x.isLeaf())
		{
			this.createCodeTable((HuffmanNode<T>)x.getLeft(), s + "0");
			this.createCodeTable((HuffmanNode<T>)x.getRight(), s + "1");
		}
		else
		{
			int index = (((Character)x.getData()).charValue() - this.getAlphabetFirst().charValue());
			this.codeTable[index] = s;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void createHuffmanTree()
	{
		PriorityQueue<HuffmanNode<T>> pq = new PriorityQueue<HuffmanNode<T>>();
		int newSize = 0;
		
		for (int i = 0; i < this.getAlphabetSize(); i++)
		{
			Character letter = this.getAlphabetString().charAt(i);
			
			if (this.getFrequencies()[i] > 0)
			{
				pq.add(new HuffmanNode<T>((T)letter, null, null, null, this.getFrequencies()[i]));
				newSize++;
			}
		}
		
		while (pq.size() > 1)
		{
			HuffmanNode<T> left = pq.poll();
			HuffmanNode<T> right = pq.poll();
			HuffmanNode<T> parent = new HuffmanNode<T>(null, null, right, left, (left.getCount() + right.getCount()));
			pq.add(parent);
			newSize++;
		}
		
		this.setRoot(pq.poll());
		this.setSize(newSize);
	}
	
	public String formatCodeTable()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Code table:\n\n");
		
		for (int i = 0, c = this.getAlphabetFirst().charValue(); i < this.getAlphabetSize(); i++, c++)
		{
			if (this.getCodeTable()[i] != null)
			{
				sb.append((char)c + " = " + this.getCodeTable()[i] + "\n");
			}
		}
		
		return sb.toString();
	}
	
	public String formatData(final String filePath, final DataFormats format)
	{
		StringBuilder sb = new StringBuilder();
		String fileData = "", dataString = "";
		int byteCount = 0, bitCount = 0;
		boolean isInput = false;
		
		switch (format)
		{
			case ORIGINAL:
				
				fileData = this.inputString;
				byteCount = this.inputString.length();
				bitCount = this.inputString.length() * 8;
				isInput = true;
				break;
			
			case COMPRESSED:
				
				fileData = this.compressInput(this.inputString);
				byteCount = (int)(Math.ceil(this.compressInput(this.inputString).length() / 8.0));
				bitCount = this.compressInput(this.inputString).length();
				isInput = false;
				break;
			
			case UNCOMPRESSED:
				
				fileData = this.uncompressOutput(this.compressInput(this.inputString));
				byteCount = this.uncompressOutput(this.compressInput(this.inputString)).length();
				bitCount = this.uncompressOutput(this.compressInput(this.inputString)).length() * 8;
				isInput = true;
				break;
		}
		
		if (filePath != null)
		{
			sb.append("File: " + filePath);
		}
		else
		{
			sb.append("\nFile: N/A");
		}
		
		sb.append("\nSize: " + byteCount + " bytes, " + bitCount + " bits.");
		
		if (isInput)
		{
			dataString = ("\nData:\n\n" + fileData);
		}
		else
		{
			String formattedData = "";
			
			for (int i = 0; i < bitCount; i++)
			{
				if (((i + 1) % 24) == 0)
				{
					formattedData = (formattedData + fileData.charAt(i) + "\n");
				}
				else
					if (((i + 1) % 8) == 0)
					{
						formattedData = (formattedData + fileData.charAt(i) + " ");
					}
					else
					{
						formattedData = (formattedData + fileData.charAt(i));
					}
			}
			
			dataString = ("\nData:\n\n" + formattedData);
		}
		
		sb.append(dataString);
		return sb.toString();
	}
	
	public String formatFrequencies()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Frequency table:\n\n");
		
		for (int i = 0, c = this.getAlphabetFirst().charValue(); i < this.getAlphabetSize(); i++, c++)
		{
			if (this.getFrequencies()[i] > 0)
			{
				sb.append((char)c + " = " + this.getFrequencies()[i] + "\n");
			}
		}
		
		return sb.toString();
	}
	
	public final Character getAlphabetFirst()
	{
		return this.alphabetFirst;
	}
	
	public final int getAlphabetSize()
	{
		return this.alphabetSize;
	}
	
	public final String getAlphabetString()
	{
		return this.alphabetString;
	}
	
	public final String[] getCodeTable()
	{
		return this.codeTable;
	}
	
	public final int[] getFrequencies()
	{
		return this.frequencies;
	}
	
	public final String getInputString()
	{
		return this.inputString;
	}
	
	protected void initialize()
	{
		this.createHuffmanTree();
		this.createCodeTable((HuffmanNode<T>)this.getRoot(), "");
	}
	
	public boolean openFile(final String filePath)
	{
		boolean retVal = false;
		
		if ((filePath == null) || filePath.isEmpty())
		{
			return retVal;
		}
		
		Scanner inputStream = null;
		
		try
		{
			inputStream = new Scanner(new File(filePath));
			retVal = this.parseFile(inputStream);
			
			if (retVal)
			{
				this.initialize();
			}
			
			return retVal;
		}
		catch (Exception exception)
		{
			Support.displayException(null, exception, false);
		}
		finally
		{
			if (inputStream != null)
			{
				inputStream.close();
				inputStream = null;
			}
		}
		
		return retVal;
	}
	
	protected boolean parseFile(final Scanner inputStream)
	{
		while (inputStream.hasNextLine())
		{
			String line = inputStream.nextLine();
			
			for (int i = 0; i < line.length(); i++)
			{
				CharSequence currentCharacterSequence = line.subSequence(i, (i + 1));
				char currentCharacter = line.charAt(i);
				
				if (this.getAlphabetString().contains(currentCharacterSequence))
				{
					this.setInputString(this.getInputString() + currentCharacter);
					int index = (currentCharacter - this.getAlphabetFirst().charValue());
					this.getFrequencies()[index]++;
				}
			}
		}
		
		return true;
	}
	
	protected final void reset(final String alphabet)
	{
		this.setAlphabet(alphabet);
		this.setCodeTable(new String[this.getAlphabetSize()]);
		this.setFrequencies(new int[this.getAlphabetSize()]);
		this.setInputString("");
	}
	
	protected final void setAlphabet(final String alphabet)
	{
		this.setAlphabetString(alphabet);
		this.setAlphabetSize(this.getAlphabetString().length());
		this.setAlphabetFirst(this.getAlphabetString().charAt(0));
	}
	
	protected final void setAlphabetFirst(final Character alphabetFirst)
	{
		this.alphabetFirst = alphabetFirst;
	}
	
	protected final void setAlphabetSize(final int alphabetSize)
	{
		this.alphabetSize = alphabetSize;
	}
	
	protected final void setAlphabetString(final String alphabetString)
	{
		this.alphabetString = alphabetString;
	}
	
	protected final void setCodeTable(final String[] codeTable)
	{
		this.codeTable = codeTable;
	}
	
	protected final void setFrequencies(final int[] frequencies)
	{
		this.frequencies = frequencies;
	}
	
	protected final void setInputString(final String inputString)
	{
		this.inputString = inputString;
	}
	
	@SuppressWarnings("unchecked")
	protected String uncompressOutput(final String output)
	{
		String retVal = "";
		int x = 0;
		
		for (int i = 0; i < this.getInputString().length(); i++)
		{
			HuffmanNode<T> nextNode = (HuffmanNode<T>)this.getRoot();
			
			while (!nextNode.isLeaf())
			{
				if (output.charAt(x) == '1')
				{
					nextNode = (HuffmanNode<T>)nextNode.getRight();
				}
				else
					if (output.charAt(x) == '0')
					{
						nextNode = (HuffmanNode<T>)nextNode.getLeft();
					}
				
				x++;
			}
			
			retVal = (retVal + ((Character)nextNode.getData()).charValue());
		}
		
		return retVal;
	}
}