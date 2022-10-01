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
    private final int length;

    //#region ctors

    public Polynomial(){
        this.coefficients = new double[]{0};
        this.exponents = new int[]{0};
        this.length = 1;
    }

    public Polynomial(double[] coefficients){
        this.coefficients = coefficients;
        this.exponents = IntStream.range(0, coefficients.length).toArray();
        this.length = coefficients.length;
    }

    public Polynomial(double[] coefficients, int[] exponents){
        this.coefficients = coefficients;
        this.exponents = exponents;
        this.length = Math.min(coefficients.length, exponents.length);
    }

    public Polynomial(Stream<Map.Entry<Integer, Double>> stream){
        Map<Integer, Double> map = stream.collect(Collectors.toMap(
            Map.Entry::getKey, Map.Entry::getValue, Double::sum
        ));
        this.exponents = map.keySet().stream().mapToInt(Integer::intValue).toArray();
        this.coefficients = map.values().stream().mapToDouble(Double::doubleValue).toArray();
        this.length = map.size();
    }

    public Polynomial(File f) throws FileNotFoundException{
        this(Arrays.stream(new Scanner(f).nextLine().replaceFirst("^(?!-)", "+")
            .replaceAll("([+-][0-9]+)(?=[-+$])", "$1x0")
            .replaceAll("x(?![0-9])", "x1")
            .replaceAll("(?<=[-+])x", "1x").split("(?=[-+])")
        ).map(s -> s.split("x")).map(
            split -> Map.entry(Integer.parseInt(split[1]), Double.parseDouble(split[0]))
        ));
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
        return this.map().toString();
    }

    public Polynomial add(Polynomial rhs){
        return new Polynomial(Stream.of(this.map(), rhs.map()).flatMap(
            map -> map.entrySet().stream()
        ));
    }

    public Polynomial multiply(Polynomial rhs){
        Set<Map.Entry<Integer, Double>> entries = rhs.map().entrySet();
        return new Polynomial(this.map().entrySet().stream().flatMap(
            e1 -> entries.stream().map(e2 -> Map.entry(
                e1.getKey() + e2.getKey(), e1.getValue() * e2.getValue()
            ))
        ));
    }

    public double evaluate(double x){
        return IntStream.range(0, this.length).mapToDouble(
            i -> this.coefficients[i] * Math.pow(x, this.exponents[i])
        ).sum();
    }

    public boolean hasRoot(double x){
        return this.evaluate(x) == 0;
    }

    public void saveToFile(String file) throws FileNotFoundException{
        try(PrintStream ps = new PrintStream(file)){
            ps.print(this.map().entrySet().stream().map(
                entry -> {
                    Double c = entry.getValue();
                    if(c == 0.0) return "";
                    String coefficient = c % 1 == 0 ? Integer.toString(c.intValue()) : Double.toString(c);
                    Integer exponent = entry.getKey();
                    String prefix = c > 0 ? "+" : "";
                    if(exponent == 0) return prefix + coefficient;
                    if(exponent == 1) return prefix + coefficient + 'x';
                    return prefix + coefficient + 'x' + exponent;
                }
            ).collect(Collectors.joining()).replaceFirst("^\\+", ""));
        }
    }
}
