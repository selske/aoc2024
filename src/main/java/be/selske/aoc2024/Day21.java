package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;
import be.selske.aoc2024.util.map.CardinalDirections;
import be.selske.aoc2024.util.map.Point;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptySet;
import static java.util.Map.entry;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

public class Day21 extends Day {
    public Day21() {
        super(21);
    }

    public static void main(String[] args) {
        new Day21()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("202648")
                .verifyPart2(null);
    }

    /**
     * +---+---+---+     +---+---+
     * | 7 | 8 | 9 |     | ^ | A |
     * +---+---+---+ +---+---+---+
     * | 4 | 5 | 6 | | < | v | > |
     * +---+---+---+ +---+---+---+
     * | 1 | 2 | 3 |
     * +---+---+---+
     * | 0 | A |
     * +---+---+
     */
    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        Map<Point, Character> keyPad = Map.ofEntries(
                entry(new Point(0, 0), '7'),
                entry(new Point(1, 0), '8'),
                entry(new Point(2, 0), '9'),
                entry(new Point(0, 1), '4'),
                entry(new Point(1, 1), '5'),
                entry(new Point(2, 1), '6'),
                entry(new Point(0, 2), '1'),
                entry(new Point(1, 2), '2'),
                entry(new Point(2, 2), '3'),
                entry(new Point(1, 3), '0'),
                entry(new Point(2, 3), 'A')
        );

        Map<Point, Character> controller = Map.ofEntries(
                entry(new Point(1, 0), '^'),
                entry(new Point(2, 0), 'A'),
                entry(new Point(0, 1), '<'),
                entry(new Point(1, 1), 'v'),
                entry(new Point(2, 1), '>')
        );

        int part1 = input.lines()
                .mapToInt(code -> {
                    int numericValue = parseInt(code.substring(0, 3));
                    int moves = part1(code, keyPad, controller).length();
                    return numericValue * moves;
                })
                .sum();
        results.setPart1(part1);

        int part2 = input.lines()
                .mapToInt(code -> {
                    int numericValue = parseInt(code.substring(0, 3));
                    int moves = part2(code, keyPad, controller);
                    return numericValue * moves;
                })
                .sum();
        System.out.println(part2);
    }

    private String part1(String code, Map<Point, Character> keyPad, Map<Point, Character> controller) {
        List<String> solutions = solve(code, new Point(2, 3), keyPad);
        for (int i = 0; i < 2; i++) {
            solutions = robotPass(controller, solutions);
        }

        return solutions.stream().min(Comparator.comparing(String::length)).orElseThrow();
    }

    private int part2(String code, Map<Point, Character> keyPad, Map<Point, Character> controller) {
        List<String> solutions = solve(code, new Point(2, 3), keyPad);
        solutions = robotPass(controller, solutions);
        String solution = solutions.stream().min(Comparator.comparing(String::length)).orElseThrow();

        Map<String, String> mapping = getMapping(controller);

        return IntStream.range(0, solution.length())
                .parallel()
                .map(i -> {
                    System.out.println(i);
                    String move;
                    if (i == 0) {
                        move = "A" + solution.charAt(0);
                    } else {
                        move = solution.substring(i - 1, i + 1);
                    }
                    return getSize(move, mapping, 24);
                })
                .peek(i -> System.out.println("Sum: " + i))
                .sum();
    }

    private int getSize(String move, Map<String, String> mapping, int depth) {
        String newMove = mapping.get(move);
        if (depth == 0) {
            return newMove.length();
        } else {
            int size = 0;
            for (int i = 0; i < newMove.length(); i++) {
                String m;
                if (i == 0) {
                    m = "A" + newMove.charAt(0);
                } else {
                    m = newMove.substring(i - 1, i + 1);
                }
                size += getSize(m, mapping, depth - 1);
            }
            return size;
        }
    }

    private Map<String, String> getMapping(Map<Point, Character> controller) {
        return Stream.of("v", "A", "^", "<", ">")
                .flatMap(k -> Stream.of("v", "A", "^", "<", ">").map(kk -> k + kk))
                .collect(toMap(Function.identity(), keys -> {
                    if (keys.charAt(0) == keys.charAt(1)) {
                        return "A";
                    } else {
                        String solution = robotPass(controller, List.of(keys)).stream().min(Comparator.comparing(String::length)).orElseThrow();
                        String part = solution.substring(solution.indexOf('A') + 1);
                        return part.substring(0, part.indexOf('A') + 1);
                    }
                }));
    }

    private List<String> robotPass(Map<Point, Character> controller, List<String> solutions) {
        solutions = solutions.parallelStream()
                .flatMap(keyPadMove -> Arrays.stream(keyPadMove.split("A"))
                        .map(moves -> moves + "A")
                        .map(moves -> solve(moves, new Point(2, 0), controller))
                        .reduce(List.of(""), (a, b) -> a.stream()
                                .flatMap(aElement -> b.stream().map(bElement -> aElement + bElement))
                                .toList()
                        )
                        .stream()
                )
                .toList();
        int shortest = solutions.stream().min(Comparator.comparing(String::length)).orElseThrow().length();
        return solutions.stream()
                .filter(solution -> solution.length() == shortest)
                .toList();
    }

    private List<String> solve(String code, Point starPoint, Map<Point, Character> keyPad) {
        List<Path> paths = List.of(new Path(code, new ArrayList<>(), emptySet(), starPoint, 0));
        while (true) {
            paths = paths.stream()
                    .flatMap(path -> {
                        if (keyPad.get(path.keyPadPosition) == path.code.charAt(path.targetIndex)) {
                            return Stream.of(path.input());
                        } else {
                            return Stream.of(CardinalDirections.values())
                                    .filter(direction -> keyPad.containsKey(path.keyPadPosition.getNeighbour(direction)))
                                    .filter(not(direction -> path.visited.contains(path.keyPadPosition.getNeighbour(direction))))
                                    .map(path::move);
                        }
                    })
                    .toList();
            int biggestProgress = paths.stream().mapToInt(Path::targetIndex).max().orElseThrow();

            paths = paths.stream().filter(path -> path.targetIndex > biggestProgress - 4).toList();

            List<String> solutions = paths.stream()
                    .filter(path -> path.targetIndex == path.code.length())
                    .map(path -> path.inputs().stream().map(Object::toString).collect(joining()))
                    .toList();
            if (!solutions.isEmpty()) {
                return solutions;
            }
        }
    }

    private static char toChar(CardinalDirections direction) {
        return switch (direction) {
            case UP -> '^';
            case RIGHT -> '>';
            case DOWN -> 'v';
            case LEFT -> '<';
        };
    }

    private record Path(
            String code,
            List<Character> inputs,
            Set<Point> visited,
            Point keyPadPosition,
            int targetIndex
    ) {

        public Path move(CardinalDirections direction) {
            List<Character> newInputs = new ArrayList<>(inputs);
            Character input = toChar(direction);
            newInputs.add(input);
            Set<Point> newVisited = new HashSet<>(visited);
            newVisited.add(keyPadPosition.getNeighbour(direction));
            return new Path(code, newInputs, newVisited, keyPadPosition.getNeighbour(direction), targetIndex);
        }

        public Path input() {
            List<Character> newInputs = new ArrayList<>(inputs);
            newInputs.add('A');
            return new Path(code, newInputs, emptySet(), keyPadPosition, targetIndex + 1);
        }
    }

}