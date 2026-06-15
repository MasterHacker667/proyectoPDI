package escom.project.Backend;

import escom.project.Backend.Imagen.Imagen;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class FiltrosNoLineales_P9 {
    private static ArrayList<Integer> obtenerVecindadCanal(Imagen img, int x, int y, double[][] mascara, int canal) {
        ArrayList<Integer> vecindad = new ArrayList<>();
        int altoMascara = mascara.length;
        int anchoMascara = mascara[0].length;
        int radioY = altoMascara / 2;
        int radioX = anchoMascara / 2;

        int anchoImg = img.getAncho();
        int altoImg = img.getAlto();

        for (int i = 0; i < altoMascara; i++) {
            for (int j = 0; j < anchoMascara; j++) {
                // Si el valor en la máscara es 1.0 (o cercano por flotantes), el píxel está activo
                if (mascara[i][j] > 0.5) {
                    // Calculamos la posición del vecino con respecto al centro (x, y)
                    int vecinoX = x + (j - radioX);
                    int vecinoY = y + (i - radioY);

                    // --- TRATAMIENTO PERIÓDICO (MÓDULO) SEGÚN EL PAPER ---
                    // Si se sale por los bordes, da la vuelta al otro extremo de la imagen
                    vecinoX = (vecinoX % anchoImg + anchoImg) % anchoImg;
                    vecinoY = (vecinoY % altoImg + altoImg) % altoImg;

                    int indicePixel = vecinoY * anchoImg + vecinoX;

                    // Extraemos el valor del canal correspondiente
                    int valorCanal = 0;
                    switch (canal) {
                        case 0: valorCanal = img.getRed(indicePixel); break;
                        case 1: valorCanal = img.getGreen(indicePixel); break;
                        case 2: valorCanal = img.getBlue(indicePixel); break;
                    }
                    vecindad.add(valorCanal);
                }
            }
        }
        return vecindad;
    }
    private static int calcularMediaAritmetica(ArrayList<Integer> v) {
        double suma = 0;
        for (int val : v) suma += val;
        return (int) Math.min(255, Math.max(0, suma / v.size()));
    }
    private static int calcularMediana(ArrayList<Integer> v) {
        Collections.sort(v);
        return v.get(v.size() / 2);
    }
    private static int calcularMaximo(ArrayList<Integer> v) {
        Collections.sort(v);
        return v.get(v.size() - 1);
    }
    private static int calcularMinimo(ArrayList<Integer> v) {
        Collections.sort(v);
        return v.get(0);
    }
    private static int calcularPuntoMedio(ArrayList<Integer> v) {
        Collections.sort(v);
        int min = v.get(0);
        int max = v.get(v.size() - 1);
        return (int) ((min + max) / 2.0);
    }
    private static int calcularMaximoMinimo(ArrayList<Integer> v, int pixelCentral) {
        Collections.sort(v);
        int min = v.get(0);
        int max = v.get(v.size() - 1);

        if (Math.abs(pixelCentral - max) <= Math.abs(pixelCentral - min)) {
            return max;
        } else {
            return min;
        }
    }
    private static int calcularInferiorGeometrico(ArrayList<Integer> v) {
        double productoria = 1.0;
        double exponente = 1.0 / v.size();
        for (int val : v) {
            // Evitamos indeterminaciones si el píxel es completamente 0 negro absoluto
            double pixelVal = (val == 0) ? 0.0001 : val;
            productoria *= Math.pow(pixelVal, exponente);
        }
        return (int) Math.min(255, Math.max(0, productoria));
    }
    private static int calcularInferiorArmonico(ArrayList<Integer> v) {
        double sumaInversos = 0.0;
        for (int val : v) {
            double pixelVal = (val == 0) ? 0.0001 : val; // Prevenir división por cero
            sumaInversos += (1.0 / pixelVal);
        }
        return (int) Math.min(255, Math.max(0, v.size() / sumaInversos));
    }
    private static int calcularAlfaTrimmed(ArrayList<Integer> v, int P) {
        int N = v.size();

        // CASO SEGURO 1: Si la máscara no seleccionó ningún píxel (está vacía),
        // devolvemos 0 para evitar que el programa truene.
        if (N == 0) {
            return 0;
        }

        // CASO SEGURO 2: Si el recorte (2P) es mayor o igual al tamaño de la vecindad,
        // significa que borraríamos todo. En ese caso, forzamos a que se comporte
        // como el filtro de la Mediana para rescatar el cálculo.
        if (2 * P >= N) {
            Collections.sort(v);
            return v.get(N / 2);
        }

        // Si pasa las condiciones seguras, ejecuta el Alfa Trimmed normal
        Collections.sort(v);
        double suma = 0;
        for (int i = P; i < (N - P); i++) {
            suma += v.get(i);
        }

        return (int) Math.min(255, Math.max(0, suma / (N - 2 * P)));
    }
    private static int calcularContraArmonico(ArrayList<Integer> v, double P) {
        double numerador = 0.0;
        double denominador = 0.0;

        for (int val : v) {
            double pixelVal = (val == 0) ? 0.0001 : val; // Evitar indeterminación por 0^P
            numerador += Math.pow(pixelVal, P + 1.0);
            denominador += Math.pow(pixelVal, P);
        }

        if (denominador == 0.0) return 0;
        return (int) Math.min(255, Math.max(0, numerador / denominador));
    }

    //1. FILTRO DE LA MEDIA ARITMÉTICA
    public static Imagen filtroMediaAritmetica(Imagen imgEntrada, double[][] mascara) {
        int ancho = imgEntrada.getAncho();
        int alto = imgEntrada.getAlto();
        Imagen imgSalida = new Imagen(ancho, alto, imgEntrada.getTipoActual(), imgEntrada.getNumCanales());

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int index = y * ancho + x;
                int alpha = imgEntrada.getAlpha(index); // Mantenemos el Alpha original

                // Procesamos cada canal de color de forma independiente
                int rFinal = calcularMediaAritmetica(obtenerVecindadCanal(imgEntrada, x, y, mascara, 0));
                int gFinal = calcularMediaAritmetica(obtenerVecindadCanal(imgEntrada, x, y, mascara, 1));
                int bFinal = calcularMediaAritmetica(obtenerVecindadCanal(imgEntrada, x, y, mascara, 2));

                imgSalida.setPixel(index, rFinal, gFinal, bFinal, alpha);
            }
        }
        return imgSalida;
    }
    //2. Filtro Mediana
    public static Imagen filtroMediana(Imagen imgEntrada, double[][] mascara) {
        int ancho = imgEntrada.getAncho();
        int alto = imgEntrada.getAlto();
        Imagen imgSalida = new Imagen(ancho, alto, imgEntrada.getTipoActual(), imgEntrada.getNumCanales());

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int index = y * ancho + x;
                int alpha = imgEntrada.getAlpha(index);

                int rFinal = calcularMediana(obtenerVecindadCanal(imgEntrada, x, y, mascara, 0));
                int gFinal = calcularMediana(obtenerVecindadCanal(imgEntrada, x, y, mascara, 1));
                int bFinal = calcularMediana(obtenerVecindadCanal(imgEntrada, x, y, mascara, 2));

                imgSalida.setPixel(index, rFinal, gFinal, bFinal, alpha);
            }
        }
        return imgSalida;
    }
    //3. FIltro Maximo
    public static Imagen filtroMaximo(Imagen imgEntrada, double[][] mascara) {
        int ancho = imgEntrada.getAncho();
        int alto = imgEntrada.getAlto();
        Imagen imgSalida = new Imagen(ancho, alto, imgEntrada.getTipoActual(), imgEntrada.getNumCanales());

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int index = y * ancho + x;
                int alpha = imgEntrada.getAlpha(index);

                int rFinal = calcularMaximo(obtenerVecindadCanal(imgEntrada, x, y, mascara, 0));
                int gFinal = calcularMaximo(obtenerVecindadCanal(imgEntrada, x, y, mascara, 1));
                int bFinal = calcularMaximo(obtenerVecindadCanal(imgEntrada, x, y, mascara, 2));

                imgSalida.setPixel(index, rFinal, gFinal, bFinal, alpha);
            }
        }
        return imgSalida;
    }
    //4. Filtro Minimo
    public static Imagen filtroMinimo(Imagen imgEntrada, double[][] mascara) {
        int ancho = imgEntrada.getAncho();
        int alto = imgEntrada.getAlto();
        Imagen imgSalida = new Imagen(ancho, alto, imgEntrada.getTipoActual(), imgEntrada.getNumCanales());

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int index = y * ancho + x;
                int alpha = imgEntrada.getAlpha(index);

                int rFinal = calcularMinimo(obtenerVecindadCanal(imgEntrada, x, y, mascara, 0));
                int gFinal = calcularMinimo(obtenerVecindadCanal(imgEntrada, x, y, mascara, 1));
                int bFinal = calcularMinimo(obtenerVecindadCanal(imgEntrada, x, y, mascara, 2));

                imgSalida.setPixel(index, rFinal, gFinal, bFinal, alpha);
            }
        }
        return imgSalida;
    }
    //5. Filtro Minimo
    public static Imagen filtroPuntoMedio(Imagen imgEntrada, double[][] mascara) {
        int ancho = imgEntrada.getAncho();
        int alto = imgEntrada.getAlto();
        Imagen imgSalida = new Imagen(ancho, alto, imgEntrada.getTipoActual(), imgEntrada.getNumCanales());

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int index = y * ancho + x;
                int alpha = imgEntrada.getAlpha(index);

                int rFinal = calcularPuntoMedio(obtenerVecindadCanal(imgEntrada, x, y, mascara, 0));
                int gFinal = calcularPuntoMedio(obtenerVecindadCanal(imgEntrada, x, y, mascara, 1));
                int bFinal = calcularPuntoMedio(obtenerVecindadCanal(imgEntrada, x, y, mascara, 2));

                imgSalida.setPixel(index, rFinal, gFinal, bFinal, alpha);
            }
        }
        return imgSalida;
    }
    //6. Filtro Maximo Minimo
    public static Imagen filtroMaximoMinimo(Imagen imgEntrada, double[][] mascara) {
        int ancho = imgEntrada.getAncho();
        int alto = imgEntrada.getAlto();
        Imagen imgSalida = new Imagen(ancho, alto, imgEntrada.getTipoActual(), imgEntrada.getNumCanales());

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int index = y * ancho + x;
                int alpha = imgEntrada.getAlpha(index);

                int rFinal = calcularMaximoMinimo(obtenerVecindadCanal(imgEntrada, x, y, mascara, 0), imgEntrada.getRed(index));
                int gFinal = calcularMaximoMinimo(obtenerVecindadCanal(imgEntrada, x, y, mascara, 1), imgEntrada.getGreen(index));
                int bFinal = calcularMaximoMinimo(obtenerVecindadCanal(imgEntrada, x, y, mascara, 2), imgEntrada.getBlue(index));

                imgSalida.setPixel(index, rFinal, gFinal, bFinal, alpha);
            }
        }
        return imgSalida;
    }
    //7. Filtro Inferior Geométrico
    public static Imagen filtroInferiorGeometrico(Imagen imgEntrada, double[][] mascara) {
        int ancho = imgEntrada.getAncho();
        int alto = imgEntrada.getAlto();
        Imagen imgSalida = new Imagen(ancho, alto, imgEntrada.getTipoActual(), imgEntrada.getNumCanales());

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int index = y * ancho + x;
                int alpha = imgEntrada.getAlpha(index);

                int rFinal = calcularInferiorGeometrico(obtenerVecindadCanal(imgEntrada, x, y, mascara, 0));
                int gFinal = calcularInferiorGeometrico(obtenerVecindadCanal(imgEntrada, x, y, mascara, 1));
                int bFinal = calcularInferiorGeometrico(obtenerVecindadCanal(imgEntrada, x, y, mascara, 2));

                imgSalida.setPixel(index, rFinal, gFinal, bFinal, alpha);
            }
        }
        return imgSalida;
    }

    //8. Filtro Inferior Armonico
    public static Imagen filtroInferiorArmonico(Imagen imgEntrada, double[][] mascara) {
        int ancho = imgEntrada.getAncho();
        int alto = imgEntrada.getAlto();
        Imagen imgSalida = new Imagen(ancho, alto, imgEntrada.getTipoActual(), imgEntrada.getNumCanales());

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int index = y * ancho + x;
                int alpha = imgEntrada.getAlpha(index);

                int rFinal = calcularInferiorArmonico(obtenerVecindadCanal(imgEntrada, x, y, mascara, 0));
                int gFinal = calcularInferiorArmonico(obtenerVecindadCanal(imgEntrada, x, y, mascara, 1));
                int bFinal = calcularInferiorArmonico(obtenerVecindadCanal(imgEntrada, x, y, mascara, 2));

                imgSalida.setPixel(index, rFinal, gFinal, bFinal, alpha);
            }
        }
        return imgSalida;
    }
    //9. ALFA TRIMMED (MEDIA ORDENADA)
    public static Imagen filtroAlfaTrimmed(Imagen imgEntrada, double[][] mascara, int P) {
        int ancho = imgEntrada.getAncho();
        int alto = imgEntrada.getAlto();
        Imagen imgSalida = new Imagen(ancho, alto, imgEntrada.getTipoActual(), imgEntrada.getNumCanales());

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int index = y * ancho + x;
                int alpha = imgEntrada.getAlpha(index);

                int rFinal = calcularAlfaTrimmed(obtenerVecindadCanal(imgEntrada, x, y, mascara, 0), P);
                int gFinal = calcularAlfaTrimmed(obtenerVecindadCanal(imgEntrada, x, y, mascara, 1), P);
                int bFinal = calcularAlfaTrimmed(obtenerVecindadCanal(imgEntrada, x, y, mascara, 2), P);

                imgSalida.setPixel(index, rFinal, gFinal, bFinal, alpha);
            }
        }
        return imgSalida;
    }
    //10. FILTRO INFERIOR CONTRA ARMÓNICO
    public static Imagen filtroContraArmonico(Imagen imgEntrada, double[][] mascara, double P) {
        int ancho = imgEntrada.getAncho();
        int alto = imgEntrada.getAlto();
        Imagen imgSalida = new Imagen(ancho, alto, imgEntrada.getTipoActual(), imgEntrada.getNumCanales());

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int index = y * ancho + x;
                int alpha = imgEntrada.getAlpha(index);

                int rFinal = calcularContraArmonico(obtenerVecindadCanal(imgEntrada, x, y, mascara, 0), P);
                int gFinal = calcularContraArmonico(obtenerVecindadCanal(imgEntrada, x, y, mascara, 1), P);
                int bFinal = calcularContraArmonico(obtenerVecindadCanal(imgEntrada, x, y, mascara, 2), P);

                imgSalida.setPixel(index, rFinal, gFinal, bFinal, alpha);
            }
        }
        return imgSalida;
    }
    //metrica unica:
    // Variables globales de la clase (ponlas justo arriba de la función en tu clase de Filtros)
    private static int pixelesRuidososIniciales = 0;
    private static double porcentajeRuidoInicial = 0.0;


    public static void calcularMetricasRuidoFiltro(Imagen imgOriginal, Imagen imgProcesada) {
        int ancho = imgOriginal.getAncho();
        int alto = imgOriginal.getAlto();
        int totalPixeles = ancho * alto;
        int pixelesModificados = 0;

        int[] pixOrig = imgOriginal.getPixeles();
        int[] pixProc = imgProcesada.getPixeles();

        for (int i = 0; i < totalPixeles; i++) {
            // Comparamos si el píxel cambió en cualquiera de sus canales RGB
            if (pixOrig[i] != pixProc[i]) {
                pixelesModificados++;
            }
        }

        double porcentajeActual = ((double) pixelesModificados / totalPixeles) * 100.0;

        String mensaje;

        // --- DETECCIÓN AUTOMÁTICA DE LA ETAPA ---
        if (porcentajeActual > 5.0 && pixelesRuidososIniciales == 0) {

            // Guardamos los datos del ruido en la memoria para usarlos después
            pixelesRuidososIniciales = pixelesModificados;
            porcentajeRuidoInicial = porcentajeActual;

            // Mensaje enfocado en los puntos 3.1 y 3.2
            mensaje = String.format(
                    "<html>" +
                            "<h2 style='color:#003366;'>Métricas: Etapa de Ruido Aplicado</h2>" +
                            "<hr>" +
                            "<p><b>Píxeles totales de la imagen:</b> %,d</p>" +
                            "<p style='color:#CC6600;'><b>3.1 Píxeles modificados por ruido:</b> %,d</p>" +
                            "<p><b>3.2 Porcentaje inicial de ruido:</b> <b><span style='color:red;'>%.2f%%</span></b></p>" +
                            "<br><p style='font-size:9px; color:gray;'>*Datos almacenados para calcular la eficiencia tras aplicar el filtro.</p>" +
                            "</html>",
                    totalPixeles, pixelesModificados, porcentajeActual
            );

        } else {
            // Si entra aquí, significa que el usuario aplicó un FILTRO para limpiar la imagen

            // Calculamos el Punto 3.4: Reducción/Eficiencia absoluta
            double eficienciaAbsoluta = porcentajeRuidoInicial - porcentajeActual;
            if (eficienciaAbsoluta < 0) eficienciaAbsoluta = 0;

            // --- CORRECCIÓN DE ORDEN DE VARIABLES ---
            mensaje = String.format(
                    "<html>" +
                            "<h2 style='color:#006633;'>Métricas: Etapa de Filtro Aplicado</h2>" +
                            "<hr>" +
                            "<table border='0' cellpadding='2'>" +
                            "<tr><td><b>Píxeles totales:</b></td><td>%,d</td></tr>" + // -> totalPixeles
                            "<tr><td style='color:gray;'>3.1 Píxeles c/ ruido inicial:</td><td style='color:gray;'>%,d (%.2f%%)</td></tr>" + // -> pixelesRuidososIniciales, porcentajeRuidoInicial
                            "<tr><td><b>3.3 Píxeles ruidosos tras filtro:</b></td><td><b>%,d</b></td></tr>" + // -> pixelesModificados
                            "<tr><td><b>3.3 %% de ruido remanente:</b></td><td><b><span style='color:red;'>%.2f%%</span></b></td></tr>" + // -> porcentajeActual
                            "</table>" +
                            "<hr>" +
                            "<h3 style='color:#006633;'>3.4 Reducción / Eficiencia Absoluta:</h3>" +
                            "<p style='font-size:14px;'>¡El filtro redujo el ruido en un <b><span style='color:green;'>%.2f%%</span></b> respecto al inicio!</p>" + // -> eficienciaAbsoluta
                            "</html>",
                    totalPixeles,
                    pixelesRuidososIniciales,
                    porcentajeRuidoInicial,
                    pixelesModificados,
                    porcentajeActual,
                    eficienciaAbsoluta
            );

            // Al terminar el ciclo completo de Filtro, reiniciamos la memoria para el siguiente ruido
            pixelesRuidososIniciales = 0;
            porcentajeRuidoInicial = 0.0;
        }

        // --- DESPLIEGUE DEL RECUADRO FLOTANTE ---
        JOptionPane.showMessageDialog(
                null,
                mensaje,
                "Análisis de Métricas (Puntos 3.1 a 3.4) - ESCOM",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
