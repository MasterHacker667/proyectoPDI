package org.pdiProject;

import org.pdiProject.graficWindow.grafficWindow;
import org.pdiProject.image.Imagen;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TransformationPanels extends JPanel {
    private Software padre;
    private grafficWindow graffic;
    private double[] bufferLMSP3;
    public TransformationPanels(Software padre){
        this.padre = padre;
        this.graffic = new grafficWindow();
        this.bufferLMSP3 = null;
        // 1. Configuración del Layout (Vertical como en Figma)
        // Usamos BoxLayout para apilar componentes en el eje Y
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //2. Creacion de botones de aqui
        JButton btnGrises = new JButton("Escala de Grises");

        // Panel para los botones de Brillo (estos van juntos horizontalmente)
        JPanel brilloContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnBrilloMas = new JButton("Brillo +1");
        JButton btnBrilloMenos = new JButton("Brillo -1");
        brilloContainer.add(btnBrilloMas);
        brilloContainer.add(btnBrilloMenos);

        JButton btnContraste = new JButton("Contraste");
        //JButton punto 2 de practica 2.
        JButton btnpunto22 = new JButton("Realizar transoformacion a color de grises");

        //JBUtton practica 3:
        JButton btnlab = new JButton("Realizar transformacion lαβ");
        JButton btnlabGris = new JButton("Extraer los canales en niveles de gris");



        // 3. Alineación al centro del panel
        btnGrises.setAlignmentX(Component.CENTER_ALIGNMENT);
        brilloContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContraste.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnpunto22.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnlab.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnlabGris.setAlignmentX(Component.CENTER_ALIGNMENT);


        //4. Espacio para cuando querramos dar funcionalidad a los botones
        btnGrises.addActionListener(e -> {
            this.changeImagePanels(1);
        });
        btnBrilloMas.addActionListener(e -> {
            this.changeImagePanels(2); //aumentar 1 cada pixel de la imagen en todos los canales
        });
        btnBrilloMenos.addActionListener(e -> {
            this.changeImagePanels(3); //aumentar 1 cada pixel de la imagen en todos los canales
        });
        //Botones de practica 2:
        btnpunto22.addActionListener(e -> {
            this.changeImagePanels(4); //Hacer la transoformacion dependiendo de la imagen:
        });
        //Botones de practica 3:
        btnlab.addActionListener(e -> {
            //Hacer la transoformacion de imagen
            this.changeImagePanels(5);
        });
        btnlabGris.addActionListener(e -> {
            this.changeImagePanels(6);
        });

        //5. Agregar botones
        this.add(btnGrises);
        this.add(Box.createRigidArea(new Dimension(0, 20))); // Espacio de 20px
        this.add(brilloContainer);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(btnContraste);
        this.add(Box.createVerticalGlue());
        this.add(Box.createRigidArea(new Dimension(0, 20))); // Espacio de 20px
        this.add(btnpunto22);
        this.add(Box.createRigidArea(new Dimension(0, 20))); // Espacio de 20px
        this.add(btnlab);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(btnlabGris);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    public void changeImagePanels(int opc){
        if(this.padre.gethayImagen()){
            if(this.padre.getPanel()){
                //Codigo para cambiar la imagen original y poner el resultado en result
                if(opc == 1) this.escalaGrises(this.padre.getImagenOriginal());
                if(opc == 2) this.brilloMas(this.padre.getImagenOriginal(), true);
                if(opc == 3) this.brilloMas(this.padre.getImagenOriginal(), false);
                if(opc==4) this.transoformacion22(this.padre.getImagenOriginal());
                if(opc == 5) this.transformacioneslab(this.padre.getImagenOriginal());
                if(opc == 6) {
                    if(this.bufferLMSP3 != null){
                        String[] canales = {"Canal l (Luminosidad)", "Canal α (Amarillo-Azul)", "Canal β (Rojo-Verde)"};
                        int seleccion = javax.swing.JOptionPane.showOptionDialog(
                                null,
                                "Selecciona el canal que deseas visualizar en escala de grises:",
                                "Punto 4: Extracción de Canales Perceptuales",
                                javax.swing.JOptionPane.DEFAULT_OPTION,
                                javax.swing.JOptionPane.PLAIN_MESSAGE,
                                null,
                                canales,
                                canales[0]
                        );
                        if (seleccion != -1) {
                            // seleccion será: 0 para l, 1 para alpha, 2 para beta
                            this.extraerCanalGrisLab(this.padre.getImagenOriginal(), this.bufferLMSP3, seleccion);
                        }else{
                            this.extraerCanalGrisLab(this.padre.getImagenOriginal(), this.bufferLMSP3, 0);
                        }

                    }else{
                        JOptionPane.showMessageDialog(this, "Buffer vacio. Debes presionarl el boton de arriba primero");
                    }

                }

            } else if(!this.padre.getHayResult()){
                JOptionPane.showMessageDialog(this, "No hay ninguna imagen Result para hacer esta operacion");
            }else{
                //Codigo para cambiar la result y reemplazarlo por un result nuevo
                if(opc == 1) this.escalaGrises(this.padre.getImagenResult());
                if(opc == 2) this.brilloMas(this.padre.getImagenResult(), true);
                if(opc == 3) this.brilloMas(this.padre.getImagenResult(), false);
                if(opc == 4) this.transoformacion22(this.padre.getImagenResult());
                if(opc == 5) this.transformacioneslab(this.padre.getImagenResult());
                if(opc == 6){
                    if(this.bufferLMSP3 != null){
                        String[] canales = {"Canal l (Luminosidad)", "Canal α (Amarillo-Azul)", "Canal β (Rojo-Verde)"};
                        int seleccion = javax.swing.JOptionPane.showOptionDialog(
                                null,
                                "Selecciona el canal que deseas visualizar en escala de grises:",
                                "Punto 4: Extracción de Canales Perceptuales",
                                javax.swing.JOptionPane.DEFAULT_OPTION,
                                javax.swing.JOptionPane.PLAIN_MESSAGE,
                                null,
                                canales,
                                canales[0]
                        );
                        if (seleccion != -1) {
                            // seleccion será: 0 para l, 1 para alpha, 2 para beta
                            this.extraerCanalGrisLab(this.padre.getImagenResult(), this.bufferLMSP3, seleccion);
                        }else{
                            this.extraerCanalGrisLab(this.padre.getImagenResult(), this.bufferLMSP3, 0);
                        }

                    }else{
                        JOptionPane.showMessageDialog(this, "Buffer vacio. Debes presionarl el boton de arriba primero");
                    }
                }
            }
        }else{
            JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
        }

    }
    public void extraerCanalGrisLab(Imagen img, double[] bufferLAB, int tipoCanal) {
        // 1. Preparación de dimensiones
        int ancho = img.getAnchoLargo()[0];
        int largo = img.getAnchoLargo()[1];
        int numPixeles = ancho * largo;
        byte[] pixelesGris = new byte[numPixeles * 3];

        // 2. Procesamiento de cada píxel
        for (int i = 0; i < numPixeles; i++) {
            int index = i * 3;

            // Extraemos el valor del canal seleccionado (l, alpha o beta)
            double valor = bufferLAB[index + tipoCanal];

            // 3. Normalización (Paso crítico para que el ojo humano lo vea)
            int gris;
            if (tipoCanal == 0) {
                // Canal l: suele ser positivo. Multiplicamos para expandir el rango.
                gris = (int) (valor * 100);
            } else {
                // Canales alpha y beta: pueden ser negativos o positivos (oponencia).
                // Sumamos 128 para que el "cero" sea un gris medio.
                gris = (int) (valor * 100) + 128;
            }

            // 4. Asegurar el rango del byte (0 a 255)
            byte b = (byte) Math.max(0, Math.min(255, gris));

            // 5. Asignación simétrica (R=G=B) para crear el efecto de gris
            pixelesGris[index]     = b; // Canal R
            pixelesGris[index + 1] = b; // Canal G
            pixelesGris[index + 2] = b; // Canal B
        }

        // 6. Empaquetado y entrega al padre
        Imagen result = new Imagen(img.getNombreImagen() + "_Gris", 3, ancho, largo, pixelesGris);
        this.padre.setImagenResult(result);
    }
    public void transformacioneslab(Imagen img) {
        int ancho = img.getAnchoLargo()[0];
        int largo = img.getAnchoLargo()[1];
        int numPixeles = ancho * largo;

        // 1. Buffer de doubles para el procesamiento (Memoria intermedia)
        double[] bufferLMS = new double[numPixeles * 3];

        // --- BLOQUE DE CÁLCULO (IDA) ---
        for (int y = 0; y < largo; y++) {
            for (int x = 0; x < ancho; x++) {
                int[] rgb = img.getPixel(x, y);
                int index = (y * ancho + x) * 3;

                // Normalización a [0, 1]
                double r = rgb[0] / 255.0;
                double g = rgb[1] / 255.0;
                double b = rgb[2] / 255.0;

                // RGB -> XYZ
                double X = (0.5141 * r) + (0.3239 * g) + (0.1604 * b);
                double Y = (0.2651 * r) + (0.6702 * g) + (0.0641 * b);
                double Z = (0.0241 * r) + (0.1228 * g) + (0.8444 * b);

                // XYZ -> LMS
                double L = (0.3897 * X) + (0.6890 * Y) - (0.0787 * Z);
                double M = (-0.2298 * X) + (1.1834 * Y) + (0.0464 * Z);
                double S = (0.0000 * X) + (0.0000 * Y) + (1.0000 * Z);

                // Logaritmo (alineación perceptual)
                double epsilon = 1e-10;
                double L_log = Math.log10(L < epsilon ? epsilon : L);
                double M_log = Math.log10(M < epsilon ? epsilon : M);
                double S_log = Math.log10(S < epsilon ? epsilon : S);

                // LMS -> l, alpha, beta (Decorrelación)
                bufferLMS[index]     = (1.0 / Math.sqrt(3.0)) * L_log + (1.0 / Math.sqrt(3.0)) * M_log + (1.0 / Math.sqrt(3.0)) * S_log;
                bufferLMS[index + 1] = (1.0 / Math.sqrt(6.0)) * L_log + (1.0 / Math.sqrt(6.0)) * M_log - (2.0 / Math.sqrt(6.0)) * S_log;
                bufferLMS[index + 2] = (1.0 / Math.sqrt(2.0)) * L_log - (1.0 / Math.sqrt(2.0)) * M_log;
            }
            this.bufferLMSP3 = bufferLMS;
        }

        // --- BLOQUE DE VISUALIZACIÓN (FUERA DE LOS BUCLES DE CÁLCULO) ---
        byte[] nuevosPixeles = new byte[numPixeles * 3];
        for (int i = 0; i < numPixeles; i++) {
            int idx = i * 3;

            // Mapeo visual (Escalamos para que se vea algo en el byte[])
            int l_vis = (int) (bufferLMS[idx] * 100);
            int a_vis = (int) (bufferLMS[idx + 1] * 100) + 128;
            int b_vis = (int) (bufferLMS[idx + 2] * 100) + 128;

            nuevosPixeles[idx]     = (byte) Math.max(0, Math.min(255, l_vis));
            nuevosPixeles[idx + 1] = (byte) Math.max(0, Math.min(255, a_vis));
            nuevosPixeles[idx + 2] = (byte) Math.max(0, Math.min(255, b_vis));
        }

        // --- ACTUALIZACIÓN DE INTERFAZ (UNA SOLA VEZ AL FINAL) ---
        Imagen result = new Imagen(img.getNombreImagen() + "_LabView", 3, ancho, largo, nuevosPixeles);
        this.padre.setImagenResult(result);
    }
    public void transoformacion22(Imagen img) {
        if (this.graffic.isGrayScale(img)) {
            // Aquí irá el paso 2 después...
            byte[] pixeles = img.getPixelesImagen();
            int canales = img.getnoCanales();
            int[] dimensiones = img.getAnchoLargo();
            int totalPixeles = dimensiones[0] * dimensiones[1];

            // ELEMENTO 1: Histograma de la componente Y (YIQ) (Punto 3)
            int[] histY = new int[256];

            for (int i = 0; i < pixeles.length; i += canales) {
                int r = pixeles[i] & 0xFF;
                int g = (canales > 1) ? (pixeles[i+1] & 0xFF) : r;
                int b = (canales > 2) ? (pixeles[i+2] & 0xFF) : r;

                // Calculamos Y (Luminancia) - Aunque sea gris, aplicamos la fórmula
                int yValue = (int)(0.299 * r + 0.587 * g + 0.114 * b);

                // Clamping por si las dudas
                if(yValue > 255) yValue = 255;

                histY[yValue]++;
            }

            // ELEMENTO 2: Probabilidad de aparición (Punto 4)
            double[] probY = new double[256];
            for (int k = 0; k < 256; k++) {
                probY[k] = (double) histY[k] / totalPixeles;
            }

            // ELEMENTO 3: Densidad de Potencia (Punto 5)
            double[] densY = new double[256];
            for (int k = 0; k < 256; k++) {
                densY[k] = Math.pow(probY[k], 2);
            }

            // ELEMENTO 4: Generar cadena para reporte
            String cadenaGris = "--- REPORTE ESTADÍSTICO GRIS (YIQ - Y) ---\n";
            for (int k = 0; k < 256; k++) {
                if (histY[k] > 0) {
                    cadenaGris += "Nivel [ " + k + " ]:\n\t" +
                            "Intensidad (Y) -> Frec: " + histY[k] +
                            ", Prob: " + String.format("%.6f", probY[k]) +
                            ", Dens: " + String.format("%.8f", densY[k]) + "\n";
                }
            }

            // ELEMENTO 5: Ventana flotante con Scroll
            JTextArea textArea = new JTextArea(cadenaGris);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 600));

            JOptionPane.showMessageDialog(this, scrollPane, "Reporte Estadístico Gris (Paso 2)", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // --- PASO 1: IMÁGENES DE COLOR (RGB) ---
            byte[] pixeles = img.getPixelesImagen();
            int canales = img.getnoCanales();
            int[] dimensiones = img.getAnchoLargo();
            int totalPixeles = dimensiones[0] * dimensiones[1];

            // ELEMENTO 1: Histogramas (Punto 3 de la práctica)
            // Arreglos para contar frecuencias de cada canal
            int[] histR = new int[256];
            int[] histG = new int[256];
            int[] histB = new int[256];

            for (int i = 0; i < pixeles.length; i += canales) {
                histR[pixeles[i] & 0xFF]++;
                if (canales > 1) histG[pixeles[i + 1] & 0xFF]++;
                if (canales > 2) histB[pixeles[i + 2] & 0xFF]++;
            }

            // ELEMENTO 2: Cálculo de Probabilidades (Punto 4 de la práctica)
            // Formula: P(rk) = nk / N
            double[] probR = new double[256];
            double[] probG = new double[256];
            double[] probB = new double[256];

            for (int k = 0; k < 256; k++) {
                probR[k] = (double) histR[k] / totalPixeles;
                probG[k] = (double) histG[k] / totalPixeles;
                probB[k] = (double) histB[k] / totalPixeles;
            }

            // ELEMENTO 3: Cálculo de Densidad de Potencia (Punto 5 de la práctica)
            // Formula: PSD = P(rk)^2
            double[] densR = new double[256];
            double[] densG = new double[256];
            double[] densB = new double[256];

            for (int k = 0; k < 256; k++) {
                densR[k] = Math.pow(probR[k], 2);
                densG[k] = Math.pow(probG[k], 2);
                densB[k] = Math.pow(probB[k], 2);
            }

            // ELEMENTO 4: Mostrar datos en consola (Puntos 3, 4 y 5)
            System.out.println("--- REPORTE ESTADÍSTICO IMAGEN COLOR (Paso 1) ---");
            String cadenaF = "";
            for (int k = 0; k < 256; k++) {
                // Solo imprimimos si hay datos para no saturar la consola (ejemplo niveles con datos)

                if (histR[k] > 0 || histG[k] > 0 || histB[k] > 0) {
                    cadenaF+="Nivel [ "+k+" ]:\n\t" + "R -> Frec: " + histR[k] + ", Prob: "+ probR[k] + ", Dens: " + densR[k] + "\n\t" + "G -> Frec: " + histG[k] + ", Prob: "+ probG[k] + ", Dens: " + densG[k] + "\n\t" + "B -> Frec: " + histB[k] + ", Prob: "+ probB[k] + ", Dens: " + densB[k] + "\n\t" ;
                    //System.out.println("Nivel [" + k + "]:");
                    //System.out.printf("  R -> Frec: %d, Prob: %.6f, Dens: %.8f\n", histR[k], probR[k], densR[k]);
                    //System.out.printf("  G -> Frec: %d, Prob: %.6f, Dens: %.8f\n", histG[k], probG[k], densG[k]);
                    //System.out.printf("  B -> Frec: %d, Prob: %.6f, Dens: %.8f\n", histB[k], probB[k], densB[k]);
                }
            }
            //Para mostrar crear un JPanel nuevo que solo contenga el texto de cadenaF
            JTextArea textArea = new JTextArea(cadenaF);
            textArea.setEditable(false); // Que no se pueda editar
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Fuente tipo consola

            // JScrollPane para poder navegar si el texto es muy largo
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 600)); // Tamaño de la ventanita

            // Mostramos el mensaje con el scrollPane
            JOptionPane.showMessageDialog(this, scrollPane, "Reporte Estadístico RGB (Paso 1)", JOptionPane.INFORMATION_MESSAGE);
            //System.out.println(cadenaF);
            System.out.println("-------------------------------------------------");
        }
    }

    public void brilloMas(Imagen img, boolean bandera){
        byte[] rawData = img.getPixelesImagen();
        int canales = img.getnoCanales();
        int[] dimensiones = img.getAnchoLargo();
        int ancho = dimensiones[0];
        int largo = dimensiones[1];
        int sbidaBajada = 0;
        if(bandera){
            sbidaBajada = 1;
        }else{
            sbidaBajada = -1;
        }

        byte[] nuevosPixeles = new byte[rawData.length];

        for (int y = 0; y < largo; y++) {
            for (int x = 0; x < ancho; x++) {
                //Codigo para aumentar en 1 los poxeles de la imagen
                for (int c = 0; c < canales; c++) {
                    int indice = (y * ancho + x) * canales + c;

                    // 1. Convertir el byte (con signo) a un entero (0-255)
                    int valorOriginal = rawData[indice] & 0xFF;

                    // 2. Aumentar el brillo
                    int nuevoValor = valorOriginal + sbidaBajada;

                    // 3. Validar que no exceda el límite (clamping)
                    if (nuevoValor > 255) nuevoValor = 255;

                    // 4. Guardar de vuelta haciendo el cast a byte
                    nuevosPixeles[indice] = (byte) nuevoValor;
                }
            }
        }
        Imagen result = new Imagen(img.getNombreImagen(), canales, ancho, largo, nuevosPixeles); //Crear resultado
        this.padre.setImagenResult(result); //Mostrar resultado
    }
    public void escalaGrises(Imagen img) {
        byte[] rawData = img.getPixelesImagen();
        int canales = img.getnoCanales();
        int[] dimensiones = img.getAnchoLargo();
        int ancho = dimensiones[0];
        int largo = dimensiones[1];

        byte[] nuevosPixeles = new byte[rawData.length];

        for (int y = 0; y < largo; y++) {
            for (int x = 0; x < ancho; x++) {
                int index = (y * ancho + x) * canales;

                // 1. Usamos un INT para acumular la suma sin desbordamiento
                int sumaColor = 0;
                int numCanalesColor = Math.min(canales, 3); // Solo promediamos R, G y B

                for (int i = 0; i < numCanalesColor; i++) {
                    sumaColor += (rawData[index + i] & 0xFF);
                }

                byte valorGris = (byte) (sumaColor / numCanalesColor);

                // 2. Asignamos el gris a los canales de color
                for (int i = 0; i < numCanalesColor; i++) {
                    nuevosPixeles[index + i] = valorGris;
                }

                // 3. Si hay más canales (como el Alpha en el canal 4), los copiamos tal cual
                if (canales > 3) {
                    for (int i = 3; i < canales; i++) {
                        nuevosPixeles[index + i] = rawData[index + i];
                    }
                }
            }
        }

        Imagen result = new Imagen(img.getNombreImagen(), canales, ancho, largo, nuevosPixeles);
        this.padre.setImagenResult(result);

        // IMPORTANTE: Necesitas avisarle al Software que pinte el resultado
        //this.padre.mostrarImagenResult();
    }
}

