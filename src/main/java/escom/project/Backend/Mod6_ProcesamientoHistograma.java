package escom.project.Backend;

import escom.project.Backend.Imagen.Imagen;

public class Mod6_ProcesamientoHistograma {
    public Imagen desplazarHistograma(Imagen img, int cantidad) {
        Imagen res = new Imagen(img.getAncho(), img.getAlto(), img.getTipoActual(), img.getNumCanales());
        for (int i = 0; i < img.getPixeles().length; i++) {
            int r = clamp(img.getRed(i) + cantidad);
            int g = clamp(img.getGreen(i) + cantidad);
            int b = clamp(img.getBlue(i) + cantidad);
            res.setPixel(i, r, g, b, img.getAlpha(i));
        }
        return res;
    }
    public Imagen expandirContraste(Imagen img, int min, int max) {
        Imagen res = new Imagen(img.getAncho(), img.getAlto(), img.getTipoActual(), img.getNumCanales());
        //int min = 255, max = 0;

        // 1. Encontrar el rango real de la imagen
        for (int i = 0; i < img.getPixeles().length; i++) {
            int v = (img.getRed(i) + img.getGreen(i) + img.getBlue(i)) / 3;
            if (v < min) min = v;
            if (v > max) max = v;
        }

        // 2. Estirar
        if (max == min) return img; // Evitar división por cero
        for (int i = 0; i < img.getPixeles().length; i++) {
            int r = clamp((img.getRed(i) - min) * 255 / (max - min));
            int g = clamp((img.getGreen(i) - min) * 255 / (max - min));
            int b = clamp((img.getBlue(i) - min) * 255 / (max - min));
            res.setPixel(i, r, g, b, img.getAlpha(i));
        }
        return res;
    }
    public Imagen contraerContraste(Imagen img, int nuevoMin, int nuevoMax) {
        Imagen res = new Imagen(img.getAncho(), img.getAlto(), img.getTipoActual(), img.getNumCanales());

        // Calculamos el rango de salida
        double rangoNuevo = nuevoMax - nuevoMin;

        for (int i = 0; i < img.getPixeles().length; i++) {
            // La fórmula es: ((PixelOriginal / 255.0) * RangoNuevo) + nuevoMin
            // Usamos 255.0 para forzar aritmética de punto flotante
            int r = clamp((int) (((img.getRed(i) / 255.0) * rangoNuevo) + nuevoMin));
            int g = clamp((int) (((img.getGreen(i) / 255.0) * rangoNuevo) + nuevoMin));
            int b = clamp((int) (((img.getBlue(i) / 255.0) * rangoNuevo) + nuevoMin));

            res.setPixel(i, r, g, b, img.getAlpha(i));
        }
        return res;
    }
    public Imagen ecualizarImagen(Imagen img) {
        int ancho = img.getAncho();
        int alto = img.getAlto();
        int totalPixeles = ancho * alto;
        Imagen res = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        // 1. Calcular el Histograma (usaremos el promedio de canales o solo uno si es Gris)
        int[] histograma = new int[256];
        for (int i = 0; i < img.getPixeles().length; i++) {
            int v = (img.getRed(i) + img.getGreen(i) + img.getBlue(i)) / 3;
            histograma[v]++;
        }

        // 2. Calcular la Función de Distribución Acumulada (CDF)
        float[] cdf = new float[256];
        float suma = 0;
        for (int i = 0; i < 256; i++) {
            suma += (float) histograma[i] / totalPixeles;
            cdf[i] = suma;
        }

        // 3. Mapear los píxeles originales a los nuevos valores
        for (int i = 0; i < img.getPixeles().length; i++) {
            // Obtenemos el valor original (promedio)
            int r1 = img.getRed(i);
            int g1 = img.getGreen(i);
            int b1 = img.getBlue(i);

            // Aplicamos la fórmula: NuevoValor = CDF(ValorOriginal) * 255
            // Lo hacemos canal por canal para mantener el color lo mejor posible
            int nR = clamp(Math.round(cdf[r1] * 255));
            int nG = clamp(Math.round(cdf[g1] * 255));
            int nB = clamp(Math.round(cdf[b1] * 255));

            res.setPixel(i, nR, nG, nB, img.getAlpha(i));
        }

        return res;
    }
    public Imagen correspondenciaHistograma(Imagen imgOrigen, Imagen imgDestino) {
        int totalOrigen = imgOrigen.getAncho() * imgOrigen.getAlto();
        int totalDestino = imgDestino.getAncho() * imgDestino.getAlto();
        Imagen res = new Imagen(imgOrigen.getAncho(), imgOrigen.getAlto(), imgOrigen.getTipoActual(), imgOrigen.getNumCanales());

        // 1. Obtener CDFs (Usando el promedio de canales para simplificar)
        float[] cdfOrigen = calcularCDF(imgOrigen, totalOrigen);
        float[] cdfDestino = calcularCDF(imgDestino, totalDestino);

        // 2. Crear la Tabla de Mapeo (Lookup Table - LUT)
        int[] lut = new int[256];
        for (int i = 0; i < 256; i++) {
            int j = 255;
            // Buscamos el valor en la CDF destino que más se acerque a la CDF origen
            while (j > 0 && cdfOrigen[i] <= cdfDestino[j]) {
                j--;
            }
            lut[i] = j;
        }

        // 3. Aplicar el mapeo a la nueva imagen
        for (int i = 0; i < imgOrigen.getPixeles().length; i++) {
            int r = lut[imgOrigen.getRed(i)];
            int g = lut[imgOrigen.getGreen(i)];
            int b = lut[imgOrigen.getBlue(i)];
            res.setPixel(i, r, g, b, imgOrigen.getAlpha(i));
        }

        return res;
    }
    //*************************EXTRAS****************************
    private int clamp(int valor) {
        if (valor > 255) return 255;
        if (valor < 0) return 0;
        return valor;
    }
    private float[] calcularCDF(Imagen img, int total) {
        int[] hist = new int[256];
        for (int i = 0; i < img.getPixeles().length; i++) {
            int v = (img.getRed(i) + img.getGreen(i) + img.getBlue(i)) / 3;
            hist[v]++;
        }
        float[] cdf = new float[256];
        float acum = 0;
        for (int i = 0; i < 256; i++) {
            acum += (float) hist[i] / total;
            cdf[i] = acum;
        }
        return cdf;
    }
}
