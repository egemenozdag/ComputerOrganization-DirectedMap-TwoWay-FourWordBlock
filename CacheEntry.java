import java.util.BitSet;

public class CacheEntry {

	/*
	 * Valid Field Tag Field Data Field
	 */
	BitSet bits;

	private int tagBitsLength;
	private int wordsPerBlock;

	private int validBitIndex;
	private int tagIndex;
	private int dataIndex;
	private int lengthOfBitSet;

	public CacheEntry(int tagBits, int wordsPerBlock) {

		// TODO initialize BitSet bits to the correct length

		this.wordsPerBlock = wordsPerBlock;
		tagBitsLength = tagBits;
		
		lengthOfBitSet = 1 + tagBits + (wordsPerBlock * 32); // 1 -> Valid
		bits = new BitSet(lengthOfBitSet);

//		System.out.println(lengthOfBitSet);

//		BitCalculator.intToBinary(tagBits);
		
		// TODO calculate the bit indices
		validBitIndex = lengthOfBitSet - 1;
		tagIndex = (wordsPerBlock * 32);
		dataIndex = 0;
	}

	
	public Boolean isValid() {
		return bits.get(validBitIndex);
	}

	public Boolean compareTag(BitSet cacheTagField) {
		return cacheTagField.equals(bits.get(32*wordsPerBlock, 32*wordsPerBlock + tagBitsLength));
	}

	public BitSet read(int wordOffset) {
		return bits.get(wordOffset * 32, wordOffset * 32 + 32);
		// Data is in the rightmost part of the BitSet.
		// We need to read 32 bit for a data.
	}

	public void write(int[] data, int tag) {
		
//		bits = new BitSet(lengthOfBitSet);
		
		bits.set(validBitIndex);
		
		BitSet tBin = BitCalculator.intToBinary(tag);
		String tString = BitCalculator.toString(tBin);
		
		int b = 0;
		for (int j = tString.length()-1; j >= 0; j--) {
			if (tString.charAt(j) == '1')
				bits.set(tagIndex + b);  // give index to the method, bit in that index will be 1.
			b++;
		}	
		
		
		for (int a = 0; a < data.length; a++) {
			
			BitSet dBin = BitCalculator.intToBinary(data[a]);
			String dString = BitCalculator.toString(dBin);	
			int d = BitCalculator.toInteger(dBin);
			
			
			while(dString.length() != 32) {
				dString = "0" + dString;
			}
			
			
			b = 0;				
			for (int i = dString.length() -1; i >= 0; i--) {
				if (dString.charAt(i) == '1')
					bits.set(32*a + b);
				b++;
			}
			
		}		
	}
}