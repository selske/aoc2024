package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.getNumericValue;

public class Day09 extends Day {

    public Day09() {
        super(9);
    }

    public static void main(String[] args) {
        new Day09()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("6421128769094")
                .verifyPart2("6448168620520");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        results.setPart1(part1(input));
        results.setPart2(part2(input + "0"));
    }

    private static long part1(String input) {
        int last = input.length() / 2 + 1;

        int i = 0;
        long headValue = 0;
        int head = 0;
        int tail = input.length();
        long sum = 0;
        long remainingTailValues = 0;
        long tailValue = last;
        while (head < tail) {
            int numberOfBlocks = getNumericValue(input.charAt(head));
            for (int block = 0; block < numberOfBlocks; block++) {
                sum += i++ * headValue;
            }
            headValue++;
            head++;
            int numberOfSpaces = getNumericValue(input.charAt(head));
            for (int space = 0; space < numberOfSpaces; space++) {
                if (remainingTailValues == 0) {
                    tail--;
                    remainingTailValues = getNumericValue(input.charAt(tail));
                    tailValue--;
                    tail--;
                }
                sum += i++ * tailValue;
                remainingTailValues--;
            }
            head++;
        }
        for (long r = 0; r < remainingTailValues; r++) {
            sum += i++ * headValue;
        }
        return sum;
    }

    private static long part2(String input) {
        List<Block> disk = new ArrayList<>();
        List<Gap> gaps = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < input.length(); i += 2) {
            int fileSize = getNumericValue(input.charAt(i));
            disk.add(new Block(index, fileSize, i / 2));
            index += fileSize;

            int freeSize = getNumericValue(input.charAt(i + 1));
            gaps.add(new Gap(index, freeSize));
            index += freeSize;
        }

        long sum = 0;
        loop:
        for (int i = disk.size() - 1; i >= 0; i--) {
            Block block = disk.get(i);
            for (int j = 0; j < gaps.size(); j++) {
                Gap gap = gaps.get(j);
                if (block.index() < gap.index()) {
                    break;
                }
                if (gap.size() >= block.size()) {
                    gaps.remove(j);
                    if (gap.size() > block.size()) {
                        gaps.add(j, new Gap(gap.index() + block.size(), gap.size() - block.size()));
                    }
                    for (long l = 0; l < block.size(); l++) {
                        sum += (gap.index() + l) * block.value();
                    }
                    continue loop;
                }
            }
            for (long j = 0; j < block.size(); j++) {
                sum += (block.index() + j) * block.value();
            }
        }

        return sum;
    }

    private record Gap(int index, int size) {}

    private record Block(int index, int size, int value) {}

}