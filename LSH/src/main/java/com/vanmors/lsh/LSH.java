package com.vanmors.lsh;


import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.*;

public class LSH {

    private final List<String> documents;

    private final int k;

    private final MinHash minHash;

    private final List<Map<IntArrayList, IntArrayList>> buckets; // своя таблица корзин на каждую полосу

    private final int b; // сколько полос

    private final int r; // сколько в одной полосе

    private final int signatures[][];

    public LSH(final List<String> documents, final int k, final int b, final int r) {
        this.documents = documents;
        this.k = k;
        this.b = b;
        this.r = r;
        this.minHash = new MinHash(b * r, 102); // длина сигнатуры = b * r
        this.buckets = new ArrayList<>(b);
        for (var band = 0; band < b; band++) {
            buckets.add(new HashMap<>());
        }
        this.signatures = new int[documents.size()][b * r];
    }

    private Set<String> createShingles(final String document) {
        final var words = document.toLowerCase()
                .replaceAll("\\p{P}", " ")
                .trim()
                .split("\\s+");
        final var shingles = new HashSet<String>();

        if (words.length == 0 || words[0].isEmpty()) {
            return shingles;
        }

        final var size = Math.min(k, words.length);

        for (int start = 0; start + size <= words.length; start++) {
            final var shingle = new StringBuilder();
            for (int i = start; i < start + size; i++) {
                if (i > start) {
                    shingle.append(' ');
                }
                shingle.append(words[i]);
            }
            shingles.add(shingle.toString());
        }

        return shingles;
    }

    public void execute() {
        for (var docId = 0; docId < documents.size(); docId++) {
            final var shingles = createShingles(documents.get(docId));
            if (shingles.isEmpty()) {
                continue; // пустой документ: сигнатура из MAX_VALUE совпала бы со всеми пустыми
            }
            final var signature = minHash.signature(shingles);
            signatureToBucket(signature, docId);
            signatures[docId] = signature;
        }
    }

    private void signatureToBucket(final int[] signature, final int docId) {
        for (var band = 0; band < b; band++) {
            // ключ — r чисел этой полосы
            final var key = new IntArrayList(r);
            for (var i = 0; i < r; i++) {
                key.add(signature[band * r + i]);
            }
            // корзина в таблице именно этой полосы; если её нет — создаём и кладём в мапу
            buckets.get(band)
                    .computeIfAbsent(key, k -> new IntArrayList())
                    .add(docId);
        }
    }

    public int[] findCandidates(final String document) {
        final var shingles = createShingles(document);
        if (shingles.isEmpty()) {
            return IntArrays.EMPTY_ARRAY;
        }
        final var signature = minHash.signature(shingles);

        // документы, у которых хотя бы одна полоса совпала целиком
        final var candidates = new IntOpenHashSet();
        for (var band = 0; band < b; band++) {
            final var key = new IntArrayList(r);
            for (var i = 0; i < r; i++) {
                key.add(signature[band * r + i]);
            }
            final var bucket = buckets.get(band).get(key);
            if (bucket != null) {
                candidates.addAll(bucket);
            }
        }

        return candidates.toIntArray();
    }

}
