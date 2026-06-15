package escom.project.Backend;

import escom.project.Backend.Imagen.Imagen;
import escom.project.Backend.Imagen.Tipo;

public class Mod3_Punto {
    public Imagen ajustarBrillo(Imagen img, int valor) {
        // Creamos la imagen de salida con las mismas propiedades
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), img.getTipoActual(), img.getNumCanales());
        Tipo tipo = img.getTipoActual();

        for (int i = 0; i < img.getPixeles().length; i++) {
            int a = img.getAlpha(i);
            int nR, nG, nB;

            if (tipo == Tipo.RGB || tipo == Tipo.RGBA) {
                // En RGB, sumamos a los tres canales de color
                nR = clamp(img.getRed(i) + valor);
                nG = clamp(img.getGreen(i) + valor);
                nB = clamp(img.getBlue(i) + valor);
            } else {
                // En YIQ, LAB, HSV: Solo afectamos el primer canal (Brillo/Luminancia)
                // Usamos getRed() como contenedor del Canal 1, getGreen() del 2, etc.
                nR = clamp(img.getRed(i) + valor); // Cambia Y, L o V
                nG = img.getGreen(i);              // Mantiene I, a o S
                nB = img.getBlue(i);               // Mantiene Q, b o H
            }

            resultado.setPixel(i, nR, nG, nB, a);
        }
        return resultado;
    }
    public Imagen ajustarContraste(Imagen img, double factor) {
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), img.getTipoActual(), img.getNumCanales());
        Tipo tipo = img.getTipoActual();

        for (int i = 0; i < img.getPixeles().length; i++) {
            int a = img.getAlpha(i);
            int nR, nG, nB;

            if (tipo == Tipo.RGB || tipo == Tipo.RGBA) {
                // Aplicamos a los tres canales
                nR = clamp((int)((img.getRed(i) - 128) * factor + 128));
                nG = clamp((int)((img.getGreen(i) - 128) * factor + 128));
                nB = clamp((int)((img.getBlue(i) - 128) * factor + 128));
            } else {
                // Solo al canal de intensidad (Canal 1 / Red bits)
                nR = clamp((int)((img.getRed(i) - 128) * factor + 128));
                nG = img.getGreen(i); // I, a, S se quedan igual
                nB = img.getBlue(i); // Q, b, H se quedan igual
            }

            resultado.setPixel(i, nR, nG, nB, a);
        }
        return resultado;
    }
    public Imagen generarNegativo(Imagen img) {
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), img.getTipoActual(), img.getNumCanales());
        Tipo tipo = img.getTipoActual();

        for (int i = 0; i < img.getPixeles().length; i++) {
            int a = img.getAlpha(i);
            int nR, nG, nB;

            if (tipo == Tipo.RGB || tipo == Tipo.RGBA) {
                // Inversión total de colores
                nR = 255 - img.getRed(i);
                nG = 255 - img.getGreen(i);
                nB = 255 - img.getBlue(i);
            } else {
                // Inversión de luminancia solamente (Mantiene la esencia del color)
                nR = 255 - img.getRed(i); // Invierte Y, L o V
                nG = img.getGreen(i);      // I, a, S intactos
                nB = img.getBlue(i);       // Q, b, H intactos
            }

            resultado.setPixel(i, nR, nG, nB, a);
        }
        return resultado;
    }
    public Imagen binarizarUmbral(Imagen img, int... umbrales) {
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), img.getTipoActual(), img.getNumCanales());

        // 1. Ordenar los umbrales de menor a mayor (por seguridad)
        java.util.Arrays.sort(umbrales);

        // 2. Definir los valores de salida (los "escalones" de color)
        // Si hay N umbrales, hay N+1 regiones.
        int numRegiones = umbrales.length + 1;
        int[] valoresSalida = new int[numRegiones];
        for (int i = 0; i < numRegiones; i++) {
            valoresSalida[i] = (i * 255) / (numRegiones - 1);
        }

        for (int i = 0; i < img.getPixeles().length; i++) {
            // Usamos la luminancia (Canal 1 / Red bits) para decidir el umbral
            int valorOriginal = img.getRed(i);
            int nuevoValor = valoresSalida[numRegiones - 1]; // Valor por defecto (el más alto)

            // Buscamos en qué región cae el píxel
            for (int j = 0; j < umbrales.length; j++) {
                if (valorOriginal < umbrales[j]) {
                    nuevoValor = valoresSalida[j];
                    break;
                }
            }

            // Aplicamos el mismo valor a los 3 canales para que sea escala de grises/binaria
            // El Alpha se mantiene
            resultado.setPixel(i, nuevoValor, nuevoValor, nuevoValor, img.getAlpha(i));
        }
        resultado.setTipoActial(Tipo.BINARIA);
        return resultado;
    }
    public Imagen invertirBinarizacion(Imagen img) {
    //Como la imagen binarizada ya está en gris/blanco/negro,
    //solo aplicamos el negativo a los canales.
        return generarNegativo(img);
    }
    //-----------------------------------------------AUXILIARES--------------------------------------------------
    private int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
