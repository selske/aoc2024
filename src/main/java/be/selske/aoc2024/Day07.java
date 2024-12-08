package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;

public class Day07 extends Day {


    public Day07() {
        super(7);
    }

    public static void main(String[] args) {
        new Day07()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("465126289353")
                .verifyPart2("70597497486371");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        List<Equation> equations = input.lines()
                .map(line -> {
                    String[] parts = line.split(": ");
                    return new Equation(parseLong(parts[0]), Arrays.stream(parts[1].split(" ")).map(value -> new Constant(parseLong(value))).toArray(Constant[]::new));
                })
                .toList();

        results.setPart1(solve(equations, part1()));
        results.setPart2(solve(equations, part2()));
    }

    private static long solve(List<Equation> equations, BiFunction<Result, Result, Stream<Result>> expander) {
        return equations.parallelStream()
                .mapToLong(equation -> {
                    List<? extends Result> results = List.of(equation.numbers()[0]);

                    for (int i = 1; i < equation.numbers().length; i++) {
                        Result number = equation.numbers()[i];
                        results = results.stream()
                                .flatMap(value -> expander.apply(value, number))
                                .toList();
                    }

                    boolean solvable = results.stream().anyMatch(value -> value.result() == equation.result());
                    if (solvable) {
                        return equation.result();
                    } else {
                        return 0;
                    }
                })
                .sum();
    }

    private static BiFunction<Result, Result, Stream<Result>> part1() {
        return (result, number) -> Stream.of(
                new Multiplication(result, number),
                new Addition(result, number)
        );
    }

    private static BiFunction<Result, Result, Stream<Result>> part2() {
        return (result, number) -> Stream.of(
                new Multiplication(result, number),
                new Addition(result, number),
                new Concatenation(result, number)
        );
    }

    private record Equation(long result, Constant[] numbers) {

    }

    private interface Result {

        long result();

    }

    private record Multiplication(Result a, Result b) implements Result {

        @Override
        public long result() {
            return a.result() * b.result();
        }

    }

    private record Addition(Result a, Result b) implements Result {

        @Override
        public long result() {
            return a.result() + b.result();
        }

    }

    private record Concatenation(Result a, Result b) implements Result {

        @Override
        public long result() {
            long aResult = a.result();
            long bResult = b.result();

            return aResult * (long) pow(10, floor(log10(bResult)) + 1) + bResult;
        }

    }


    private record Constant(long result) implements Result {
    }

}