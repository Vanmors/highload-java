package com.vanmors.lsh;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

public class MinHash {
    private final int n;
    private final int[] seeds;

    public MinHash(final int n, final long seed) {
        this.n = n;
        final var rnd = new Random(seed);           // fixed seed: the same hash family on every run
        seeds = new int[n];
        for (var i = 0; i < n; i++) {
            seeds[i] = rnd.nextInt();
        }
    }

    public int[] signature(final Set<String> shingles) {
        final var sig = new int[n];
        Arrays.fill(sig, Integer.MAX_VALUE);
        for (final String s : shingles) {
            for (var i = 0; i < n; i++) {        // n full string hashes per shingle
                final var v = hash(s, seeds[i]);
                if (v < sig[i]) sig[i] = v;
            }
        }
        return sig;
    }

    private int hash(final String shingle, final int seed) {
        // a new hash function is created and the whole string is rehashed on every call
        final var h = Hashing.murmur3_32_fixed(seed)
                .hashString(shingle, StandardCharsets.UTF_8)
                .asInt();
        return h & 0x7FFFFFFF;                   // non-negative, so MAX_VALUE stays a valid initial value
    }

}
