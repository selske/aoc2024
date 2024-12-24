package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.*;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;

public class Day19 extends Day {
    public Day19() {
        super(19);
    }

    public static void main(String[] args) {
        new Day19()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("251")
                .verifyPart2("616957151871345");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        List<String> lines = input.lines().toList();

        Map<Character, List<String>> patterns = Arrays.stream(lines.getFirst().split(", +"))
                .collect(groupingBy(pattern -> pattern.charAt(0)));

        HashSet<String> notCreatable = new HashSet<>();
        results.setPart1(part1(lines, patterns, notCreatable));
        results.setPart2(part2(lines, patterns, notCreatable));
    }

    private static long part1(List<String> lines, Map<Character, List<String>> patterns, Set<String> notCreatable) {
        return lines.stream()
                .skip(2)
                .filter(design -> part1(design, patterns, notCreatable))
                .count();
    }

    private static boolean part1(String design, Map<Character, List<String>> patterns, Set<String> notCreatable) {
        if (notCreatable.contains(design)) {
            return false;
        }
        if (design.isEmpty()) {
            return true;
        }
        char c = design.charAt(0);
        List<String> availablePatterns = patterns.getOrDefault(c, Collections.emptyList());
        boolean creatable = availablePatterns.stream()
                .filter(design::startsWith)
                .anyMatch(pattern -> part1(design.substring(pattern.length()), patterns, notCreatable));
        if (!creatable) {
            notCreatable.add(design);
        }
        return creatable;
    }

    private long part2(List<String> lines, Map<Character, List<String>> patterns, HashSet<String> notCreatable) {
        Map<String, Long> combinations = new HashMap<>();
        return lines.stream()
                .skip(2)
                .filter(not(notCreatable::contains))
                .mapToLong(design -> part2(design, patterns, combinations))
                .sum();
    }

    private static long part2(String design, Map<Character, List<String>> patterns, Map<String, Long> combinations) {
        if (design.isEmpty()) {
            return 1;
        }
        if (combinations.containsKey(design)) {
            return combinations.get(design);
        }

        List<String> availablePatterns = patterns.getOrDefault(design.charAt(0), Collections.emptyList());
        long numberOfCombinations = availablePatterns.stream()
                .filter(design::startsWith)
                .mapToLong(pattern -> part2(design.substring(pattern.length()), patterns, combinations))
                .sum();
        combinations.put(design, numberOfCombinations);
        return numberOfCombinations;
    }

}