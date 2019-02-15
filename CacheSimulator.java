import java.util.BitSet;
import java.util.Random;

public class CacheSimulator {

    public static final int ROWS = 100;
    public static final int COLS = 100;

    public static void main(String[] args) {
	    Cache cache_direct_map = new Cache(1048576, 4, 1); // 0 3658
        Cache cache_two_way = new Cache(1048576, 4, 2);  // 0 3736
        Cache cache_four_word_block = new Cache(1048576, 16, 1); // 7500 7549

        Cache testCache = cache_four_word_block;

        int ArrayBase = Cache.mainMemory.getBaseAddress();
        
        
        // Reading row major
        for (int r = 0; r < ROWS; ++r) { // 10000 => 2^13
            for (int c = 0; c < COLS; ++c) {
                int arrayIndex =  r * ROWS + c;
                int data = testCache.read(arrayIndex + ArrayBase);
                // TIL, java doesn't automatically enable assert statements
                // You need to send -ea flag to java as:
                // $ java -ea CacheSimulator
                
                System.out.println(r+" "+c +" "+ data +  " " + Cache.mainMemory.mem[r * ROWS + c]);
                assert data == Cache.mainMemory.mem[r * ROWS + c];
            }
        }
        
        System.out.println("hit/miss " + testCache.hits + " " + testCache.misses);
//        testCache.hits = 0;
//        testCache.misses = 0;
        
//        for (int r = 0; r < ROWS; ++r) { // rows
//            for (int c = 0; c < COLS; ++c) {
//                int arrayIndex = r * ROWS + c;
//                int data = testCache.read(arrayIndex + ArrayBase);
//                // TIL, java doesn't automatically enable assert statements
//                // You need to send -ea flag to java as:
//                // $ java -ea CacheSimulator
//                assert data == Cache.mainMemory.mem[r * ROWS + c];
//            }
//        }
//        System.out.println("Cache four word block");
//        System.out.println("hit/miss " + testCache.hits + " " + testCache.misses);
    }
    
}