package org.pdiProject.graficWindow;

import javax.swing.*;
import java.awt.*;

public class HistogramChart extends JPanel {
    private int[] datos;
    private Color colorBarra;
    // Márgenes para que quepan los textos de los ejes
    private final int MARGEN_IZQ = 50;
    private final int MARGEN_INF = 30;
    private final int MARGEN_SUP = 10;
    private final int MARGEN_DER = 10;

    public HistogramChart(int[] datos, Color colorBarra) {
        this.datos = datos;
        this.colorBarra = colorBarra;
        this.setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (datos == null || datos.length == 0) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int anchoTotal = getWidth();
        int altoTotal = getHeight();

        // Área útil para el gráfico
        int anchoGrafico = anchoTotal - MARGEN_IZQ - MARGEN_DER;
        int altoGrafico = altoTotal - MARGEN_INF - MARGEN_SUP;

        // 1. Encontrar el valor máximo para escalar
        int maxVal = 0;
        for (int v : datos) if (v > maxVal) maxVal = v;
        if (maxVal == 0) maxVal = 1;

        // 2. Dibujar Ejes (Líneas base)
        g2.setColor(Color.BLACK);
        // Eje Y
        g2.drawLine(MARGEN_IZQ, MARGEN_SUP, MARGEN_IZQ, altoTotal - MARGEN_INF);
        // Eje X
        g2.drawLine(MARGEN_IZQ, altoTotal - MARGEN_INF, anchoTotal - MARGEN_DER, altoTotal - MARGEN_INF);

        // 3. Dibujar Etiquetas de Intensidad (Eje X: 0, 128, 255)
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        String[] etiquetasX = {"0", "128", "255"};
        for (int i = 0; i < etiquetasX.length; i++) {
            int val = (i == 0) ? 0 : (i == 1 ? 128 : 255);
            int x = MARGEN_IZQ + (int)(val * (double)anchoGrafico / 255);
            g2.drawString(etiquetasX[i], x - 5, altoTotal - MARGEN_INF + 15);
            g2.drawLine(x, altoTotal - MARGEN_INF, x, altoTotal - MARGEN_INF + 5); // Pequeña marca
        }

        // 4. Dibujar Etiqueta de Frecuencia Máxima (Eje Y)
        g2.drawString(String.valueOf(maxVal), 5, MARGEN_SUP + 10);

        // 5. Dibujar las barras del Histograma
        g2.setColor(colorBarra);
        for (int i = 0; i < datos.length; i++) {
            if (datos[i] == 0) continue;

            int x = MARGEN_IZQ + (int) (i * (double) anchoGrafico / datos.length);
            int alturaBarra = (int) ((double) datos[i] / maxVal * altoGrafico);

            // Dibujamos la barra (el +1 en el ancho es para que no queden huecos blancos)
            g2.drawLine(x, altoTotal - MARGEN_INF, x, altoTotal - MARGEN_INF - alturaBarra);
        }

        // Opcional: Una cuadrícula tenue de fondo
        g2.setColor(new Color(230, 230, 230));
        g2.drawLine(MARGEN_IZQ, MARGEN_SUP + altoGrafico / 2, anchoTotal - MARGEN_DER, MARGEN_SUP + altoGrafico / 2);
    }
}