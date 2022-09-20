public class Polynomial {
    double[] coefficients;

    public Polynomial() {
        this.coefficients = new double[]{0};
    }

    public Polynomial(double[] coefficients) {
        this.coefficients = coefficients;
    }

    public Polynomial add(Polynomial rhs) {
        double[] longer, shorter;
        if (this.coefficients.length > rhs.coefficients.length) {
            longer = this.coefficients;
            shorter = rhs.coefficients;
        } else {
            shorter = this.coefficients;
            longer = rhs.coefficients;
        }
        Polynomial poly = new Polynomial(longer.clone());
        for (int i = 0; i < shorter.length; i++) {
            poly.coefficients[i] += shorter[i];
        }
        return poly;
    }

    public double evaluate(double x) {
        double d = 1.0;
        double sum = 0.0;
        for (double coefficient : this.coefficients) {
            sum += d * coefficient;
            d *= x;
        }
        return sum;
    }

    public boolean hasRoot(double x) {
        return this.evaluate(x) == 0;
    }
}
