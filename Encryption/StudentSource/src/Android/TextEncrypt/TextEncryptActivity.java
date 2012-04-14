package Android.TextEncrypt;

import android.app.Activity;
import android.os.Bundle;

//import android.app.Activity;
//import android.os.Bundle;

//import android.app.Activity;
import java.security.SecureRandom;

import javax.crypto.Cipher;

//import android.os.Bundle;
import android.widget.*;

import android.view.View;
import android.view.View.OnClickListener;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class TextEncryptActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		generateKey();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Button encryptButton = (Button) findViewById(R.id.encryptbutton);
		final Button decryptButton = (Button) findViewById(R.id.decryptbutton);
		final EditText input = (EditText) findViewById(R.id.secretMessage);
		final EditText CodedMessage = (EditText) findViewById(R.id.encryptedCode);
		final EditText output = (EditText) findViewById(R.id.originalMessage);
		encryptButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					CodedMessage.setText(encrypt(input.getText().toString()));
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

			};
		});
		decryptButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					output.setText(String.valueOf(decrypt(CodedMessage.getText()
							.toString())));
				} catch (Exception e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
			}
		});
	}

	private final static String RSA = "RSA";
	public static PublicKey pubKey;
	public static PrivateKey priKey;
/**
 *  This method generates public and private keys.
 */
	public static void generateKey()  
	{
		KeyPairGenerator gen = KeyPairGenerator.getInstance(RSA); 
		gen.initialize(512, new SecureRandom());
		KeyPair keyPair = gen.generateKeyPair();
		pubKey = keyPair.getPublic();
		priKey = keyPair.getPrivate();
	}
/**
 *  It takes a string and encrypt it using a PublicKey
 * @param text The parameter text is of type string
 * @param pubRSA The parameter pubRSA is of type PublicKey
 * @return returns array of type byte.
 */
	private static byte[] encrypt(String text, PublicKey pubRSA)
	{
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.ENCRYPT_MODE, pubRSA);
		return cipher.doFinal(text.getBytes());
	}
/**
 * This method takes a string as an argument; Encrypts it and return it as a string. 
 * Or it returns null
 * @param text of type string
 * @return a string
 */
	public final static String encrypt(String text) {
		
			return byte2hex(encrypt(text, pubKey));
		
			return null;
	}
/**
 * This method takes a byte array as an argument and decrypts it using PrivateKey
 * @param src of type byte array.
 * @return a byte array.
 */
	private static byte[] decrypt(byte[] src) {
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		return cipher.doFinal(src);
	}
/**
 * This method takes a string as an argument; Decrypts it and return it as a string. 
 * Or it returns null
 * @param text of type string
 * @return a string
 */
	public final static String decrypt(String data) {
		
			return new String(decrypt(hex2byte(data.getBytes())));
		
			return null;
	}
/**
 * This method takes a byte type array, converts it to hex and returns an uppercase string.
 */
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1)
				hs += ("0" + stmp);
			else
				hs += stmp;
		}
		return hs.toUpperCase();
	}
/**
 * This method is used to convert from hex to byte. 
 */
	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0){
			throw new IllegalArgumentException("IllegalArgumentException");
		}
		byte[] b2 = new byte[b.length / 2];

		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}
}