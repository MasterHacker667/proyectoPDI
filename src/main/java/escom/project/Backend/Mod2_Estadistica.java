package escom.project.Backend;

import escom.project.Backend.Imagen.Imagen;
import escom.project.Backend.Imagen.Tipo;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class Mod2_Estadistica {
    public void generarHistograma(Imagen img) {
        // 1. Crear la ventana (JFrame)
        JFrame ventanaHisto = new JFrame("Análisis Espectral - Tipo: " + img.getTipoActual());

        // Determinamos cuántos canales graficar
        int numCanales = img.getNumCanales();

        // Configuramos el Grid según la cantidad de canales (2x2 es lo más común)
        ventanaHisto.setLayout(new GridLayout(2, 2, 10, 10));

        // Nombres de etiquetas según el espacio de color
        String[] etiquetas = obtenerEtiquetasSegunTipo(img.getTipoActual());
        Color[] colores = obtenerColoresSegunTipo(img.getTipoActual());

        for (int c = 0; c < numCanales; c++) {
            // A. Obtener el conteo de frecuencias para este canal
            int[] frecuencias = calcularFrecuencias(img, c);

            // B. Crear el Dataset para JFreeChart
            XYSeries series = new XYSeries(etiquetas[c]);
            for (int i = 0; i < frecuencias.length; i++) {
                series.add(i, frecuencias[i]);
            }
            XYSeriesCollection dataset = new XYSeriesCollection(series);

            // C. Crear el gráfico de área o barras
            JFreeChart chart = ChartFactory.createXYAreaChart(
                    etiquetas[c], // Título
                    "Intensidad", // Eje X
                    "Píxeles",    // Eje Y
                    dataset,
                    PlotOrientation.VERTICAL,
                    false, true, false
            );

            // D. Personalizar color
            chart.getXYPlot().getRenderer().setSeriesPaint(0, colores[c]);

            // E. Añadir al panel
            ventanaHisto.add(new ChartPanel(chart));
        }

        ventanaHisto.pack();
        ventanaHisto.setSize(800, 600);
        ventanaHisto.setLocationRelativeTo(null);
        ventanaHisto.setVisible(true);
    }
    public double[][] calcularProbabilidad(Imagen img) {
        int numCanales = img.getNumCanales();
        int totalPixeles = img.getAncho() * img.getAlto();
        double[][] probabilidadesPorCanal = new double[numCanales][256];

        // Nombres dinámicos según el tipo de imagen (RGB, YIQ, etc.)
        String[] nombresCanales = obtenerEtiquetasSegunTipo(img.getTipoActual());

        // Preparar el contenedor principal de la ventana
        JFrame ventana = new JFrame("Análisis de Probabilidades - " + img.getTipoActual());
        ventana.setLayout(new BorderLayout());

        // Panel central para las gráficas (2x2)
        JPanel panelGraficas = new JPanel(new GridLayout(2, 2));

        // Panel derecho para la lista de texto (con scroll)
        JTextArea areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollTexto = new JScrollPane(areaTexto);
        scrollTexto.setPreferredSize(new Dimension(250, 0));

        for (int c = 0; c < numCanales; c++) {
            // 1. Obtener histograma base
            int[] histograma = generarHistogramaSimple(img, c);

            // 2. Calcular probabilidades y llenar el área de texto
            areaTexto.append("--- CANAL: " + nombresCanales[c] + " ---\n");
            XYSeries serie = new XYSeries(nombresCanales[c]);

            for (int i = 0; i < 256; i++) {
                probabilidadesPorCanal[c][i] = (double) histograma[i] / totalPixeles;

                // Solo agregamos al texto intensidades que sí existen para no saturar
                if (histograma[i] > 0) {
                    areaTexto.append(String.format("Intensidad %3d: %.6f\n", i, probabilidadesPorCanal[c][i]));
                }

                // Añadir a la serie de la gráfica
                serie.add(i, probabilidadesPorCanal[c][i]);
            }
            areaTexto.append("\n");

            // 3. Crear gráfica de probabilidad (Línea o Área)
            JFreeChart chart = ChartFactory.createXYLineChart(
                    "PDF: " + nombresCanales[c],
                    "Intensidad", "P(x)",
                    new XYSeriesCollection(serie),
                    PlotOrientation.VERTICAL, false, true, false);

            // Personalizar color
            chart.getXYPlot().getRenderer().setSeriesPaint(0, obtenerColoresSegunTipo(img.getTipoActual())[c]);
            panelGraficas.add(new ChartPanel(chart));
        }
        JPanel panelFooter = new JPanel(new GridLayout(img.getNumCanales(), 1));
        panelFooter.setBorder(BorderFactory.createTitledBorder("Resumen Estadístico"));

        double[] medias = calcularMedia(img);
        double[] varianzas = calcularVarianza(img, medias);
        String[] nombres = obtenerEtiquetasSegunTipo(img.getTipoActual());
        double [] asimetria = this.calcularAsimetria(img, medias, varianzas);
        double [] curtosis = this.calcularCurtosis(img, medias, varianzas);
        double [] densidadPontencia = this.calcularDensidadPotencia(img);

        for (int c = 0; c < img.getNumCanales(); c++) {
            double desviacion = Math.sqrt(varianzas[c]);
            JLabel labelEst = new JLabel(String.format(
                    "  %s -> Media: %.2f | Varianza: %.2f | Desv. Estándar: %.2f | Asimetría: %.2f | Curtosis: %.2f | Densidad de Potencia: %.2f ",
                    nombres[c], medias[c], varianzas[c], desviacion, asimetria[c], curtosis[c], densidadPontencia[c]
            ));
            labelEst.setFont(new Font("SansSerif", Font.BOLD, 12));
            panelFooter.add(labelEst);
        }

        ventana.add(panelFooter, BorderLayout.SOUTH);

        ventana.add(panelGraficas, BorderLayout.CENTER);
        ventana.add(scrollTexto, BorderLayout.EAST);
        ventana.pack();
        ventana.setSize(1000, 600);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);

        return probabilidadesPorCanal;
    }
    // 1. Densidad Acumulada (Suma progresiva de probabilidades)
    public double[][] calcularDensidadAcumulada(Imagen img) {
        double[][] prob = calcularProbabilidadSilenciosa(img); // Método que solo calcula sin abrir ventana
        double[][] acumulada = new double[img.getNumCanales()][256];

        for (int c = 0; c < img.getNumCanales(); c++) {
            acumulada[c][0] = prob[c][0];
            for (int i = 1; i < 256; i++) {
                acumulada[c][i] = acumulada[c][i - 1] + prob[c][i];
            }
        }
        return acumulada;
    }

    // 2. Media (μ): Σ (intensidad * probabilidad)
    public double[] calcularMedia(Imagen img) {
        double[][] prob = calcularProbabilidadSilenciosa(img);
        double[] medias = new double[img.getNumCanales()];

        for (int c = 0; c < img.getNumCanales(); c++) {
            for (int i = 0; i < 256; i++) {
                medias[c] += i * prob[c][i];
            }
        }
        return medias;
    }

    // 3. Varianza (σ²): Σ ((intensidad - media)² * probabilidad)
    public double[] calcularVarianza(Imagen img, double[] medias) {
        double[][] prob = calcularProbabilidadSilenciosa(img);
        double[] varianzas = new double[img.getNumCanales()];

        for (int c = 0; c < img.getNumCanales(); c++) {
            for (int i = 0; i < 256; i++) {
                varianzas[c] += Math.pow(i - medias[c], 2) * prob[c][i];
            }
        }
        return varianzas;
    }
    // 1. Asimetría (Skewness): Indica si el histograma está "inclinado"
    public double[] calcularAsimetria(Imagen img, double[] medias, double[] varianzas) {
        double[][] prob = calcularProbabilidadSilenciosa(img);
        double[] asimetrias = new double[img.getNumCanales()];

        for (int c = 0; c < img.getNumCanales(); c++) {
            double desvEst = Math.sqrt(varianzas[c]);
            if (desvEst == 0) continue; // Evitar división por cero en imágenes planas

            double sumaCubica = 0;
            for (int i = 0; i < 256; i++) {
                sumaCubica += Math.pow(i - medias[c], 3) * prob[c][i];
            }
            asimetrias[c] = sumaCubica / Math.pow(desvEst, 3);
        }
        return asimetrias;
    }

    // 2. Curtosis: Indica qué tan "puntiagudo" es el histograma
    public double[] calcularCurtosis(Imagen img, double[] medias, double[] varianzas) {
        double[][] prob = calcularProbabilidadSilenciosa(img);
        double[] curtosis = new double[img.getNumCanales()];

        for (int c = 0; c < img.getNumCanales(); c++) {
            double desvEst = Math.sqrt(varianzas[c]);
            if (desvEst == 0) continue;

            double sumaCuarta = 0;
            for (int i = 0; i < 256; i++) {
                sumaCuarta += Math.pow(i - medias[c], 4) * prob[c][i];
            }
            // Restamos 3 para que una distribución normal sea 0 (Curtosis de exceso)
            curtosis[c] = (sumaCuarta / Math.pow(desvEst, 4)) - 3;
        }
        return curtosis;
    }

    // 3. Densidad de Potencia (Energía): Qué tan uniforme es la distribución
    public double[] calcularDensidadPotencia(Imagen img) {
        double[][] prob = calcularProbabilidadSilenciosa(img);
        double[] potencias = new double[img.getNumCanales()];

        for (int c = 0; c < img.getNumCanales(); c++) {
            for (int i = 0; i < 256; i++) {
                potencias[c] += Math.pow(prob[c][i], 2);
            }
        }
        return potencias;
    }
    //-----------------------------------------------EXTRAS-----------------------------------------------
    // Determina los nombres de los ejes según el modelo
    private String[] obtenerEtiquetasSegunTipo(Tipo tipo) {
        return switch (tipo) {
            case RGB, RGBA -> new String[]{"Rojo", "Verde", "Azul", "Alpha"};
            case YIQ -> new String[]{"Luminancia (Y)", "In-phase (I)", "Quadrature (Q)"};
            case HSV -> new String[]{"Matiz (H)", "Saturación (S)", "Valor (V)"};
            case LAB -> new String[]{"Luminosidad (L)", "Eje a", "Eje b"};
            default -> new String[]{"Gris", "Canal 2", "Canal 3", "Canal 4"};
        };
    }

    // Determina los colores de las gráficas
    private Color[] obtenerColoresSegunTipo(Tipo tipo) {
        if (tipo == Tipo.RGB || tipo == Tipo.RGBA) {
            return new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.DARK_GRAY};
        }
        // Para otros modelos usamos una paleta estándar
        return new Color[]{Color.BLACK, Color.ORANGE, Color.CYAN, Color.MAGENTA};
    }

    // Realiza el conteo de píxeles (Histograma tradicional)
    private int[] calcularFrecuencias(Imagen img, int canal) {
        int[] histo = new int[256];
        for (int i = 0; i < img.getPixeles().length; i++) {
            int valor = switch(canal) {
                case 0 -> img.getRed(i);
                case 1 -> img.getGreen(i);
                case 2 -> img.getBlue(i);
                default -> img.getAlpha(i);
            };
            // Clamping por seguridad
            if (valor >= 0 && valor < 256) histo[valor]++;
        }
        return histo;
    }
    /**
     * Genera el conteo de frecuencias (histograma) de un canal específico
     * sin disparar ninguna interfaz gráfica.
     */
    private int[] generarHistogramaSimple(Imagen img, int canal) {
        int[] histo = new int[256];
        int[] pixeles = img.getPixeles(); // Suponiendo que devuelve el buffer ARGB

        for (int i = 0; i < pixeles.length; i++) {
            int valor = 0;

            // Extraemos el valor según el canal solicitado
            // 0: Rojo, 1: Verde, 2: Azul, 3: Alpha o Gris
            switch (canal) {
                case 0 -> valor = img.getRed(i);
                case 1 -> valor = img.getGreen(i);
                case 2 -> valor = img.getBlue(i);
                case 3 -> {
                    // Si la imagen tiene 4 canales usamos Alpha,
                    // si no, podemos usar el promedio (Gris)
                    if (img.getNumCanales() > 3) {
                        valor = img.getAlpha(i);
                    } else {
                        valor = (int) (img.getRed(i) * 0.299 +
                                img.getGreen(i) * 0.587 +
                                img.getBlue(i) * 0.114);
                    }
                }
                default -> valor = 0;
            }

            // Clamping preventivo: aseguramos que el índice esté entre 0 y 255
            if (valor < 0) valor = 0;
            if (valor > 255) valor = 255;

            histo[valor]++;
        }

        return histo;
    }
    private double[][] calcularProbabilidadSilenciosa(Imagen img) {
        int numCanales = img.getNumCanales();
        int totalPixeles = img.getAncho() * img.getAlto();
        double[][] probabilidades = new double[numCanales][256];

        for (int c = 0; c < numCanales; c++) {
            // Usamos el histograma simple que ya habías hecho
            int[] histograma = generarHistogramaSimple(img, c);

            for (int i = 0; i < 256; i++) {
                probabilidades[c][i] = (double) histograma[i] / totalPixeles;
            }
        }
        return probabilidades;
    }
}

