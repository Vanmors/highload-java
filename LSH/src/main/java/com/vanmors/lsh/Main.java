package com.vanmors.lsh;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        final var documents = List.of(
                "Кошка ловит мышь в доме каждый день",       // 0
                "Кошка ловит мышь в доме каждый вечер",      // 1 — почти как 0
                "Собака бегает по парку с мячом",            // 2
                "Кошка спит на диване весь день",            // 3
                "Собака бегает по парку с красным мячом"     // 4 — почти как 2
        );

        // k = 2 слова в шингле, 20 полос по 2 числа (сигнатура из 40 чисел)
        final var lsh = new LSH(documents, 2, 20, 2);
        lsh.execute();

        final var queries = List.of(
                "Кошка ловит мышь в доме каждый день",       // точная копия 0 -> должен найтись 0 (и, скорее всего, 1)
                "Собака бегает по парку с синим мячом",      // похож на 2 и 4
                "Совсем другой текст про погоду и дождь"     // ничего похожего
        );

        for (final var query : queries) {
            final var candidates = lsh.findCandidates(query);
            System.out.println("Запрос: " + query);
            System.out.println("Кандидаты: " + Arrays.toString(candidates));
            for (final var id : candidates) {
                System.out.println("   " + id + ": " + documents.get(id));
            }
            System.out.println();
        }
    }
}
