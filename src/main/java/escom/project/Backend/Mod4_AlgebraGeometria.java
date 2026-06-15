package escom.project.Backend;

import escom.project.Backend.Imagen.Imagen;

public class Mod4_AlgebraGeometria {
    public Imagen trasladar(Imagen img, int dx, int dy) {
        // Creamos la imagen del mismo tamaño
        Imagen res = new Imagen(img.getAncho(), img.getAlto(), img.getTipoActual(), img.getNumCanales());

        for (int y = 0; y < img.getAlto(); y++) {
            for (int x = 0; x < img.getAncho(); x++) {
                int nuevoX = x + dx;
                int nuevoY = y + dy;

                // Solo pintamos si está dentro de los límites de la nueva imagen
                if (nuevoX >= 0 && nuevoX < img.getAncho() && nuevoY >= 0 && nuevoY < img.getAlto()) {
                    int iOriginal = y * img.getAncho() + x;
                    int iDestino = nuevoY * img.getAncho() + nuevoX;

                    res.getPixeles()[iDestino] = img.getPixeles()[iOriginal];
                }
            }
        }
        return res;
    }
    public Imagen rotar(Imagen img, double grados) {
        Imagen res = new Imagen(img.getAncho(), img.getAlto(), img.getTipoActual(), img.getNumCanales());
        double rad = Math.toRadians(grados);
        int xc = img.getAncho() / 2;
        int yc = img.getAlto() / 2;

        for (int y = 0; y < img.getAlto(); y++) {
            for (int x = 0; x < img.getAncho(); x++) {
                // Rotación inversa para evitar huecos (mapeo inverso)
                int xAntiguo = (int) (Math.cos(rad) * (x - xc) + Math.sin(rad) * (y - yc) + xc);
                int yAntiguo = (int) (-Math.sin(rad) * (x - xc) + Math.cos(rad) * (y - yc) + yc);

                if (xAntiguo >= 0 && xAntiguo < img.getAncho() && yAntiguo >= 0 && yAntiguo < img.getAlto()) {
                    res.getPixeles()[y * img.getAncho() + x] = img.getPixeles()[yAntiguo * img.getAncho() + xAntiguo];
                }
            }
        }
        return res;
    }
    public Imagen interpolacion(Imagen img, double factorX, double factorY, String metodo) {
        // Calculamos el nuevo tamaño basado en los factores
        int nuevoAncho = (int) (img.getAncho() * factorX);
        int nuevoAlto = (int) (img.getAlto() * factorY);

        // Creamos la imagen de destino
        Imagen res = new Imagen(nuevoAncho, nuevoAlto, img.getTipoActual(), img.getNumCanales());

        for (int y = 0; y < nuevoAlto; y++) {
            for (int x = 0; x < nuevoAncho; x++) {

                // Mapeo Inverso: ¿A qué coordenada (aunque sea decimal)
                // le corresponde este punto en la imagen original?
                double xOrigen = x / factorX;
                double yOrigen = y / factorY;

                if (metodo.equalsIgnoreCase("Vecino")) {
                    // Simplemente redondeamos al entero más cercano
                    int xC = (int) Math.min(img.getAncho() - 1, Math.floor(xOrigen + 0.5));
                    int yC = (int) Math.min(img.getAlto() - 1, Math.floor(yOrigen + 0.5));
                    res.getPixeles()[y * nuevoAncho + x] = img.getPixeles()[yC * img.getAncho() + xC];

                } else if (metodo.equalsIgnoreCase("Bilineal")) {
                    // Buscamos los 4 vecinos reales
                    int x1 = (int) Math.floor(xOrigen);
                    int y1 = (int) Math.floor(yOrigen);
                    int x2 = Math.min(img.getAncho() - 1, x1 + 1);
                    int y2 = Math.min(img.getAlto() - 1, y1 + 1);

                    // Pesos fraccionales
                    double dx = xOrigen - x1;
                    double dy = yOrigen - y1;

                    // Interpolamos cada canal (R, G, B, A)
                    int r = interpolarCapa(img.getRed(y1*img.getAncho()+x1), img.getRed(y1*img.getAncho()+x2),
                            img.getRed(y2*img.getAncho()+x1), img.getRed(y2*img.getAncho()+x2), dx, dy);

                    int g = interpolarCapa(img.getGreen(y1*img.getAncho()+x1), img.getGreen(y1*img.getAncho()+x2),
                            img.getGreen(y2*img.getAncho()+x1), img.getGreen(y2*img.getAncho()+x2), dx, dy);

                    int b = interpolarCapa(img.getBlue(y1*img.getAncho()+x1), img.getBlue(y1*img.getAncho()+x2),
                            img.getBlue(y2*img.getAncho()+x1), img.getBlue(y2*img.getAncho()+x2), dx, dy);

                    int a = interpolarCapa(img.getAlpha(y1*img.getAncho()+x1), img.getAlpha(y1*img.getAncho()+x2),
                            img.getAlpha(y2*img.getAncho()+x1), img.getAlpha(y2*img.getAncho()+x2), dx, dy);

                    res.setPixel(y * nuevoAncho + x, r, g, b, a);
                }
            }
        }
        return res;
    }
    public Imagen operar(Imagen img1, Imagen img2, String operacion) {
        // 1. Buscamos las dimensiones mínimas para evitar el OutOfBounds
        int anchoMin = Math.min(img1.getAncho(), img2.getAncho());
        int altoMin = Math.min(img1.getAlto(), img2.getAlto());

        // 2. El lienzo resultante tendrá el tamaño de la intersección (la más pequeña)
        Imagen res = new Imagen(anchoMin, altoMin, img1.getTipoActual(), img1.getNumCanales());

        // 3. Usamos doble ciclo para emparejar coordenadas (x, y) correctamente
        for (int y = 0; y < altoMin; y++) {
            for (int x = 0; x < anchoMin; x++) {

                // Calculamos el índice para cada imagen basándonos en su propio ancho
                int i1 = y * img1.getAncho() + x;
                int i2 = y * img2.getAncho() + x;
                int iDestino = y * anchoMin + x;

                // Extraemos canales
                int r1 = img1.getRed(i1), g1 = img1.getGreen(i1), b1 = img1.getBlue(i1);
                int r2 = img2.getRed(i2), g2 = img2.getGreen(i2), b2 = img2.getBlue(i2);
                int a = img1.getAlpha(i1);

                int nR = 0, nG = 0, nB = 0;

                switch (operacion.toLowerCase()) {
                    case "sumar":
                        nR = clamp(r1 + r2); nG = clamp(g1 + g2); nB = clamp(b1 + b2);
                        break;
                    case "restar":
                        nR = clamp(r1 - r2); nG = clamp(g1 - g2); nB = clamp(b1 - b2);
                        break;
                    case "multiplicar":
                        nR = clamp((r1 * r2) / 255);
                        nG = clamp((g1 * g2) / 255);
                        nB = clamp((b1 * b2) / 255);
                        break;
                    case "dividir":
                        nR = clamp(r2 == 0 ? 255 : (r1 * 255) / r2);
                        nG = clamp(g2 == 0 ? 255 : (g1 * 255) / g2);
                        nB = clamp(b2 == 0 ? 255 : (b1 * 255) / b2);
                        break;
                }
                res.setPixel(iDestino, nR, nG, nB, a);
            }
        }
        return res;
    }

