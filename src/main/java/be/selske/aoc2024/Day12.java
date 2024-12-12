package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;
import be.selske.aoc2024.util.map.CardinalDirections;
import be.selske.aoc2024.util.map.MapParser;
import be.selske.aoc2024.util.map.Point;

import java.util.*;
import java.util.stream.Stream;

import static be.selske.aoc2024.util.map.CardinalDirections.LEFT;
import static be.selske.aoc2024.util.map.CardinalDirections.RIGHT;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;

public class Day12 extends Day {

    public Day12() {
        super(12);
    }

    public static void main(String[] args) {
        new Day12()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("1359028")
                .verifyPart2("839780");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        Map<Point, Character> map = new HashMap<>();
        Map<Character, List<Set<Point>>> regions = new HashMap<>();
        MapParser.parse(input, (point, c) -> {
            map.put(point, c);
            List<Set<Point>> regionsList = regions.computeIfAbsent(c, _ -> new ArrayList<>());
            List<Set<Point>> matchingRegions = Stream.of(LEFT, CardinalDirections.UP)
                    .map(point::getNeighbour)
                    .filter(neighbour -> {
                        Character neighbourValue = map.get(neighbour);
                        return neighbourValue != null && neighbourValue == c;
                    })
                    .flatMap(neighbour -> regionsList.stream()
                            .filter(region -> region.contains(neighbour))
                            .findAny().stream())
                    .distinct()
                    .toList();
            matchingRegions.forEach(region -> region.add(point));
            if (matchingRegions.isEmpty()) {
                regionsList.add(new HashSet<>(Set.of(point)));
            } else if (matchingRegions.size() == 2) {
                regionsList.remove(matchingRegions.getLast());
                matchingRegions.getFirst().addAll(matchingRegions.getLast());
            }
        });

        results.setPart1(getPart1(regions));
        results.setPart2(getPart2(regions));
    }

    private static long getPart1(Map<Character, List<Set<Point>>> regions) {
        return regions.values().parallelStream()
                .flatMap(Collection::stream)
                .mapToLong(region -> {
                    long area = region.size();
                    long perimeter = perimeter(region);
                    return area * perimeter;
                })
                .sum();
    }

    private static long perimeter(Set<Point> region) {
        return region.stream()
                .mapToLong(garden -> Arrays.stream(CardinalDirections.values())
                        .map(garden::getNeighbour)
                        .filter(not(region::contains))
                        .count()
                )
                .sum();
    }

    private static long getPart2(Map<Character, List<Set<Point>>> regions) {
        return regions.values().parallelStream()
                .flatMap(Collection::stream)
                .mapToLong(region -> {
                    long area = region.size();
                    long sides = sides(region);
                    return area * sides;
                })
                .sum();
    }

    private static long sides(Set<Point> region) {
        long verticalSides = Stream.of(LEFT, RIGHT)
                .mapToLong(direction -> {
                    Map<Integer, List<Point>> verticalNeighboursPerColumn = region.stream()
                            .map(direction::getNeighbour)
                            .filter(not(region::contains))
                            .collect(groupingBy(Point::x));

                    return verticalNeighboursPerColumn.values().stream()
                            .mapToLong(neighbours -> {
                                neighbours.sort(Comparator.comparing(Point::y));

                                int previous = neighbours.getFirst().y();
                                int sideCount = 1;
                                for (int i = 1; i < neighbours.size(); i++) {
                                    int current = neighbours.get(i).y();
                                    if (current > previous + 1) {
                                        sideCount++;
                                    }
                                    previous = current;
                                }
                                return sideCount;
                            })
                            .sum();
                })
                .sum();

        return verticalSides * 2;
    }

}