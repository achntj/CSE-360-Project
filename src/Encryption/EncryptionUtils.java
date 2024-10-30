package Encryption;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * <p>EncryptionUtils class.</p>
 * 
 * <p>Description: This utility class provides methods for converting between 
 * byte arrays and character arrays, generating an initialization vector 
 * for encryption, and printing character arrays.</p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00	2024-10-30 Project Phase 2 User Home Page
 */

public class EncryptionUtils {
	private static int IV_SIZE = 16;
	
	/**
     * Converts a byte array to a character array using the default charset.
     * 
     * @param bytes The byte array to convert.
     * @return The converted character array.
     */
	public static char[] toCharArray(byte[] bytes) {		
        CharBuffer charBuffer = Charset.defaultCharset().decode(ByteBuffer.wrap(bytes));
        return Arrays.copyOf(charBuffer.array(), charBuffer.limit());
	}
	
    /**
     * Converts a character array to a byte array using the default charset.
     * 
     * @param chars The character array to convert.
     * @return The converted byte array.
     */
	static byte[] toByteArray(char[] chars) {		
        ByteBuffer byteBuffer = Charset.defaultCharset().encode(CharBuffer.wrap(chars));
        return Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
	}
		
	/**
     * Generates an initialization vector (IV) from a character array.
     * The IV is created by repeating the input characters until it reaches the specified size.
     * 
     * @param text The character array used to generate the IV.
     * @return A byte array representing the initialization vector.
     */
	public static byte[] getInitializationVector(char[] text) {
		char iv[] = new char[IV_SIZE];
		
		int textPointer = 0;
		int ivPointer = 0;
		while(ivPointer < IV_SIZE) {
			iv[ivPointer++] = text[textPointer++ % text.length];
		}
		
		return toByteArray(iv);
	}
	
	/**
     * Prints the characters in a character array to the standard output.
     * 
     * @param chars The character array to print.
     */
	public static void printCharArray(char[] chars) {
		for(char c : chars) {
			System.out.print(c);
		}
	}
}
