import java.io.*;

public class Driver{
    public static void main(String[] args) throws IOException{
        Polynomial p = new Polynomial();
        System.out.println(p.evaluate(3));
        Polynomial p1 = new Polynomial(new double[]{6, 0, 0, 5});
        Polynomial p2 = new Polynomial(new double[]{0, -2, 0, 0, -9});
        Polynomial s = p1.add(p2);
        System.out.println("s(0.1) = " + s.evaluate(0.1));
        System.out.println("1 is " + (s.hasRoot(1) ? "a" : "not a") + " root of s");
        // -12x-64x4-45x7
        System.out.println("p1 * p2 = " + p1.multiply(p2));

        Polynomial q1 = new Polynomial(new double[]{6, -2, 5}, new int[]{0, 1, 3});
        Polynomial q2 = new Polynomial(new double[]{5, -3, 7}, new int[]{0, 2, 8});
        // supposedly to be the same as q2
        Polynomial qq2 = new Polynomial(new File("poly"));

        // 11-2x-3x2+5x3+7x8
        System.out.println("q1 + q2 = " + q1.add(q2));
        q1.add(qq2).saveToFile("sum");
        System.out.println("q1 + qq2= " + new Polynomial(new File("sum")));

        // 30-10x-18x2+31x3-15x5+42x8-14x9+35x11
        System.out.println("q1 * q2 = " + q1.multiply(q2));
        q1.multiply(qq2).saveToFile("prod");
        System.out.println("q1 * qq2= " + new Polynomial(new File("prod")));
    }
}
