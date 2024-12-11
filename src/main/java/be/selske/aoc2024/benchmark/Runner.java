package be.selske.aoc2024.benchmark;

public class Runner {

    public static void main(String[] args) throws Exception {
        for (String arg : args) {
            int day = Integer.parseInt(arg);
            System.out.println("Day " + day);

            Day dayToRun = (Day) Runner.class.getClassLoader().loadClass("be.selske.aoc2024.Day%02d".formatted(day)).newInstance();

            dayToRun.example()
                    .puzzle()
                    .benchmark();

            System.out.println();
        }
    }
}
