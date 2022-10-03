import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Polynomial{
    public double[] coefficients;
    public int[] exponents;
    private int length;

    //#region ctors

    public Polynomial(){
        this.coefficients = new double[]{0};
        this.exponents = new int[]{0};
        this.length = 1;
    }

    public Polynomial(double[] coefficients){
        this.coefficients = Arrays.stream(coefficients).filter(
            c -> c != 0.0
        ).toArray();
        this.exponents = IntStream.range(0, coefficients.length).filter(
            i -> coefficients[i] != 0.0
        ).toArray();
        this.length = this.coefficients.length;
    }

    public Polynomial(double[] coefficients, int[] exponents){
        this(coefficients);
        this.length = Math.min(coefficients.length, exponents.length);
        this.exponents = IntStream.range(0, this.length).filter(
            i -> coefficients[i] != 0.0
        ).map(i -> exponents[i]).toArray();
    }

    private Polynomial(Stream<Map.Entry<Integer, Double>> stream){
        // sum the coefficients with the same exponent
        Map<Integer, Double> map = stream.collect(Collectors.toMap(
            Map.Entry::getKey, Map.Entry::getValue, Double::sum
        ));
        this.exponents = map.keySet().stream().mapToInt(Integer::intValue).toArray();
        this.coefficients = map.values().stream().mapToDouble(Double::doubleValue).toArray();
        this.length = map.size();
    }

    public Polynomial(File f) throws FileNotFoundException{
        this(Arrays.stream(new Scanner(f).nextLine().replaceFirst("^(?!-)", "+")
            .replaceAll("([+-][0-9]+)(?=[-+$])", "$1x0") // e.g. 20 -> 20x0
            .replaceAll("x(?![0-9])", "x1") // e.g. 20x -> 20x1
            .replaceAll("(?<=[-+])x", "1x") // e.g. x5 -> 1x5
            .split("(?=[-+])")
        ).map(s -> s.split("x")).map(split -> Map.entry(
            Integer.parseInt(split[1]), Double.parseDouble(split[0])
        )));
    }
    //#endregion

    private Map<Integer, Double> map(){
        return IntStream.range(0, this.length).boxed().collect(
            Collectors.groupingBy(
                i -> this.exponents[i],
                Collectors.reducing(0.0, i -> this.coefficients[i], Double::sum)
            )
        );
    }

    @Override
    public String toString(){
        return this.map().entrySet().stream().map(
            entry -> {
                Double coefficient = entry.getValue();
                if(coefficient == 0.0) return "";
                Integer exponent = entry.getKey();
                String s = (coefficient > 0 ? "+" : "") + (
                    // use int if possible
                    coefficient % 1 == 0
                        ? Integer.toString(coefficient.intValue())
                        : Double.toString(coefficient)
                );
                return switch(exponent){
                    case 0 -> s;
                    case 1 -> s + 'x';
                    default -> s + 'x' + exponent;
                };
            }
        ).collect(Collectors.joining()).replaceFirst("^\\+", "");
    }

    public Polynomial add(Polynomial rhs){
        return new Polynomial(Stream.of(this.map(), rhs.map()).flatMap(
            map -> map.entrySet().stream()
        ));
    }

    public Polynomial multiply(Polynomial rhs){
        Set<Map.Entry<Integer, Double>> entries = rhs.map().entrySet();
        // (a0 + a1*x + a2*x^2 + ...) * (b0 + b1*x + b2*x^2 + ...)
        return new Polynomial(this.map().entrySet().stream().flatMap(
            e1 -> entries.stream().map(e2 -> Map.entry(
                // a1 * b1 * x ^ (a1+b1)
                e1.getKey() + e2.getKey(), e1.getValue() * e2.getValue()
            ))
        ));
    }

    public double evaluate(double x){
        // imagine using arrays smh
        // return IntStream.range(0, this.length).mapToDouble(
        //     i -> this.coefficients[i] * Math.pow(x, this.exponents[i])
        // ).sum();
        return this.map().entrySet().stream().mapToDouble(
            entry -> entry.getValue() * Math.pow(x, entry.getKey())
        ).sum();
    }

    public boolean hasRoot(double x){
        return this.evaluate(x) == 0;
    }

    public void saveToFile(String file) throws FileNotFoundException{
        try(PrintStream ps = new PrintStream(file)){
            ps.print(this);
        }
    }
}
