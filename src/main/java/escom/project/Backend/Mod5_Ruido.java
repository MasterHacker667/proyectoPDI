package escom.project.Backend;

import escom.project.Backend.Imagen.Imagen;

import java.util.Random;

public class Mod5_Ruido {
    public Imagen agregarRuidoSalYPimienta(Imagen img, double densidad) {
        Imagen res = new Imagen(img.getAncho(), img.getAlto(), img.getTipoActual(), img.getNumCanales());
        System.arraycopy(img.getPixeles(), 0, res.getPixeles(), 0, img.getPixeles().length);

        Random rnd = new Random();
        int totalPixeles = img.getAncho() * img.getAlto();
        int cantidadRuido = (int) (totalPixeles * densidad);

        for (int i = 0; i < cantidadRuido; i++) {
            int pos = rnd.nextInt(totalPixeles);
            // 50% probabilidad de sal (255) o pimienta (0)
            int color = rnd.nextBoolean() ? 255 : 0;
            res.setPixel(pos, color, color, color, img.getAlpha(pos));
        }
        return res;
    }
    public Imagen agregarRuidoGaussiano(Imagen img, double media, double desviacion) {
        // Creamos la imagen de destino
        Imagen res = new Imagen(img.getAncho(), img.getAlto(), img.getTipoActual(), img.getNumCanales());

        // Usamos la clase Random de Java que ya trae el método para distribución normal
        Random rnd = new Random();

        for (int i = 0; i < img.getPixeles().length; i++) {
            // nextGaussian() genera un número con media 0 y desviación 1
            // Lo multiplicamos por nuestra desviación y sumamos la media deseada
            double ruido = (rnd.nextGaussian() * desviacion) + media;

            // Extraemos colores originales
            int r1 = img.getRed(i);
            int g1 = img.getGreen(i);
            int b1 = img.getBlue(i);
            int a = img.getAlpha(i);

            // Sumamos el ruido y aplicamos clamp para no salirnos de [0, 255]
            int nR = clamp((int) (r1 + ruido));
            int nG = clamp((int) (g1 + ruido));
            int nB = clamp((int) (b1 + ruido));

            res.setPixel(i, nR, nG, nB, a);
        }
        return res;
    }
    public Imagen agregarRuidoUniforme(Imagen img, double a, double b) {
        Imagen res = new Imagen(img.getAncho(), img.getAlto(), img.getTipoActual(), img.getNumCanales());
        Random rnd = new Random();

        for (int i = 0; i < img.getPixeles().length; i++) {
            // Genera un valor aleatorio entre 'a' y 'b'
            // rnd.nextDouble() genera un valor entre 0.0 y 1.0
            double ruido = a + (b - a) * rnd.nextDouble();

            int r = clamp((int) (img.getRed(i) + ruido));
            int g = clamp((int) (img.getGreen(i) + ruido));
            int bCol = clamp((int) (img.getBlue(i) + ruido));
            int aChan = img.getAlpha(i);

            res.setPixel(i, r, g, bCol, aChan);
        }
        return res;
    }

    //*************************EXTRAS****************************
    private int clamp(int valor) {
        if (valor > 255) return 255;
        if (valor < 0) return 0;
        return valor;
    }
}
