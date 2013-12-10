/* Copyright (c) 2013 "Naftoreiclag" https://github.com/Naftoreiclag
 *
 * Distributed under the MIT License (http://opensource.org/licenses/mit-license.html)
 * See accompanying file LICENSE
 */

package foo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Scanner;

public class PoetryBot
{
	public static int POETRYBOTVERSION = 1;
	
	public void loadWords()
	{
		System.out.println("Begin loading words...");

		BufferedReader reader = null;

		try
		{
			String currentLine;

			reader = new BufferedReader(new FileReader("testfile.txt"));

			while((currentLine = reader.readLine()) != null)
			{
				System.out.println(currentLine);
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
		
		System.out.println("Done!");
	}
	
	public void sayHello()
	{
		System.out.println("Hello! I am Poetry Bot.");
		System.out.println("I am poetry bot #" + POETRYBOTVERSION);
		System.out.println();
	}

	public void writePoem()
	{
		System.out.print("What would you like me to write about? ");
		Scanner listener = new Scanner(System.in);
		String input = listener.nextLine();
		listener.close();
		System.out.println();
		
		System.out.println("I will write about " + '"' + input + '"' + ".");
		
		byte[] hash = getSHA512(input);
		
		BitSet hashBits = getBits(hash);
		
		BitSet linePicker = hashBits.get(0, 22);
		
		BitSet[] lines = getLines(linePicker, hashBits);
		
		for(BitSet bs : lines)
		{
			
		}
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
		PoetryBot pb = new PoetryBot();
		pb.sayHello();
		pb.loadWords();
		pb.writePoem();
	}
}
