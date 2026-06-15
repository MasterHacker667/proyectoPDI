package escom.project.Backend;

import escom.project.Backend.Imagen.Imagen;
import escom.project.Backend.Imagen.Tipo;

public class Mod1_Color {

    public Imagen extraerCanalRojo(Imagen img){
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), Tipo.RGB, 3);

        for (int i = 0; i < img.getPixeles().length; i++) {
            int a = img.getAlpha(i);
            int r = img.getRed(i);
            // Rojo se queda, Verde y Azul van a 0
            resultado.setPixel(i, r, 0, 0, a);
        }
        return resultado;
    }
    public Imagen extraerCanalVerde(Imagen img){
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), Tipo.RGB, 3);

        for (int i = 0; i < img.getPixeles().length; i++) {
            int a = img.getAlpha(i);
            int g = img.getGreen(i);
            // Verde se queda, Rojo y Azul van a 0
            resultado.setPixel(i, 0, g, 0, a);
        }
        return resultado;
    }
    public Imagen extraerCanalAzul(Imagen img){
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), Tipo.RGB, 3);

        for (int i = 0; i < img.getPixeles().length; i++) {
            int a = img.getAlpha(i);
            int b = img.getBlue(i);
            // Azul se queda, Rojo y Verde van a 0
            resultado.setPixel(i, 0, 0, b, a);
        }
        return resultado;
    }
    public Imagen extraerCanalAlpha(Imagen img){
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), Tipo.GRIS, 1);

        for (int i = 0; i < img.getPixeles().length; i++) {
            int a = img.getAlpha(i);

            // Para que el Alpha se "vea", ponemos el valor de A en los tres canales (R,G,B)
            // Esto crea una imagen en escala de grises que representa la transparencia
            resultado.setPixel(i, a, a, a, 255); // Forzamos Alpha a 255 para ver el mapa
        }
        return resultado;
    }
    public Imagen convertirGrisMedia(Imagen img){
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), Tipo.GRIS, 1);

        for (int i = 0; i < img.getPixeles().length; i++) {
            int a = img.getAlpha(i);
            int r = img.getRed(i);
            int g = img.getGreen(i);
            int b = img.getBlue(i);

            // Calculamos el promedio
            int promedio = (r + g + b) / 3;

            // En una imagen de grises, R, G y B valen lo mismo
            resultado.setPixel(i, promedio, promedio, promedio, a);
        }
        return resultado;
    }
    public Imagen convertirGrisLuminancia(Imagen img){
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), Tipo.GRIS, 1);

        for (int i = 0; i < img.getPixeles().length; i++) {
            int a = img.getAlpha(i);
            int r = img.getRed(i);
            int g = img.getGreen(i);
            int b = img.getBlue(i);

            // Aplicamos los coeficientes de luminosidad
            int y = (int)(0.299 * r + 0.587 * g + 0.114 * b);

            // Aseguramos que no sobrepase 255 (truncamiento)
            if (y > 255) y = 255;

            resultado.setPixel(i, y, y, y, a);
        }
        return resultado;
    }
    public Imagen convertirRGBaYIQ(Imagen img){
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), Tipo.YIQ, 3);

        for (int i = 0; i < img.getPixeles().length; i++) {
            int r = img.getRed(i);
            int g = img.getGreen(i);
            int b = img.getBlue(i);
            int a = img.getAlpha(i);

            float y = (float)(0.299 * r + 0.587 * g + 0.114 * b);
            float i_val = (float)(0.596 * r - 0.274 * g - 0.322 * b);
            float q = (float)(0.211 * r - 0.523 * g + 0.312 * b);

            // Guardamos los valores (puedes guardarlos empaquetados o en un arreglo float paralelo si necesitas precisión extrema)
            resultado.setPixel(i, (int)y, (int)i_val, (int)q, a);
        }
        return resultado;
    }
    public Imagen convertirYIQaRGB(Imagen img){
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), Tipo.RGB, 3);

        for (int i = 0; i < img.getPixeles().length; i++) {
            int a = img.getAlpha(i);
            // Aquí extraemos Y, I, Q que guardamos previamente
            // Nota: Si los guardaste como bytes, recuerda que I y Q pueden ser negativos
            float y = (float) img.getRed(i);   // Usamos el canal R para guardar Y
            float invI = (float) (byte) img.getGreen(i); // Casteo a byte para recuperar el signo
            float invQ = (float) (byte) img.getBlue(i);

            // Aplicamos la matriz inversa
            int r = (int)(y + 0.956 * invI + 0.621 * invQ);
            int g = (int)(y - 0.272 * invI - 0.647 * invQ);
            int b = (int)(y - 1.106 * invI + 1.703 * invQ);

            // Validamos rangos [0, 255]
            r = Math.max(0, Math.min(255, r));
            g = Math.max(0, Math.min(255, g));
            b = Math.max(0, Math.min(255, b));

            resultado.setPixel(i, r, g, b, a);
        }
        return resultado;

    }
    public Imagen convertirRGBaHSV(Imagen img){
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), Tipo.HSV, 3);

        for (int i = 0; i < img.getPixeles().length; i++) {
            float r = img.getRed(i) / 255.0f;
            float g = img.getGreen(i) / 255.0f;
            float b = img.getBlue(i) / 255.0f;
            int a = img.getAlpha(i);

            float max = Math.max(r, Math.max(g, b));
            float min = Math.min(r, Math.min(g, b));
            float delta = max - min;

            // --- Calcular H (Hue) ---
            float h = 0;
            if (delta != 0) {
                if (max == r) {
                    h = 60 * (((g - b) / delta) % 6);
                } else if (max == g) {
                    h = 60 * (((b - r) / delta) + 2);
                } else if (max == b) {
                    h = 60 * (((r - g) / delta) + 4);
                }
            }
            if (h < 0) h += 360;

            // --- Calcular S (Saturation) ---
            float s = (max == 0) ? 0 : (delta / max);

            // --- Calcular V (Value) ---
            float v = max;

            // Guardado:
            // Para visualización rápida, escalamos H(0-360) a 0-255, S y V a 0-255
            int hByte = (int) (h * 255 / 360);
            int sByte = (int) (s * 255);
            int vByte = (int) (v * 255);

            resultado.setPixel(i, hByte, sByte, vByte, a);
        }
        return resultado;
    }
    public Imagen convertirHSVaRGB(Imagen img){
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), Tipo.RGB, 3);

        for (int i = 0; i < img.getPixeles().length; i++) {
            int a = img.getAlpha(i);

            // Recuperamos los valores escalados (asumiendo que H se guardó en R, S en G, V en B)
            float h = (img.getRed(i) * 360f) / 255f;
            float s = img.getGreen(i) / 255f;
            float v = img.getBlue(i) / 255f;

            float c = v * s;
            float x = c * (1 - Math.abs((h / 60f) % 2 - 1));
            float m = v - c;

            float rTmp = 0, gTmp = 0, bTmp = 0;

            if (h >= 0 && h < 60) {
                rTmp = c; gTmp = x; bTmp = 0;
            } else if (h >= 60 && h < 120) {
                rTmp = x; gTmp = c; bTmp = 0;
            } else if (h >= 120 && h < 180) {
                rTmp = 0; gTmp = c; bTmp = x;
            } else if (h >= 180 && h < 240) {
                rTmp = 0; gTmp = x; bTmp = c;
            } else if (h >= 240 && h < 300) {
                rTmp = x; gTmp = 0; bTmp = c;
            } else {
                rTmp = c; gTmp = 0; bTmp = x;
            }

            // Convertir a rango 0-255 sumando el valor base m
            int r = (int) ((rTmp + m) * 255);
            int g = (int) ((gTmp + m) * 255);
            int b = (int) ((bTmp + m) * 255);

            resultado.setPixel(i, r, g, b, a);
        }
        return resultado;

    }
    public Imagen convertirRGBaLab(Imagen img){ //Basado en el PDF de Reinhard
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), Tipo.LAB, 3);

        for (int i = 0; i < img.getPixeles().length; i++) {
            // 1. Normalizar RGB a [0, 1] y evitar ceros para el log
            double r = Math.max(img.getRed(i) / 255.0, 0.00001);
            double g = Math.max(img.getGreen(i) / 255.0, 0.00001);
            double b = Math.max(img.getBlue(i) / 255.0, 0.00001);

            // 2. RGB -> LMS
            double L = 0.3811*r + 0.5783*g + 0.0402*b;
            double M = 0.1967*r + 0.7244*g + 0.0782*b;
            double S = 0.0241*r + 0.1288*g + 0.8444*b;

            // 3. Logaritmo
            double logL = Math.log10(L);
            double logM = Math.log10(M);
            double logS = Math.log10(S);

            // 4. LMS -> Lab (Reinhard)
            double l_res = (logL + logM + logS) / Math.sqrt(3);
            double a_res = (logL + logM - 2*logS) / Math.sqrt(6);
            double b_res = (logL - logM) / Math.sqrt(2);

            // Nota: Para "ver" Lab en un int[] de píxeles,
            // tendrías que normalizar estos valores a 0-255.
            resultado.setPixel(i, (int)l_res, (int)a_res, (int)b_res, img.getAlpha(i));
        }
        return resultado;
    }
    public Imagen convertirLabaRGB(Imagen img){
        Imagen resultado = new Imagen(img.getAncho(), img.getAlto(), Tipo.RGB, 3);

        for (int i = 0; i < img.getPixeles().length; i++) {
            int alpha = img.getAlpha(i);

            // 1. Recuperar los valores Lab (Asumiendo que se guardaron en R, G, B)
            // Nota: L, a, b suelen ser double, si los guardaste como int,
            // asegúrate de usar la misma escala de normalización.
            double L_val = (double) img.getRed(i);
            double a_val = (double) (byte) img.getGreen(i); // byte para recuperar negativos
            double b_val = (double) (byte) img.getBlue(i);

            // 2. Lab -> LMS logarítmico
            double logL = L_val / Math.sqrt(3) + a_val / Math.sqrt(6) + b_val / Math.sqrt(2);
            double logM = L_val / Math.sqrt(3) + a_val / Math.sqrt(6) - b_val / Math.sqrt(2);
            double logS = L_val / Math.sqrt(3) - (2 * a_val) / Math.sqrt(6);

            // 3. Linealizar (Inversa del logaritmo base 10)
            double L = Math.pow(10, logL);
            double M = Math.pow(10, logM);
            double S = Math.pow(10, logS);

            // 4. LMS -> RGB (Matriz inversa de Reinhard)
            int r = (int) ((4.4679 * L - 3.5873 * M + 0.1193 * S) * 255);
            int g = (int) ((-1.2186 * L + 2.3809 * M - 0.1624 * S) * 255);
            int b = (int) ((0.0497 * L - 0.2439 * M + 1.2045 * S) * 255);

            // 5. Clamping [0, 255]
            r = Math.max(0, Math.min(255, r));
            g = Math.max(0, Math.min(255, g));
            b = Math.max(0, Math.min(255, b));

            resultado.setPixel(i, r, g, b, alpha);
        }
        return resultado;
    }
}
