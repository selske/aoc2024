package be.selske.aoc2024.benchmark;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public abstract class Day {

    private final String exampleInput;
    private final String input;
    private boolean executeExample = false;
    private boolean executePuzzle = false;
    private final String exampleParam;
    private final String puzzleParam;

    public Day(int day) {
        this(day, null, null);
    }

    public Day(int day, String exampleParam, String puzzleParam) {
        this.exampleParam = exampleParam;
        this.puzzleParam = puzzleParam;
        try (InputStream exampleStream = Day.class.getResourceAsStream("/day" + day + "_example.txt");
             InputStream inputStream = Day.class.getResourceAsStream("/day" + day + ".txt")
        ) {
            this.exampleInput = new String(exampleStream.readAllBytes());
            this.input = new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void solve(ResultContainer results, String input, String parameter);

    public Day example() {
        this.executeExample = true;
        return this;
    }

    public ResultContainer solve() {
        var results = new ResultContainer();
        solve(results, input, puzzleParam);
        return results;
    }

    public Day puzzle() {
        this.executePuzzle = true;
        return this;
    }

    public Result benchmark() {
        if (executeExample) {
            var results = new ResultContainer();
            solve(results, exampleInput, exampleParam);
            System.out.println("Example 1: " + results.part1);
            System.out.println("Example 2: " + results.part2);
            System.out.println();
        }
        if (executePuzzle) {
            var results = new ResultContainer();
            var before = System.nanoTime();
            solve(results, input, puzzleParam);

            var after = System.nanoTime();

            var hot = after - before;

            System.out.println("Part 1: " + results.part1);
            System.out.println("Part 2: " + results.part2);
            System.out.println();
            if (executeExample) {
                System.out.println("Hot time:  " + hot / 1_000_000. + "ms");
            } else {
                System.out.println("Cold time:  " + hot / 1_000_000. + "ms");
            }

            return new Result(results.part1 == null ? null : results.part1.toString(), results.part2 == null ? null : results.part2.toString(), hot);
        } else {
            return new Result(null, null, -1);
        }
    }

    public record Result(String part1, String part2, long duration) {

        public Result verifyPart1(String expected) {
            if (expected == null) {
                return this;
            }
            if (!Objects.equals(part1, expected)) {
                throw new IllegalStateException("Expected " + expected + " for part 1, but was: " + part1);
            }
            return this;
        }

        public Result verifyPart2(String expected) {
            if (expected == null) {
                return this;
            }
            if (!Objects.equals(part2, expected)) {
                throw new IllegalStateException("Expected " + expected + " for part 2, but was: " + part2);
            }
            return this;
        }
    }

    public static class ResultContainer {
        private Object part1;
        private Object part2;

        public void setPart1(Object part1) {
            this.part1 = part1;
        }

        public void setPart2(Object part2) {
            this.part2 = part2;
        }
    }
}
