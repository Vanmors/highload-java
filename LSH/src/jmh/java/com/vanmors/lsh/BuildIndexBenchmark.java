package com.vanmors.lsh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class BuildIndexBenchmark {

    @Param({"1000", "10000"})
    int docCount;

    List<String> documents;
    LSH lsh;
    String query;

    @Setup(Level.Trial)
    public void setup() {
        final var rnd = new Random(42);
        final var dictionary = new String[2000];
        for (int i = 0; i < dictionary.length; i++) {
            dictionary[i] = "w" + i;
        }
        documents = new ArrayList<>();
        for (int d = 0; d < docCount; d++) {
            final var sb = new StringBuilder();
            for (int w = 0; w < 100; w++) {          // 100 слов в документе
                sb.append(dictionary[rnd.nextInt(dictionary.length)]).append(' ');
            }
            documents.add(sb.toString());
        }
        lsh = new LSH(documents, 3, 20, 5);
        lsh.execute();
        query = documents.get(0);
    }

    @Benchmark
    public LSH buildIndex() {
        final var index = new LSH(documents, 3, 20, 5);
        index.execute();
        return index;
    }

    @Benchmark
    public int[] findCandidates() {
        return lsh.findCandidates(query);
    }

}
