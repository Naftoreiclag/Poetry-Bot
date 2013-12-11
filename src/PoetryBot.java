/* Copyright (c) 2013 "Naftoreiclag" https://github.com/Naftoreiclag
 *
 * Distributed under the MIT License (http://opensource.org/licenses/mit-license.html)
 * See accompanying file LICENSE
 */

package foo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PoetryBot
{
	public static String[] wordList;
	
	public static int POETRYBOTVERSION = 1;
	public static String WORDLISTFILENAME = "brit-shuffle1.txt";
	
	public static void loadWords()
	{
		System.out.println("Begin loading words from " + WORDLISTFILENAME + "...");
		
		List<String> wordArray = new LinkedList<String>();

		BufferedReader reader = null;

		try
		{
			String currentLine;

			reader = new BufferedReader(new FileReader(WORDLISTFILENAME));
			
			while((currentLine = reader.readLine()) != null)
			{
				wordArray.add(currentLine);
			}

		}
		catch(IOException e) { e.printStackTrace(); }
		finally
		{
			try
			{
				if(reader != null)
				{
					reader.close();
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		wordList = new String[wordArray.size()];

		System.out.println("Doing stuff. I'd like to put a progress bar here, but java won't let me. :(");
		for(int i = 0; i < wordArray.size(); ++ i)
		{
			wordList[i] = wordArray.get(i);
		}
		
		System.out.println("Done! Loaded " + wordList.length + " words!");
		System.out.println();
	}
	
	public void sayHello()
	{
		System.out.println("Hello! I am Poetry Bot.");
		System.out.println("I am poetry bot #" + POETRYBOTVERSION);
		System.out.println();
	}

	public void writePoem(String input)
	{
		
		System.out.println("I will write about " + '"' + input + '"' + ".");
		
		int wordIndex = getWordIndex(input);
		
		byte[] hash = getSHA512(input);
		
		if(wordIndex == -1)
		{
			System.out.println("I don't know what a " + '"' + input + '"' + " is, but I'll try my best.");
			
			wordIndex = generateWordIndex(input);
		}
		
		System.out.println("Word index: " + wordIndex);
		
		BitSet hashBits = getBits(hash);
		
		BitSet linePicker = hashBits.get(0, 22);
		
		BitSet[] lines = getLines(linePicker, hashBits);
		
		// THE MAGIC
		for(BitSet bs : lines)
		{
			System.out.println(makeLine(wordIndex, bs));
		}
	}
	
	public String makeLine(int keywordIndex, BitSet bs)
	{
		String returnVal = "";
		
		int numWords = getIntegerFromBitset(bs.get(0, 4));
		
		if(numWords > 2)
		{
			numWords = 2;
		}
		
		int[] fbw = new int[14];
		
		fbw[ 0] = getIntegerFromBitset(bs.get( 4,  8));
		fbw[ 1] = getIntegerFromBitset(bs.get( 8, 12));
		fbw[ 2] = getIntegerFromBitset(bs.get(12, 16));
		fbw[ 3] = getIntegerFromBitset(bs.get(16, 20));
		fbw[ 4] = getIntegerFromBitset(bs.get( 1,  5));
		fbw[ 5] = getIntegerFromBitset(bs.get( 5,  9));
		fbw[ 6] = getIntegerFromBitset(bs.get( 9, 13));
		fbw[ 7] = getIntegerFromBitset(bs.get(13, 17));
		fbw[ 8] = getIntegerFromBitset(bs.get(17, 21));
		fbw[ 9] = getIntegerFromBitset(bs.get( 2,  6));
		fbw[10] = getIntegerFromBitset(bs.get( 6, 10));
		fbw[11] = getIntegerFromBitset(bs.get(10, 14));
		fbw[12] = getIntegerFromBitset(bs.get(14, 18));
		fbw[13] = getIntegerFromBitset(bs.get(18, 22));
		
		String[] fbs = new String[14];
		
		int sum = 0;
		
		for(int index = 0; index < 14; ++ index)
		{
			sum += fbw[index];
			
			fbs[index] = wordList[keywordIndex + sum];
			
			if(fbw[index] == 0)
			{
				fbs[index] = wordList[keywordIndex];
			}
		}
		
		for(int index = 0; index < numWords; ++ index)
		{
			returnVal += fbs[index] + " ";
		}
		
		return returnVal;
	}

	public static int getIntegerFromBitset(BitSet bs)
	{
		int returnVal = 0;
		for (int i = 0; i < 32; i++)
		{
			if(bs.get(i))
			{
				returnVal |= (1 << i);
			}
		}
		return returnVal;
	}

	public static int generateWordIndex(String input)
	{
		int wordIndex = 1;
		while(wordIndex < wordList.length)
		{
			wordIndex *= input.length();
		}
		
		wordIndex = wordIndex % wordList.length;
		
		return wordIndex;
	}
	
	public static int getWordIndex(String word)
	{
		for(int i = 0; i < wordList.length; ++ i)
		{
			if(wordList[i].equals(word))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	private static BitSet[] getLines(BitSet lineChooser, BitSet hashBits)
	{
		int numLines = 0;
		
		for(int lineBitIndex = 0; lineBitIndex < 22; ++ lineBitIndex)
		{
			if(lineChooser.get(lineBitIndex))
			{
				++ numLines;
			}
		}
		
		BitSet[] returnVal = new BitSet[numLines];
		
		int currentSpot = 0;
		for(int lineBitIndex = 0; lineBitIndex < 22; ++ lineBitIndex)
		{
			if(lineChooser.get(lineBitIndex))
			{
				int spot = (lineBitIndex * 22) + 22;
				
				returnVal[currentSpot] = hashBits.get(spot, spot + 22);
				
				++ currentSpot;
			}
		}
		
		return returnVal;
	}
	
	private static void printBitSet(BitSet b, int length)
	{
		for(int i = 0; i < length; ++ i)
		{
			if(b.get(i))
			{
				System.out.print("1");
			}
			else
			{
				System.out.print(".");
			}
		}
		System.out.println();
	}
	
	private static byte[] getSHA512(String input)
	{
		MessageDigest sHAer = null;
		byte[] hash = null;
		try
		{
			sHAer = MessageDigest.getInstance("SHA-512");
		}
		catch (NoSuchAlgorithmException e1) { e1.printStackTrace(); }
		try
		{
			hash = sHAer.digest(input.getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		return hash;
	}
	
	private static BitSet getBits(byte[] bytes)
	{
		BitSet returnVal = BitSet.valueOf(bytes);
		
		return returnVal;
	}
	
	public static void main(String[] args)
	{
		PoetryBot.loadWords();
		PoetryBot pb = new PoetryBot();
		
		// Only need to run this once
		//PoetryBot.shuffleWordsAndWriteToFile();
		
		pb.sayHello();
		Scanner listener = new Scanner(System.in);
		
		boolean run = true;
		
		while(run)
		{

			System.out.print("What would you like me to write about? ");
			String input = listener.nextLine();
			System.out.println();
			
			pb.writePoem(input);
			System.out.println();
		}
		listener.close();
	}
	
	public static void shuffleWordsAndWriteToFile()
	{
		List<String> fooList = Arrays.asList(wordList);
		
		Collections.shuffle(fooList);
		
		PrintWriter writer =
		null;
		try
		{
			writer = new PrintWriter("brit-shuffle1.txt", "UTF-8");
			
			for(String s : fooList)
			{
				writer.println(s);
			}
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.close();
	}
}