    public Imagen operacionLogica(Imagen img1, Imagen img2, String operacion) {
        // 1. Usamos el tamaño mínimo para evitar errores de índice
        int anchoMin = Math.min(img1.getAncho(), img2.getAncho());
        int altoMin = Math.min(img1.getAlto(), img2.getAlto());

        Imagen res = new Imagen(anchoMin, altoMin, img1.getTipoActual(), img1.getNumCanales());

        for (int y = 0; y < altoMin; y++) {
            for (int x = 0; x < anchoMin; x++) {
                int i1 = y * img1.getAncho() + x;
                int i2 = y * img2.getAncho() + x;
                int iDestino = y * anchoMin + x;

                int nR = 0, nG = 0, nB = 0;
                int a = img1.getAlpha(i1);

                switch (operacion.toLowerCase()) {
                    case "and":
                        nR = img1.getRed(i1) & img2.getRed(i2);
                        nG = img1.getGreen(i1) & img2.getGreen(i2);
                        nB = img1.getBlue(i1) & img2.getBlue(i2);
                        break;
                    case "or":
                        nR = img1.getRed(i1) | img2.getRed(i2);
                        nG = img1.getGreen(i1) | img2.getGreen(i2);
                        nB = img1.getBlue(i1) | img2.getBlue(i2);
                        break;
                    case "xor":
                        nR = img1.getRed(i1) ^ img2.getRed(i2);
                        nG = img1.getGreen(i1) ^ img2.getGreen(i2);
                        nB = img1.getBlue(i1) ^ img2.getBlue(i2);
                        break;
                    case "not":
                        // El NOT solo se aplica a la imagen 1 (operación unaria)
                        // Usamos & 0xFF para asegurar que el resultado sea un entero de 8 bits positivo
                        nR = (~img1.getRed(i1)) & 0xFF;
                        nG = (~img1.getGreen(i1)) & 0xFF;
                        nB = (~img1.getBlue(i1)) & 0xFF;
                        break;
                }
                // Aquí no hace falta clamp() porque las operaciones de bits
                // sobre 8 bits nunca se saldrán del rango 0-255.
                res.setPixel(iDestino, nR, nG, nB, a);
            }
        }
        return res;
    }
    public Imagen operacionRelacional(Imagen img1, Imagen img2, String operador) {
        int anchoMin = Math.min(img1.getAncho(), img2.getAncho());
        int altoMin = Math.min(img1.getAlto(), img2.getAlto());

        Imagen res = new Imagen(anchoMin, altoMin, img1.getTipoActual(), img1.getNumCanales());

        for (int y = 0; y < altoMin; y++) {
            for (int x = 0; x < anchoMin; x++) {
                int i1 = y * img1.getAncho() + x;
                int i2 = y * img2.getAncho() + x;
                int iDest = y * anchoMin + x;

                // Extraemos el valor promedio (gris) para comparar magnitud de brillo
                // O podrías comparar canal por canal, pero lo estándar es brillo
                int v1 = (img1.getRed(i1) + img1.getGreen(i1) + img1.getBlue(i1)) / 3;
                int v2 = (img2.getRed(i2) + img2.getGreen(i2) + img2.getBlue(i2)) / 3;

                boolean cumple = false;

                switch (operador) {
                    case ">":  cumple = (v1 > v2);  break;
                    case "<":  cumple = (v1 < v2);  break;
                    case "==": cumple = (v1 == v2); break;
                    case "!=": cumple = (v1 != v2); break;
                    case ">=": cumple = (v1 >= v2); break;
                    case "<=": cumple = (v1 <= v2); break;
                }

                // Si cumple la relación, ponemos blanco, si no, negro
                int color = cumple ? 255 : 0;
                res.setPixel(iDest, color, color, color, 255);
            }
        }
        return res;
    }
    //---------------------------------------------------------EXTRAS------------------------------------
    private int interpolarCapa(int v1, int v2, int v3, int v4, double dx, double dy) {
        double resultado = v1 * (1 - dx) * (1 - dy) +
                v2 * dx * (1 - dy) +
                v3 * (1 - dx) * dy +
                v4 * dx * dy;
        return (int) Math.round(resultado);
    }
    private int clamp(int valor) {
        if (valor > 255) return 255;
        if (valor < 0) return 0;
        return valor;
    }
}
