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
	
	public static int POETRYBOTVERSION = 2;
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
		
		byte[] hash = getSHA256(input);
		
		if(wordIndex == -1)
		{
			//System.out.println("I don't know what a " + '"' + input + '"' + " is, but I'll try my best.");
			System.out.println("I don't know what a " + '"' + input + '"' + " is, sorry!");
			
			wordIndex = generateWordIndex(input);
			return;
		}
		
		System.out.println("Word index: " + wordIndex);
		
		BitSet hashBits = getBits(hash);
		
		BitSet linePicker = hashBits.get(0, 16);
		
		BitSet[] lines = getLines(linePicker, hashBits);
		
		// THE MAGIC
		for(BitSet bs : lines)
		{
			System.out.println(makeLine(wordIndex, bs));
		}
	}
	
	public String makeLine(int keywordIndex, BitSet bs)
	{
		String returnVal = "+" + wordList[keywordIndex];
		
		int numWordsToAdd = getIntegerFromBitset(bs.get(0, 2)) + 1;
		
		int[] wordIndexOffsets = new int[numWordsToAdd];
		
		int total = 1;
		for(int spot = 0; spot < numWordsToAdd; ++ spot)
		{
			int number = getIntegerFromBitset(bs.get((spot * 4), (spot * 4) + 4));
			
			total += number;
			
			wordIndexOffsets[spot] = total;
			
			returnVal += " " + wordList[keywordIndex + total];
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
		
		for(int lineBitIndex = 0; lineBitIndex < 15; ++ lineBitIndex)
		{
			if(lineChooser.get(lineBitIndex))
			{
				++ numLines;
			}
		}
		
		BitSet[] returnVal = new BitSet[numLines];
		
		int currentSpot = 0;
		for(int lineBitIndex = 0; lineBitIndex < 15; ++ lineBitIndex)
		{
			if(lineChooser.get(lineBitIndex))
			{
				int spot = (lineBitIndex * 16) + 16;
				
				returnVal[currentSpot] = hashBits.get(spot, spot + 16);
				
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
	
	private static byte[] getSHA256(String input)
	{
		MessageDigest sHAer = null;
		byte[] hash = null;
		try
		{
			sHAer = MessageDigest.getInstance("SHA-256");
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
