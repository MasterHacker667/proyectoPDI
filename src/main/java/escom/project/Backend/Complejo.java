package escom.project.Backend;

public class Complejo {
    public double real;
    public double imag;

    public Complejo(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public double magnitud() {
        return Math.sqrt(real * real + imag * imag);
    }
}