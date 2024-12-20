package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;

import static java.lang.Integer.parseInt;
import static java.lang.Math.pow;
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
//                .example()
                .puzzle()
                .benchmark(false)
                .verifyPart1("7,3,5,7,5,7,4,3,0")
                .verifyPart2(null); // too low 105694709871369
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

        String part1 = part1(instructions, Arrays.copyOf(register, register.length));
        results.setPart1(part1);
        long part2 = part2(instructions, Arrays.copyOf(register, register.length));
        results.setPart2(part2);
    }

    private String part1(int[] instructions, long[] register) {
        List<Integer> outputs = run(instructions, register);
        return outputs.stream().map(Object::toString).collect(joining(","));
    }

    private long part2(int[] instructions, long[] initialRegister) {
        Random random = new Random();
//        return LongStream.generate(() -> random.nextLong((long) pow(8, 12)) + (3 * (long) pow(8, 15)))

        long result = 3 * pow(8, 15);
        for (int n = 14; n >= 0; n--) {
            System.out.println(n);
            int finalN = n;
            long finalResult = result;
            LongStream.rangeClosed(0, 7)
                    .filter(i -> {
                        long[] register = Arrays.copyOf(initialRegister, initialRegister.length);
                        register[A] = i * pow(8, finalN) + finalResult;
                        List<Integer> output = run(instructions, register);
                        System.out.println(output + " " + output.size());
                        return output.get(finalN) == instructions[finalN];
                    }).toArray();
            long f = LongStream.rangeClosed(0, 7)
                    .filter(i -> {
                        long[] register = Arrays.copyOf(initialRegister, initialRegister.length);
                        register[A] = i * pow(8, finalN) + finalResult;
                        List<Integer> output = run(instructions, register);
//                        System.out.println(output + " " + output.size());
                        return output.get(finalN) == instructions[finalN];
                    })
                    .findFirst().orElseThrow();
//            result += f * pow(8, n);
        }
        return result;
    }

    private long pow(long a, long b) {
        return (long) Math.pow(a, b);
    }

    private int part2x(int[] instructions, long[] initialRegister) {
        int i = 0;
        trying:
        while (true) {
            System.out.println(i);
            long[] register = Arrays.copyOf(initialRegister, initialRegister.length);
            register[A] = i;
            List<Integer> outputs = run(instructions, register);

            if (instructions.length == outputs.size()) {
                for (int j = 0; j < instructions.length; j++) {
                    if (instructions[j] != outputs.get(j)) {
                        i++;
                        continue trying;
                    }
                }
                return i;
            }
            i++;
        }
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
                case 5 -> outputs.add((int) (comboOperand(register, operand) % 8));
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
        register[target] = result; //& 0xFFFFFFFFL;
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