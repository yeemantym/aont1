//AONT (Bastion) first and then encrypt the fragments in parallel

package aont1;
import aont1.CryptoUtils;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;


public class aont {
	
	public static void main(String[] args) throws Exception{
		long startTime = System.nanoTime();
		String filePath = "/home/yeeman/Documents/1MB.txt";
		
		// encrypt and decrypt need the same key.
        // get AES 256 bits (32 bytes) key
        SecretKey secretKey = CryptoUtils.getAESKey(256);

        // encrypt and decrypt need the same IV.
        // AES-GCM needs IV 96-bit (12 bytes)
        byte[] iv = CryptoUtils.getRandomNonce(12);
        
		File file = new File(filePath);
		//byte[] inputArray = Files.readAllBytes(file.toPath());
		FileInputStream is = new FileInputStream(file);
		FileOutputStream os = new FileOutputStream(new File("output_aont1.txt"));
		byte[] inputArray = new byte[4096];
		int read = 0;
		while((read = is.read(inputArray)) > 0) {
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
		    //os.write(encryptedArray);
		    for (int i=0; i<encryptedArray.length; i++) { 
				os.write(encryptedArray[i]);		
			}
		}
		is.close();
		os.close();

	
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
		//System.out.println("Length of encryptedArray: "+encryptedArray.length);		
		long endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime) + " ns"); 
		
	}


}
