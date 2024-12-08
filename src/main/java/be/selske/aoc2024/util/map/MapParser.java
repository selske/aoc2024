package be.selske.aoc2024.util.map;

import java.util.List;
import java.util.function.BiConsumer;

public class MapParser {

    public static MapSize parse(String input, BiConsumer<Point, Character> consumer) {
        List<String> lines = input.lines().toList();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Point point = new Point(x, y);
                consumer.accept(point, c);
            }
        }
        return new MapSize(lines.getFirst().length(), lines.size());
    }

}
