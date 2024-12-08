package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import static java.lang.Integer.parseInt;
import java.util.*;

public class Day05 extends Day {

    public Day05() {
        super(5);
    }

    public static void main(String[] args) {
        new Day05()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("5651")
                .verifyPart2("4743");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        List<String> lines = input.lines().toList();
        Set<ForcedOrder> forcedOrders = new HashSet<>();
        for (String line : lines) {
            if (line.isBlank()) {
                break;
            }
            String[] parts = line.split("\\|");
            forcedOrders.add(new ForcedOrder(parseInt(parts[0]), parseInt(parts[1])));
        }

        List<List<Integer>> updates = lines.stream()
                .skip(forcedOrders.size() + 1L)
                .map(line -> Arrays.stream(line.split(",")).map(Integer::parseInt).toList())
                .toList();

        List<List<Integer>> correctlyOrderedUpdates = new ArrayList<>();
        List<List<Integer>> incorrectlyOrderedUpdates = new ArrayList<>();
        updates.forEach(update -> {
            if (isInCorrectOrder(update, forcedOrders)) {
                correctlyOrderedUpdates.add(update);
            } else {
                incorrectlyOrderedUpdates.add(update);
            }
        });

        long part1 = correctlyOrderedUpdates.stream()
                .mapToInt(this::getMiddle)
                .sum();
        results.setPart1(part1);

        long part2 = incorrectlyOrderedUpdates.stream()
                .map(update -> update.stream()
                        .sorted(byForcedOrder(forcedOrders))
                        .toList())
                .mapToInt(this::getMiddle)
                .sum();
        results.setPart2(part2);
    }

    private Comparator<Integer> byForcedOrder(Set<ForcedOrder> forcedOrders) {
        return (o1, o2) -> {
            if (forcedOrders.contains(new ForcedOrder(o1, o2))) {
                return -1;
            } else if (forcedOrders.contains(new ForcedOrder(o2, o1))) {
                return 1;
            } else {
                return 0;
            }
        };
    }

    private Integer getMiddle(List<Integer> update) {
        return update.get(update.size() / 2);
    }

    private boolean isInCorrectOrder(List<Integer> update, Collection<ForcedOrder> forcedOrders) {
        Set<Integer> seenUpdates = new HashSet<>();
        for (int page : update) {
            if (forcedOrders.stream()
                    .filter(forcedOrder -> forcedOrder.first == page)
                    .anyMatch(forcedOrder -> seenUpdates.contains(forcedOrder.second))) {
                return false;
            }
            seenUpdates.add(page);
        }
        return true;
    }

    private record ForcedOrder(int first, int second) {

    }

}