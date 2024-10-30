package Encryption;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider; 

/**
 * <p>EncryptionHelper class.</p>
 * 
 * <p>Description: This class provides methods for encrypting and decrypting 
 * data using the AES encryption algorithm in CBC mode with PKCS5 padding. 
 * It utilizes the Bouncy Castle security provider for cryptographic operations.</p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00	2024-10-30 Project Phase 2 User Home Page
 */

public class EncryptionHelper {

	private static String BOUNCY_CASTLE_PROVIDER_IDENTIFIER = "BC";	
	private Cipher cipher;
	
	// Key bytes for AES encryption (128-bit key)
	byte[] keyBytes = new byte[] {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
	private SecretKey key = new SecretKeySpec(keyBytes, "AES");

	 /**
     * Constructs an EncryptionHelper object and initializes the cipher instance 
     * with the Bouncy Castle security provider.
     * 
     * @throws Exception If there is an error initializing the cipher.
     */
	public EncryptionHelper() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", BOUNCY_CASTLE_PROVIDER_IDENTIFIER);		
	}
	
	/**
     * Encrypts the given plaintext using AES encryption with the specified 
     * initialization vector (IV).
     * 
     * @param plainText The plaintext to encrypt.
     * @param initializationVector The IV used for encryption.
     * @return The encrypted ciphertext.
     * @throws Exception If there is an error during encryption.
     */
	public byte[] encrypt(byte[] plainText, byte[] initializationVector) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(initializationVector));
		return cipher.doFinal(plainText);
	}
	
    /**
     * Decrypts the given ciphertext using AES decryption with the specified 
     * initialization vector (IV).
     * 
     * @param cipherText The ciphertext to decrypt.
     * @param initializationVector The IV used for decryption.
     * @return The decrypted plaintext.
     * @throws Exception If there is an error during decryption.
     */
	public byte[] decrypt(byte[] cipherText, byte[] initializationVector) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(initializationVector));
		return cipher.doFinal(cipherText);
	}
	
}
