
package userUtil;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * @author Jitin
 *
 */
public class PasswordUtility {
	private static final String algorithm="AES";
	private static final String keyStr="CICS525MSSGROUP6";

	public static String decrypt(String message){
		String decryptedStr="";
		Cipher cipher=null;
		try{
			byte[] key=keyStr.getBytes();
			cipher=Cipher.getInstance(algorithm);
			SecretKeySpec k= new SecretKeySpec(key, algorithm);
			cipher.init(Cipher.DECRYPT_MODE, k);
			byte[] raw=new BASE64Decoder().decodeBuffer(message.trim());
			decryptedStr=new String(cipher.doFinal(raw),"UTF-8");
		}
		catch(NoSuchAlgorithmException algorithmException){
			algorithmException.printStackTrace();
		}
		catch(NoSuchPaddingException noSuchPaddingException){
			noSuchPaddingException.printStackTrace();
		}
		catch(InvalidKeyException invalidKeyException){
			invalidKeyException.printStackTrace();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		catch(IllegalBlockSizeException illegalBlockSizeException){
			illegalBlockSizeException.printStackTrace();
		}
		catch(BadPaddingException badPaddingException){
			badPaddingException.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return decryptedStr;
	}
	
	public static String encrypt(String message){
		Cipher c=null;
		String encryptString="";
		try{
			byte[] key=keyStr.getBytes();
			byte[] dataToSend=message.getBytes();
			c=Cipher.getInstance(algorithm);
			SecretKeySpec k= new SecretKeySpec(key, algorithm);
			c.init(Cipher.ENCRYPT_MODE, k);
			byte[] encryptData=c.doFinal(dataToSend);
			encryptString=new BASE64Encoder().encode(encryptData);
		}
		catch(NoSuchAlgorithmException algorithmException){
			algorithmException.printStackTrace();
		}
		catch(NoSuchPaddingException noSuchPaddingException){
			noSuchPaddingException.printStackTrace();
		}
		catch(InvalidKeyException invalidKeyException){
			invalidKeyException.printStackTrace();
		}
		catch(IllegalBlockSizeException illegalBlockSizeException){
			illegalBlockSizeException.printStackTrace();
		}
		catch(BadPaddingException badPaddingException){
			badPaddingException.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return encryptString;
	}

}
