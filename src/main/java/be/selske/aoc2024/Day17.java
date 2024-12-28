package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.joining;

public class Day17 extends Day {

    public static final int A = 0;
    public static final int B = 1;
    public static final int C = 2;

    public Day17() {
        super(17);
    }

    public static void main(String[] args) {
        new Day17()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("7,3,5,7,5,7,4,3,0")
                .verifyPart2("105734774294938");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        List<String> lines = input.lines().toList();
        long[] register = new long[]{
                parseInt(lines.get(0).split(": ")[1]),
                parseInt(lines.get(1).split(": ")[1]),
                parseInt(lines.get(2).split(": ")[1]),
        };

        int[] instructions = Arrays.stream(lines.get(4).split(": ")[1].split(",")).mapToInt(Integer::parseInt).toArray();

        results.setPart1(part1(instructions, register));
        results.setPart2(part2(instructions));
    }

    private String part1(int[] instructions, long[] register) {
        List<Integer> outputs = run(instructions, register);
        return outputs.stream().map(Object::toString).collect(joining(","));
    }

    private long part2(int[] instructions) {
        return find(instructions, new int[instructions.length], instructions.length - 1);
    }

    private long find(int[] instructions, int[] factors, int p) {
        long start = 0;
        for (int i = 0; i < factors.length; i++) {
            start += factors[i] * pow(8, i);
        }
        if (p == -1) {
            return start;
        }
        for (int i = 0; i < 8; i++) {
            long a = start + pow(8, p) * i;
            List<Integer> output = run(instructions, new long[]{a, 0, 0});
            if (output.size() == 16 && output.get(p) == instructions[p]) {
                int[] newFactors = Arrays.copyOf(factors, factors.length);
                newFactors[p] = i;
                long result = find(instructions, newFactors, p - 1);
                if (result > 0) {
                    return result;
                }
            }
        }
        return -1;
    }

    private long pow(long a, long b) {
        return (long) Math.pow(a, b);
    }

    private List<Integer> run(int[] instructions, long[] register) {
        List<Integer> outputs = new ArrayList<>();

        int instructionPointer = 0;
        while (instructionPointer < instructions.length) {
            int instruction = instructions[instructionPointer++];
            int operand = instructions[instructionPointer++];

            switch (instruction) {
                case 0 -> divide(A, register, operand);
                case 1 -> register[B] = register[B] ^ operand;
                case 2 -> register[B] = comboOperand(register, operand) % 8;
                case 3 -> {
                    if (register[A] != 0) {
                        instructionPointer = operand;
                    }
                }
                case 4 -> register[B] = register[B] ^ register[C];
                case 5 -> {
                    outputs.add((int) (comboOperand(register, operand) % 8));
                }
                case 6 -> divide(B, register, operand);
                case 7 -> divide(C, register, operand);
            }
        }
        return outputs;
    }

    private void divide(int target, long[] register, int operand) {
        long numerator = register[A];
        double denominator = pow(2, comboOperand(register, operand));
        long result = (long) (numerator / denominator);
        register[target] = result;
    }

    private long comboOperand(long[] register, int operand) {
        return switch (operand) {
            case 0, 1, 2, 3 -> operand;
            case 4 -> register[A];
            case 5 -> register[B];
            case 6 -> register[C];
            default -> throw new IllegalArgumentException("Invalid operand: " + operand);
        };
    }

}