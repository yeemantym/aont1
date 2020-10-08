//AONT (Bastion) first and then encrypt the fragments in parallel

package aont1;
import java.util.Arrays;
import java.util.stream.IntStream;

public class aont {
	   
	public static void main(String[] args) {
		
		String originalString = "Hello";
		final String secretKey = "ssshhhhhhhhhhh!!!!";
		byte[] inputArray = originalString.getBytes();
		System.out.println("inputArray:");
		System.out.println(Arrays.toString(inputArray));
		
		//apply Bastion AONT to inputArray
		int t = 0;
		for (int i=0; i<inputArray.length; i++) {
			t=t^inputArray[i];
		}
		
		int[] inputArrayAONT = new int[inputArray.length]; 
		for (int i=0; i<inputArray.length; i++) {
			inputArrayAONT[i]=inputArray[i]^t;
		}
		System.out.println("inputArrayAONT:");
		System.out.println(Arrays.toString(inputArrayAONT));
		
		//encrypt inputArrayAONT in parrallel with aes 
		String[] encryptedArray = new String[inputArray.length];
		/*for (int i=0; i<inputArray.length; i++) {
			encryptedArray[i]=aes.encrypt(Integer.toString(inputArrayAONT[i]), secretKey);
		}*/
		IntStream.range(0, inputArray.length).parallel().forEach(i->{
			encryptedArray[i]=aes.encrypt(Integer.toString(inputArrayAONT[i]), secretKey);
		});

		System.out.println("encryptedArray:");
		System.out.println(Arrays.toString(encryptedArray));		
		
	}


}
