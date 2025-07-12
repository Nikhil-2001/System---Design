package org.learning;

import java.util.BitSet;

public class BloomFilter {
    private final BitSet bitset;
    private final int bitsetSize;
    private final int numHashFunctions;

    public BloomFilter(int bitsetSize, int numHashFunctions) {
        this.bitsetSize = bitsetSize;
        this.numHashFunctions = numHashFunctions;
        this.bitset = new BitSet(bitsetSize);
    }

    // Add an element to the Bloom filter
    public void add(String key) {
        for (int i = 0; i < numHashFunctions; i++) {
            int hash = hash(key, i);
            bitset.set(Math.abs(hash % bitsetSize));
        }
    }

    // Check if element might be present
    public boolean mightContain(String key) {
        for (int i = 0; i < numHashFunctions; i++) {
            int hash = hash(key, i);
            if (!bitset.get(Math.abs(hash % bitsetSize))) {
                return false; // Definitely not present
            }
        }
        return true; // Might be present
    }

    // Basic hash mixing using key + i, as part hashing we need to have k independent i.e different hash functions,
    // This basic hash function is multiplied with constant 0x5bd1e995 so that the resulting hash functions are different
    // 0x5bd1e995 this is number is particualr choosen from MurmurHash2 because of its high bit mixing, and avalanche effect
    private int hash(String key, int i) {
        return key.hashCode() ^ (i * 0x5bd1e995);
    }

    public static void main(String[] args) {
        BloomFilter bloom = new BloomFilter(1024, 3); // 1024 bits, 3 hash functions

        // Add keys
        bloom.add("apple");
        bloom.add("banana");
        bloom.add("carrot");

        // Check membership
        System.out.println("apple?   " + bloom.mightContain("apple"));   // true
        System.out.println("banana?  " + bloom.mightContain("banana"));  // true
        System.out.println("carrot?  " + bloom.mightContain("carrot"));  // true

        // Key not added
        System.out.println("durian?  " + bloom.mightContain("durian"));  // false (or maybe true if false positive)
        System.out.println("eggplant?" + bloom.mightContain("eggplant")); // false
    }
}