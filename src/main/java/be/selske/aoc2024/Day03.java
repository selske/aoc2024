package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class Day03 extends Day {

    public Day03() {
        super(3);
    }

    public static void main(String[] args) {
        new Day03()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("189527826")
                .verifyPart2("63013756");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        Matcher matcher = Pattern.compile("mul\\((?<a>\\d{1,3}),(?<b>\\d{1,3})\\)|(?<do>do\\(\\))|(?<dont>don't\\(\\))").matcher(input);

        int part1 = 0;
        int part2 = 0;
        boolean mulEnabled = true;
        while(matcher.find()) {
            if (matcher.start("do") != -1) {
                mulEnabled = true;
            } else if (matcher.start("dont") != -1) {
                mulEnabled = false;
            } else {
                int result = parseInt(matcher.group("a")) * parseInt(matcher.group("b"));
                part1 += result;
                if (mulEnabled) {
                    part2 += result;
                }
            }
        }
        results.setPart1(part1);
        results.setPart2(part2);
    }

}