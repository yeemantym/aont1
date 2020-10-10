//AONT (Bastion) first and then encrypt the fragments in parallel

package aont1;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import aont1.CryptoUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class aont {
	private static String readAllBytesJava7(String filePath) 
    {
        String content = "";
 
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
 
        return content;
    }
	
	public static void main(String[] args) throws Exception{
		long startTime = System.nanoTime();
		//String originalString = "Hello";
		String filePath = "/home/yeeman/Documents/300MB.txt";
		String originalString = readAllBytesJava7( filePath );
		
		// encrypt and decrypt need the same key.
        // get AES 256 bits (32 bytes) key
        SecretKey secretKey = CryptoUtils.getAESKey(256);

        // encrypt and decrypt need the same IV.
        // AES-GCM needs IV 96-bit (12 bytes)
        byte[] iv = CryptoUtils.getRandomNonce(12);
        
		byte[] inputArray = originalString.getBytes();
		
		//apply Bastion AONT to inputArray
		int t = 0;
		for (int i=0; i<inputArray.length; i++) {
			t=t^inputArray[i];
		}
		
		byte[] inputArrayAONT = new byte[inputArray.length]; 
		for (int i=0; i<inputArray.length; i++) {
			inputArrayAONT[i]=(byte)(inputArray[i]^t);
		}
		
		//encrypt inputArrayAONT in parallel with aes 
		byte[] encryptedArray = new byte[inputArray.length];
		encryptedArray = aes_gcm.encryptWithPrefixIV(inputArrayAONT, secretKey, iv);
	/*	IntStream.range(0, inputArray.length).parallel().forEach(i->{
			encryptedArray[i]=aes.encrypt(Integer.toString(inputArrayAONT[i]), secretKey);
		});*/

	/*	System.out.println("encryptedArray:");
		for (int i=0; i<encryptedArray.length; i++) {
			if (i % 10 == 0 && i > 0) {
	            System.out.println();
	        } 
			System.out.print(encryptedArray[i]);
			if (i!=encryptedArray.length-1) {
				System.out.print(", ");
			}
			else {
				System.out.println();
			}		
		}*/
		System.out.println("Length of encryptedArray: "+encryptedArray.length);		
		long endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime) + " ns"); 
		
	}


}
