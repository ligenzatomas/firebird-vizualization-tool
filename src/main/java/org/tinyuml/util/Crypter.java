/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyuml.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author cml
 */
public class Crypter {
	
	private static final char[] PASSWORD = "ahcaiWu2Nafai3hu".toCharArray();
	private static final byte[] SALT = { 
		(byte) 0x12, (byte) 0x15, (byte) 0xef, (byte) 0x28,
		(byte) 0xde, (byte) 0x31, (byte) 0x00, (byte) 0xff};
	
	public static void encryptObjectToFile(Serializable object, String filename) {	
		
		try {
			
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));

			Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 8));

			SealedObject sealedObject = new SealedObject( object, pbeCipher);
			
			CipherOutputStream cipherOutputStream = new CipherOutputStream( new BufferedOutputStream( new FileOutputStream( filename ) ), pbeCipher );
			
			try {
				ObjectOutputStream outputStream = new ObjectOutputStream( cipherOutputStream );

				outputStream.writeObject( sealedObject );
				outputStream.close();
				
			} finally {
				
				try {
					cipherOutputStream.close();
				} catch (IOException ex) {
					Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidKeySpecException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (NoSuchPaddingException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidKeyException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidAlgorithmParameterException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalBlockSizeException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static Object decryptObjectFromFile(String filename) {
		
		Object obj = new Object();
		CipherInputStream cipherInputStream = null;
		
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));

			Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 8));

			cipherInputStream = new CipherInputStream( new BufferedInputStream( new FileInputStream( filename ) ), pbeCipher );
			
			try {
				ObjectInputStream inputStream = new ObjectInputStream( cipherInputStream );

				SealedObject sealedObject = (SealedObject) inputStream.readObject();
				obj = sealedObject.getObject( pbeCipher );
			} catch (IOException ex) {
				Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalBlockSizeException ex) {
				Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
			} catch (BadPaddingException ex) {
				Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
			
				try {
					cipherInputStream.close();
				} catch (IOException ex) {
					Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidKeySpecException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (NoSuchPaddingException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidKeyException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidAlgorithmParameterException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return obj;
	}
	
	public static String encryptString(String text) {
		
		String encryptText = text;
		
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));

			Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 8));

			encryptText = base64Encode(pbeCipher.doFinal(text.getBytes("UTF-8")));
		} catch (BadPaddingException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidKeySpecException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (NoSuchPaddingException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidKeyException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidAlgorithmParameterException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalBlockSizeException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return encryptText;
	}
	
	public static String decryptString(String text) {
		
		String decryptText = text;
		
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));

			Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 8));

			decryptText = new String(pbeCipher.doFinal(base64Decode(text)), "UTF-8");
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidKeySpecException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (NoSuchPaddingException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidKeyException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidAlgorithmParameterException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalBlockSizeException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (BadPaddingException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Crypter.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return decryptText;
	}
	
	private static String base64Encode(byte[] bytes) {
		
		return DatatypeConverter.printBase64Binary(bytes);
	}
	
	private static byte[] base64Decode(String text) throws IOException {
		
		return DatatypeConverter.parseBase64Binary(text);
	}
}
