import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class PoetryBot
{
	public static int POETRYBOTVERSION = 1;

	private void run()
	{
		Scanner listener = new Scanner(System.in);

		System.out.println("Hello! I am Poetry Bot.");
		
		System.out.println("I am poetry bot #" + POETRYBOTVERSION);
		
		System.out.println();

		System.out.print("What would you like me to write about? ");
		String input = listener.nextLine();
		System.out.println();
		
		System.out.println("I will write about " + '"' + input + '"' + ".");
		
		byte[] hash = getSHA512(input);
		
		for(byte b : hash)
		{
			System.out.println(b);
		}
		
		listener.close();
	}
	
	private byte[] getSHA512(String input)
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

	public static void main(String[] args)
	{
		PoetryBot pb = new PoetryBot();
		pb.run();
	}
}
