package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class Day22 extends Day {
    public Day22() {
        super(22);
    }

    public static void main(String[] args) {
        new Day22()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("13022553808")
                .verifyPart2("1555");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        results.setPart1(part1(input));
        results.setPart2(part2(input));
    }

    private long part1(String input) {
        return input.lines()
                .mapToLong(Long::parseLong)
                .map(secret -> {
                    for (int i = 0; i < 2000; i++) {
                        secret = generateNext(secret);
                    }
                    return secret;
                })
                .sum();
    }

    private long part2(String input) {
        return input.lines()
                .parallel()
                .mapToLong(Long::parseLong)
                .mapToObj(secret -> {
                    Map<List<Integer>, Integer> priceMap = new HashMap<>();

                    List<Integer> priceChanges = new ArrayList<>();
                    int previousPrice = 0;
                    for (int i = 0; i < 2000; i++) {
                        secret = generateNext(secret);
                        int price = (int) (secret % 10);
                        priceChanges.add(price - previousPrice);
                        if (priceChanges.size() > 4) {
                            priceChanges.removeFirst();
                            priceMap.putIfAbsent(new ArrayList<>(priceChanges), price);
                        }
                        previousPrice = price;
                    }
                    return priceMap;
                })
                .flatMap(priceMap -> priceMap.entrySet().stream())
                .collect(groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)))
                .values().stream()
                .mapToInt(Integer::intValue)
                .max().orElseThrow();
    }

    private long generateNext(long currentSecret) {
        long secret = currentSecret;
        long result = secret << 6;
        secret = mix(result, secret);
        secret = prune(secret);

        result = secret >>> 5;
        secret = mix(result, secret);
        secret = prune(secret);

        result = secret << 11;
        secret = mix(result, secret);
        secret = prune(secret);
        return secret;
    }

    private long mix(long result, long secret) {
        return result ^ secret;
    }

    private long prune(long secret) {
        return secret % 16_777_216;
    }

}