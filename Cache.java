import java.util.BitSet;

public class Cache {

	public int Capacity; // 2^m bytes
	public int BlockSize; // B-byte (2^b-byte) blocks
	public int Associativity; // 1 to N

	public int tagLength;
	public int indexLength;
	public int blockOffsetLength;
	public int wordPerBlock;
	
	public int a = 0;

	public int hits,misses;
	
	public static MainMemory mainMemory;

	private CacheEntry Sets[][];

	/**
	 *
	 * @param capacity
	 *            cache capacity in bytes
	 * @param blocksize
	 *            size of each block in bytes
	 * @param associativity
	 *            the number of blocks in each set
	 */
	public Cache(int capacity, int blocksize, int associativity) {
		Associativity = associativity;
		BlockSize = blocksize;
		wordPerBlock = BlockSize / 4;
		// TODO create the main memory if it doesn't already exist
		mainMemory = new MainMemory();
		// TODO create sets of cache entries
		indexLength = (capacity / (Associativity * BlockSize));
		indexLength = BitCalculator.bitsToRepr(indexLength);
		blockOffsetLength = BitCalculator.bitsToRepr(wordPerBlock);
		tagLength = 32 - (indexLength + blockOffsetLength);
		
		int index = (capacity / (Associativity * BlockSize));
		Sets = new CacheEntry[index][associativity];
		for (int i = 0; i < index; i++) {
			for (int j = 0; j < associativity; j++) {
				Sets[i][j] = new CacheEntry(tagLength, wordPerBlock);
			}
		}
	}

	public int read(int address) {
		BitSet bit = BitCalculator.intToBinary(address);

		// TODO extract the tag, index, word offset fields from the address
		String wOffset = "";
		String Adr = BitCalculator.toString(bit);
		for (int i = Adr.length(); i < 32; i++) {
			// Add zeros to beginning
			Adr = "0" + Adr;
		}
		String tag = Adr.substring(0, tagLength);
		String index = Adr.substring(tagLength, tagLength + indexLength);
		int Woffset = 0;
		if (blockOffsetLength > 0) {
			wOffset = Adr.substring(tagLength + indexLength, Adr.length());
			BitSet bs = new BitSet(blockOffsetLength);
			
			int b = 0;
			for(int i = wOffset.length() - 1; i >= 0; i--) {
				if(wOffset.charAt(i) == '1')
					bs.set(b);
				b++;
			}	
			Woffset = BitCalculator.toInteger(bs);
		}
		
		
		
		// TODO check each set if the cache entry's valid bit is set
		int Tag = Integer.parseInt(tag, 2);
		int Index = Integer.parseInt(index, 2);
		// and the tag value equates at the index row
		int way = 0;
		for (int i = 0; i < Associativity; i++) {
			if (Sets[Index][i].isValid()) {
				BitSet bTag = BitCalculator.intToBinary(Tag);
				if (Sets[Index][i].compareTag(bTag)) {
					a++;
					hits++;
					return BitCalculator.toInteger(Sets[Index][i].read(Woffset));
				} else if (!Sets[Index][i].compareTag(bTag)) {
					way++;
				}

			} else if (!Sets[Index][i].isValid()) {
				way++;
			}
		}
		int r = 0;
		if (way == Associativity) {
			misses++;
			int[] Data = new int[wordPerBlock];
			int remainder = address % wordPerBlock;
			for (int i = 0; i < Data.length; i++) {
				Data[i] = mainMemory.read(address - remainder + i);
			}
			this.write(Data, Index, Tag);
		}
		for (int i = 0; i < Associativity; i++) {
			if (Sets[Index][i].isValid()) {
				BitSet bTag = BitCalculator.intToBinary(Tag);
				if (Sets[Index][i].compareTag(bTag)) {
					return BitCalculator.toInteger(Sets[Index][i].read(Woffset));
				}
			}
		}
		return 0;
	}

	// TODO write data to the cache entry, if you already do this inside read()
	// feel free to refactor that functionality to here
	private void write(int[] data, int index, int tag) {
		int rnd = (int)(Math.random() * Associativity);
		Sets[index][rnd].write(data, tag);
	}

}