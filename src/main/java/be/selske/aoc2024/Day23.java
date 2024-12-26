package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

public class Day23 extends Day {
    public Day23() {
        super(23);
    }

    public static void main() {
        new Day23()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("1200")
                .verifyPart2("ag,gh,hh,iv,jx,nq,oc,qm,rb,sm,vm,wu,zr");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        Map<String, Set<String>> links = new HashMap<>();
        input.lines()
                .forEach(line -> {
                    String[] parts = line.split("-");
                    String from = parts[0];
                    String to = parts[1];
                    links.computeIfAbsent(from, _ -> new HashSet<>()).add(to);
                    links.computeIfAbsent(to, _ -> new HashSet<>()).add(from);
                });

        List<Set<String>> triangles = links.keySet().stream()
                .flatMap(a -> links.get(a).stream()
                        .flatMap(b -> links.get(b).stream()
                                .filter(c -> links.get(a).contains(c))
                                .map(c -> (Set<String>) new HashSet<>(List.of(a, b, c)))
                        )
                )
                .filter(set -> set.size() == 3)
                .distinct()
                .toList();

        results.setPart1(part1(triangles));
        results.setPart2(part2(links, triangles));
    }

    private static long part1(List<Set<String>> triangles) {
        return triangles.stream()
                .filter(set -> set.stream().anyMatch(e -> e.startsWith("t")))
                .count();
    }

    private String part2(Map<String, Set<String>> links, List<Set<String>> triangles) {
        Map<String, List<Set<String>>> trianglesPerComputer = links.keySet().stream()
                .collect(toMap(Function.identity(), computer -> triangles.stream()
                        .filter(triangle -> triangle.contains(computer))
                        .toList()));
        Map<Integer, List<Entry<String, List<Set<String>>>>> trianglesPerComputerBySize = trianglesPerComputer
                .entrySet().stream()
                .collect(groupingBy(e -> e.getValue().size()));

        return trianglesPerComputerBySize
                .entrySet().stream()
                .max(comparingInt(Entry::getKey))
                .map(cluster -> cluster.getValue().stream()
                        .map(Entry::getKey)
                        .sorted()
                        .collect(Collectors.joining(",")))
                .orElseThrow();
    }

}