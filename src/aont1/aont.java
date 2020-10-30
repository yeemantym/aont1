//AONT (Bastion) first and then encrypt the fragments in parallel

package aont1;
import aont1.CryptoUtils;
import aont1.aes_gcm;
import aont1.aont;

import javax.crypto.SecretKey;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


public class aont {
	
	// split array into fragments of constant size
	public List<byte[]> splitArrayConstant(byte[] array, int numOfChunks) {
			
		List<byte[]> result = new ArrayList<byte[]>();
		int chunkSize = (int)Math.ceil((double)array.length/numOfChunks);
	    for(int i = 0; i < numOfChunks; ++i) {
	    	int start = i * chunkSize;
	    	int length = Math.min(array.length - start, chunkSize);
	    	byte[] temp = new byte[length];
	    	System.arraycopy(array, start, temp, 0, length);
	    	result.add(temp);
	    }
		return result;
	}
	
	public static void main(String[] args) throws Exception{
		long startTime = System.nanoTime();
		String filePath = "/home/yeeman/Documents/2000MB.txt";
		aont aont = new aont();
		
		// encrypt and decrypt need the same key.
        // get AES 256 bits (32 bytes) key
        SecretKey secretKey = CryptoUtils.getAESKey(256);

        // encrypt and decrypt need the same IV.
        // AES-GCM needs IV 96-bit (12 bytes)
        byte[] iv = CryptoUtils.getRandomNonce(12);
        
		File file = new File(filePath);
		byte[] inputArray = Files.readAllBytes(file.toPath());
	
		//apply Bastion AONT to inputArray
		int t = 0;
		for (int i=0; i<inputArray.length; i++) {
			t=t^inputArray[i];
		}
			
		byte[] inputArrayAONT = new byte[inputArray.length]; 
		for (int i=0; i<inputArray.length; i++) {
			inputArrayAONT[i]=(byte)(inputArray[i]^t);
		}
			
		//Fragmentation of inputArrayAONT into 4 fragments of constant size
		List<byte[]> fragment = aont.splitArrayConstant(inputArrayAONT,4);
		
		//Encrypt each fragment in parallel
		List<byte[]> encryptedListBytes = new ArrayList<byte[]>();
				
		IntStream.range(0, fragment.size()).parallel().forEach(i->{
			byte encrypt[] = new byte[fragment.get(i).length];
			try {
				encrypt = aes_gcm.encryptWithPrefixIV(fragment.get(i), secretKey, iv);
			} catch (Exception e) {
				e.printStackTrace();
			}
				encryptedListBytes.add(encrypt);
		});
	
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
		System.out.println("Length of encryptedArray: "+encryptedListBytes.size());		
		long endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime) + " ns"); 
		
	}


}
