package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class Day24 extends Day {
    public Day24() {
        super(24);
    }

    public static void main() {
        new Day24()
                .example()
                .puzzle()
                .benchmark(false)
                .verifyPart1("66055249060558")
                .verifyPart2("fcd,fhp,hmk,rvf,tpc,z16,z20,z33");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        List<String> lines = input.lines().toList();

        Map<String, Wire> wires = new HashMap<>();
        int i;
        for (i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) break;
            String[] parts = line.split(": ");
            String name = parts[0];
            byte value = Byte.parseByte(parts[1]);
            Wire wire = new Wire(name);
            wire.setValue(value);
            wires.put(name, wire);
        }
        i++;
        List<Gate> andGates = new ArrayList<>();
        List<Gate> orGates = new ArrayList<>();
        List<Gate> xorGates = new ArrayList<>();
        for (; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(" ");
            Wire a = wires.computeIfAbsent(parts[0], Wire::new);
            String gate = parts[1];
            Wire b = wires.computeIfAbsent(parts[2], Wire::new);
            Wire output = wires.computeIfAbsent(parts[4], Wire::new);
            switch (gate) {
                case "AND" -> andGates.add(new AndGate(a, b, output));
                case "OR" -> orGates.add(new OrGate(a, b, output));
                case "XOR" -> xorGates.add(new XorGate(a, b, output));
                default -> throw new IllegalArgumentException("Unexpected gate: " + gate);
            }
        }

        results.setPart1(part1(wires));
        results.setPart2(part2(xorGates, andGates, orGates));
    }

    private String part2(List<Gate> xorGates, List<Gate> andGates, List<Gate> orGates) {
        List<String> swaps = new ArrayList<>();
        AtomicReference<String> previousCarry = new AtomicReference<>("");
        for (int a = 1; a < 44; a++) {
            String x = "x%02d".formatted(a);
            String y = "y%02d".formatted(a);
            String z = "z%02d".formatted(a);
            Optional<Gate> aXorB = xorGates.stream()
                    .filter(involvesWire(x))
                    .filter(involvesWire(y))
                    .findFirst();
            if (aXorB.isPresent()) {
                Optional<Gate> xorOutput = xorGates.stream()
                        .filter(involvesWire(aXorB.get().output.name()))
                        .findFirst();
                if (xorOutput.isEmpty()) {
                    xorMismatch(xorGates, previousCarry.get(), swaps, aXorB.get());
                } else {
                    String carry = xorOutput.map(otherInput(aXorB.get().output.name()))
                            .map(Wire::name)
                            .orElseThrow();

                    andGates.stream()
                            .filter(involvesWire(carry))
                            .filter(involvesWire(aXorB.get().output.name()))
                            .findFirst()
                            .ifPresent(gate -> andGates.stream()
                                    .filter(involvesWire(x))
                                    .filter(involvesWire(y))
                                    .findFirst()
                                    .ifPresent(xAndY -> {
                                        Optional<Gate> or = orGates.stream()
                                                .filter(involvesWire(xAndY.output.name))
                                                .filter(involvesWire(gate.output.name))
                                                .findFirst();
                                        if (or.isEmpty()) {
                                            orMismatch(orGates, xAndY, gate, swaps);
                                        } else {
                                            previousCarry.set(or.get().output.name);
                                            xorGates.stream()
                                                    .filter(involvesWire(aXorB.get().output.name))
                                                    .filter(involvesWire(carry))
                                                    .findFirst()
                                                    .ifPresent(output -> checkOutput(z, output, swaps));
                                        }
                                    }));
                }
            }

        }

        return swaps.stream().sorted().collect(Collectors.joining(","));
    }

    private static void checkOutput(String z, Gate output, List<String> swaps) {
        if (!output.output.name.equals(z)) {
            swaps.add(output.output.name);
            swaps.add(z);
        }
    }

    private static void xorMismatch(List<Gate> xorGates, String previousCarry, List<String> swaps, Gate aXorB) {
        xorGates.stream()
                .filter(involvesWire(previousCarry))
                .map(otherInput(previousCarry))
                .findFirst()
                .ifPresent(swap -> {
                    swaps.add(swap.name);
                    swaps.add(aXorB.output.name);
                });
    }

    private static void orMismatch(List<Gate> orGates, Gate xAndY, Gate carryAndXXorY, List<String> swaps) {
        Optional<Gate> xAndYOr = orGates.stream()
                .filter(involvesWire(xAndY.output.name))
                .findFirst();

        Optional<Gate> carryAndXXorYOr = orGates.stream()
                .filter(involvesWire(carryAndXXorY.output.name))
                .findFirst();

        if (xAndYOr.isPresent()) {
            swaps.add(carryAndXXorY.output.name);
            swaps.add(xAndYOr.map(otherInput(xAndY.output.name)).orElseThrow().name);
        } else if (carryAndXXorYOr.isPresent()) {
            swaps.add(xAndY.output.name);
            swaps.add(carryAndXXorYOr.map(otherInput(carryAndXXorYOr.get().output.name)).orElseThrow().name);
        } else {
            throw new IllegalStateException();
        }
    }

    private static Function<Gate, Wire> otherInput(String output) {
        return xor -> {
            if (xor.a.name.equals(output)) {
                return xor.b;
            } else {
                return xor.a;
            }
        };
    }

    private static long part1(Map<String, Wire> wires) {
        return wires.values().stream()
                .filter(wire -> wire.name.startsWith("z"))
                .mapToLong(wire -> ((long) wire.value) << parseInt(wire.name.substring(1)))
                .sum();
    }

    private static Predicate<Gate> involvesWire(String wire) {
        return gate -> gate.a.name.equals(wire) || gate.b.name.equals(wire);
    }

    private class Wire {
        private final String name;
        private Byte value;
        private final List<Gate> outputGates = new ArrayList<>();

        private Wire(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }

        public Byte value() {
            return value;
        }

        public void setValue(byte value) {
            this.value = value;
            outputGates.forEach(Gate::tryOperate);
        }

        public void addOutputGate(Gate outputGate) {
            this.outputGates.add(outputGate);
            outputGates.forEach(Gate::tryOperate);
        }
    }

    private abstract class Gate {
        protected final Wire a;
        protected final Wire b;
        protected final Wire output;

        private Gate(Wire a, Wire b, Wire output) {
            this.a = a;
            this.b = b;
            this.output = output;
            a.addOutputGate(this);
            b.addOutputGate(this);
            output.addOutputGate(this);
            tryOperate();
        }

        private void tryOperate() {
            if (a.value() != null && b.value() != null && output.value() == null) {
                operate();
            }
        }

        protected abstract void operate();

    }

    private class AndGate extends Gate {

        private AndGate(Wire a, Wire b, Wire output) {
            super(a, b, output);
        }

        protected void operate() {
            output.setValue((byte) (a.value() & b.value()));
        }

    }

    private class OrGate extends Gate {

        private OrGate(Wire a, Wire b, Wire output) {
            super(a, b, output);
        }

        protected void operate() {
            output.setValue((byte) (a.value | b.value));
        }

    }

    private class XorGate extends Gate {

        private XorGate(Wire a, Wire b, Wire output) {
            super(a, b, output);
        }

        protected void operate() {
            output.setValue((byte) (a.value ^ b.value));
        }

    }

}