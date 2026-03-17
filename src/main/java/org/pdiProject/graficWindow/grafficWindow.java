package org.pdiProject.graficWindow;

import org.pdiProject.image.Imagen;

import javax.swing.*;
import java.awt.*;

public class grafficWindow extends JFrame {
    public grafficWindow(){
        this.setTitle("Software PDI");
        this.setSize(1200, 800);

        this.setLayout(new BorderLayout(5, 5));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    public boolean isGrayScale(Imagen imagen) {
        int numCanales = imagen.getnoCanales();
        // Si solo tiene 1 canal, por definición es escala de grises
        if (numCanales == 1) return true;
        // Si tiene menos de 3 (y no es 1), no podemos compararlo como RGB
        if (numCanales < 3) return false;

        byte[] pixeles = imagen.getPixelesImagen();

        // Analizamos los píxeles
        for (int i = 0; i < pixeles.length; i += numCanales) {
            int r = pixeles[i] & 0xFF;
            int g = pixeles[i + 1] & 0xFF;
            int b = pixeles[i + 2] & 0xFF;

            // Si detectamos que UN solo píxel tiene colores diferentes,
            // ya no es una imagen en escala de grises pura.
            if (r != g || g != b) {
                return false;
            }
        }
        return true; // Si terminó el ciclo sin salir, es gris
    }
    public boolean isBinary(Imagen imagen) {
        byte[] pixeles = imagen.getPixelesImagen();
        int numCanales = imagen.getnoCanales();

        for (int i = 0; i < pixeles.length; i += numCanales) {
            // Solo revisamos el primer canal (o el canal de intensidad)
            // porque ya sabemos por la lógica previa si es gris o RGB
            int valor = pixeles[i] & 0xFF;

            if (valor != 0 && valor != 255) {
                return false; // Encontró un tono intermedio
            }
        }
        return true; // Solo hay 0s y 255s
    }
    //SI no es garyScale o Binary, entonces es una de 3 o mas canales

    public void histogramaElect(Imagen imagen){
        //Ya que sabemos que nos pasaron, debemos averiguar que imagen es:
        /*
        * El escala de grises tiene sus 3 canales iguales
        * El RGB o cualquier otro, tiene sus 3 canales no necesariamente iguales
        * Si tiene 4 o mas canales, entonces se grafican todos canal por canal.
        * Para saber si es RGB o algun otro, debemos recibirlo de los datos de entrada4
        * Debemos saber si es escala de grisis, binaria o alguna otra de 3 o mas canales que son diferentes.
        *
        * Despues de dsaber que imagen es, debemos graficar todos sus canales:
        *   Si es un solo canal, solo es un histograma, si es de dos, tambien.
        *   Solo las imagenes escala de grises y binarias tienen un solo canal.
        * Pondremos un seguro, donde el maximo de histogramas/graficos permitidos en un panel seran 4.
        *
        * */
        boolean esGris = isGrayScale(imagen);
        if (!esGris) {
            // Si NO es gris, es color (RGB/RGBA), incluso si solo usa 0s y 255s
            this.graficarImagenColores(imagen);
        } else {
            // Si ES gris (R=G=B), entonces verificamos si es binaria o gris normal
            if (this.isBinary(imagen)) {
                this.graficarImagenBinaria(imagen);
            } else {
                this.graficarImagenGris(imagen);
            }
        }
    }
    public void histogramaElect(Imagen imagen, int tipoImagen){
       //Lo mismo pero, si es de colores, debemos definir de que canales se habla: RGB,
    }
    private void graficarImagenGris(Imagen imagen) {
        // 1. Limpiar el contenedor antes de dibujar
        this.getContentPane().removeAll();

        int numCanales = imagen.getnoCanales();
        byte[] pixeles = imagen.getPixelesImagen();

        // 2. Preparar los datos.
        // Si es gris, solo nos interesan el canal 0 (Intensidad)
        // y el canal 3 (Alpha/Extra) si es que existe.
        int canalIntensidad = 0;
        int canalExtra = 3;

        int[] histoGris = new int[256];
        int[] histoExtra = new int[256];
        boolean tieneExtra = (numCanales >= 4);

        // 3. Recorrer píxeles para llenar los histogramas
        for (int i = 0; i < pixeles.length; i += numCanales) {
            int valorGris = pixeles[i + canalIntensidad] & 0xFF;
            histoGris[valorGris]++;

            if (tieneExtra) {
                int valorExtra = pixeles[i + canalExtra] & 0xFF;
                histoExtra[valorExtra]++;
            }
        }

        // 4. Configurar el Layout
        // Si hay canal extra, usamos 1x2, si no, 1x1.
        if (tieneExtra) {
            this.setLayout(new GridLayout(1, 2, 10, 10));
        } else {
            this.setLayout(new GridLayout(1, 1));
        }

        // 5. Agregar los gráficos al JFrame
        // Usamos el color DARK_GRAY para la intensidad y MAGENTA para el extra
        agregarGrafico(histoGris, Color.DARK_GRAY, "Histograma de Intensidad (Gris)");

        if (tieneExtra) {
            agregarGrafico(histoExtra, Color.MAGENTA, "Canal Extra / Alpha");
        }

        // 6. Refrescar la ventana
        this.revalidate();
        this.repaint();

        System.out.println("Graficando Escala de Grises. Canales totales: " + numCanales);
    }

    private void graficarImagenBinaria(Imagen imagen) {
        // 1. Limpiar el contenedor
        this.getContentPane().removeAll();

        int numCanales = imagen.getnoCanales();
        byte[] pixeles = imagen.getPixelesImagen();

        // 2. Preparar los datos
        // Usamos el canal 0 para la información binaria
        int[] histoBinario = new int[256];
        int[] histoExtra = new int[256];
        boolean tieneExtra = (numCanales >= 4);

        // 3. Llenar histogramas
        for (int i = 0; i < pixeles.length; i += numCanales) {
            int valorBinario = pixeles[i] & 0xFF;
            histoBinario[valorBinario]++;

            if (tieneExtra) {
                int valorExtra = pixeles[i + 3] & 0xFF; // Canal 4 (Alpha)
                histoExtra[valorExtra]++;
            }
        }

        // 4. Configurar el Layout dinámico
        if (tieneExtra) {
            this.setLayout(new GridLayout(1, 2, 10, 10));
        } else {
            this.setLayout(new GridLayout(1, 1));
        }

        // 5. Agregar gráficos
        // Usamos Negro para diferenciarlo visualmente de la escala de grises
        agregarGrafico(histoBinario, Color.BLACK, "Histograma Binario (0 - 255)");

        if (tieneExtra) {
            // El canal extra se mantiene en Magenta para consistencia
            agregarGrafico(histoExtra, Color.MAGENTA, "Canal Extra (Alpha)");
        }

        // 6. Refrescar interfaz
        this.revalidate();
        this.repaint();

        System.out.println("Graficando Imagen Binaria. Canales totales: " + numCanales);
    }
    private void graficarImagenColores(Imagen imagen) {
        // 1. Limpiar el contenedor
        this.getContentPane().removeAll();

        int numCanales = imagen.getnoCanales();
        byte[] pixeles = imagen.getPixelesImagen();

        // 2. Preparar los datos para los 3 canales base + el extra
        int[][] frecuencias = new int[4][256];
        boolean tieneExtra = (numCanales >= 4);

        // 3. Recorrido de píxeles (Llenamos todos los canales en un solo ciclo para ser rápidos)
        for (int i = 0; i < pixeles.length; i += numCanales) {
            // Canal 0: Rojo, Canal 1: Verde, Canal 2: Azul
            frecuencias[0][pixeles[i] & 0xFF]++;
            frecuencias[1][pixeles[i + 1] & 0xFF]++;
            frecuencias[2][pixeles[i + 2] & 0xFF]++;

            if (tieneExtra) {
                frecuencias[3][pixeles[i + 3] & 0xFF]++;
            }
        }

        // 4. Configurar Layout 2x2 para los 4 posibles gráficos
        this.setLayout(new GridLayout(2, 2, 10, 10));

        // 5. Agregar los 3 canales RGB con sus colores correspondientes
        agregarGrafico(frecuencias[0], Color.RED, "Canal Rojo (R)");
        agregarGrafico(frecuencias[1], new Color(0, 150, 0), "Canal Verde (G)"); // Un verde más oscuro para que se vea bien
        agregarGrafico(frecuencias[2], Color.BLUE, "Canal Azul (B)");

        // 6. Si tiene canal extra (como Alpha), lo agregamos en el 4to espacio
        if (tieneExtra) {
            agregarGrafico(frecuencias[3], Color.MAGENTA, "Canal Extra / Alpha");
        } else {
            // Si no hay extra, agregamos un panel vacío para mantener la estructura del Grid
            this.add(new JPanel());
        }

        // 7. Refrescar interfaz
        this.revalidate();
        this.repaint();

        System.out.println("Graficando Imagen a Color. Canales detectados: " + numCanales);
    }
    private void agregarGrafico(int[] datos, Color colorCanal, String titulo) {
        // 1. Creamos el contenedor individual para este histograma
        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 2. Creamos el componente que dibuja (el que definimos al principio)
        // Si aún no tienes la clase HistogramChart, avísame para pasártela completa
        HistogramChart grafico = new HistogramChart(datos, colorCanal);

        // 3. Etiqueta superior para saber qué estamos viendo
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));

        // 4. Armamos el panel
        panelContenedor.add(lblTitulo, BorderLayout.NORTH);
        panelContenedor.add(grafico, BorderLayout.CENTER);

        // 5. Lo añadimos al JFrame (grafficWindow)
        this.add(panelContenedor);
    }
    private double[][] calcularEstadisticascompletas(int[] histograma, int totalPixeles) {
        double[] probabilidad = new double[256];
        double[] densidad = new double[256];

        for (int i = 0; i < 256; i++) {
            probabilidad[i] = (double) histograma[i] / totalPixeles; // Punto 4
            densidad[i] = Math.pow(probabilidad[i], 2);             // Punto 5
        }
        return new double[][]{probabilidad, densidad};
    }
}
