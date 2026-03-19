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

    //Para el ruido
    private JPanel panelSeccionRuido;
    private JButton[] botonesRuidoOriginales;
    public TransformationPanels(Software padre){
        this.padre = padre;
        this.graffic = new grafficWindow();
        this.bufferLMSP3 = null;
        //Ruido
        this.panelSeccionRuido = new JPanel(new GridLayout(0, 1, 5, 5));
        this.panelSeccionRuido.setBorder(BorderFactory.createTitledBorder("Laboratorio de Ruido"));

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
        JButton btnpuntoRGBaYIQ = new JButton("RGB a YIQ");
        JButton btnpuntoYIQaRGB = new JButton("YIQ a RGB");
        JButton btnpuntoRGBaHSV = new JButton("RGB a HSV");
        JButton btnpuntoYIQaHSV = new JButton("YIQ a HSV");
        JButton btnpuntoHSVaYIQ = new JButton("HSV a YIQ");
        JButton btnpuntoHSVaRGB = new JButton("HSV a RGB");
        JButton btnpuntoRGBaHSI = new JButton("RGB a HSI");
        JButton btnpuntoHSIaRGB = new JButton("HSI a RGB");
        JButton btnpuntoYIQaHSI = new JButton("YIQ a HSI");
        JButton btnpuntoHSIaYIQ = new JButton("HSI a YIQ");
        JButton btnpuntoHSVaHSI = new JButton("HSV a HSI");
        JButton btnpuntoHSIaHSV = new JButton("HSI a HSV");

        // Estos son los que el profe quiere para ver las diferencias reales
        JButton btnExtraerY = new JButton("Ver Canal Y (Luminancia)");
        JButton btnExtraerV = new JButton("Ver Canal V (Valor)");
        JButton btnExtraerI = new JButton("Ver Canal I (Intensidad)");

        //JBUtton practica 3:
        JButton btnlab = new JButton("Realizar transformacion lαβ");
        JButton btnlabGris = new JButton("Extraer los canales en niveles de gris");

        //Botones para el ruido:
        // Creamos los botones originales
        JButton btnSalPimienta = new JButton("Ruido sal y Pimienta");
        JButton btnGaussiano = new JButton("Ruido Gaussiano");
        JButton btnUniforme = new JButton("Ruido Uniforme");
        JButton btnRayleigh = new JButton("Ruido Rayleigh");
        JButton btnExponencial = new JButton("Ruido Exponencial");
        JButton btnErlang_Gamma = new JButton("Erlang/Gamma");
        this.botonesRuidoOriginales = new JButton[]{
                btnSalPimienta,
                btnGaussiano,
                btnUniforme,
                btnRayleigh,
                btnExponencial,
                btnErlang_Gamma
        };
        // Agregamos los botones al panel de la sección
        for (JButton btn : botonesRuidoOriginales) {
            this.panelSeccionRuido.add(btn);
        }

        // 3. Alineación al centro del panel
        btnGrises.setAlignmentX(Component.CENTER_ALIGNMENT);
        brilloContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContraste.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnpunto22.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnlab.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnlabGris.setAlignmentX(Component.CENTER_ALIGNMENT);
        // --- ALINEACIÓN AL CENTRO (Añadir después de declarar los botones) ---
//Ruido:
        btnSalPimienta.addActionListener(e -> this.changeImagePanels(33));
        btnGaussiano.addActionListener(e -> this.changeImagePanels(34));
        btnUniforme.addActionListener(e -> this.changeImagePanels(35));
        btnRayleigh.addActionListener(e -> this.changeImagePanels(36));
        btnExponencial.addActionListener(e -> this.changeImagePanels(37));
        btnErlang_Gamma.addActionListener(e -> this.changeImagePanels(38));
// Grupo YIQ
        btnpuntoRGBaYIQ.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnpuntoYIQaRGB.setAlignmentX(Component.CENTER_ALIGNMENT);

// Grupo HSV
        btnpuntoRGBaHSV.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnpuntoYIQaHSV.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnpuntoHSVaYIQ.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnpuntoHSVaRGB.setAlignmentX(Component.CENTER_ALIGNMENT);

// Grupo HSI
        btnpuntoRGBaHSI.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnpuntoHSIaRGB.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnpuntoYIQaHSI.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnpuntoHSIaYIQ.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnpuntoHSVaHSI.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnpuntoHSIaHSV.setAlignmentX(Component.CENTER_ALIGNMENT);

// Grupo Extracción/Control
        btnExtraerY.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExtraerV.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExtraerI.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        // --- 2. ACTION LISTENERS (LÓGICA DE CONTROL) ---

// --- 2. ACTION LISTENERS (CONECTADOS A CHANGEIMAGEPANELS) ---

// Bloque NTSC (YIQ)
        btnpuntoRGBaYIQ.addActionListener(e -> this.changeImagePanels(10));
        btnpuntoYIQaRGB.addActionListener(e -> this.changeImagePanels(11));

// Bloque Perceptual HSV
        btnpuntoRGBaHSV.addActionListener(e -> this.changeImagePanels(12));
        btnpuntoHSVaRGB.addActionListener(e -> this.changeImagePanels(13));

// Bloque Perceptual HSI
        btnpuntoRGBaHSI.addActionListener(e -> this.changeImagePanels(14));
        btnpuntoHSIaRGB.addActionListener(e -> this.changeImagePanels(15));

// "La Trampa" (Conversiones Cruzadas)
        btnpuntoYIQaHSV.addActionListener(e -> this.changeImagePanels(20));
        btnpuntoHSVaYIQ.addActionListener(e -> this.changeImagePanels(21));
        btnpuntoYIQaHSI.addActionListener(e -> this.changeImagePanels(22));
        btnpuntoHSIaYIQ.addActionListener(e -> this.changeImagePanels(23));
        btnpuntoHSVaHSI.addActionListener(e -> this.changeImagePanels(24));
        btnpuntoHSIaHSV.addActionListener(e -> this.changeImagePanels(25));

// Bloque de Extracción de Canales
        btnExtraerY.addActionListener(e -> this.changeImagePanels(30));
        btnExtraerV.addActionListener(e -> this.changeImagePanels(31));
        btnExtraerI.addActionListener(e -> this.changeImagePanels(32));        //5. Agregar botones
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

        this.add(new JLabel("--- MODELO YIQ (NTSC) ---"));
        this.add(btnpuntoRGBaYIQ);
        this.add(btnpuntoYIQaRGB);
        this.add(Box.createRigidArea(new Dimension(0, 10)));

        this.add(new JLabel("--- MODELO HSV ---"));
        this.add(btnpuntoRGBaHSV);
        this.add(btnpuntoHSVaRGB);
        this.add(Box.createRigidArea(new Dimension(0, 10)));

        this.add(new JLabel("--- MODELO HSI ---"));
        this.add(btnpuntoRGBaHSI);
        this.add(btnpuntoHSIaRGB);
        this.add(Box.createRigidArea(new Dimension(0, 10)));

        this.add(new JLabel("--- CONVERSIONES CRUZADAS ---"));
        this.add(btnpuntoYIQaHSV);
        this.add(btnpuntoHSVaYIQ);
        this.add(btnpuntoYIQaHSI);
        this.add(btnpuntoHSIaYIQ);
        this.add(btnpuntoHSVaHSI);
        this.add(btnpuntoHSIaHSV);
        this.add(Box.createRigidArea(new Dimension(0, 15)));

        this.add(new JLabel("--- VISUALIZACIÓN DE CANALES ---"));
        this.add(btnExtraerY);
        this.add(btnExtraerV);
        this.add(btnExtraerI);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(panelSeccionRuido);
    }

    public void changeImagePanels(int opc) {
        if (!this.padre.gethayImagen()) {
            JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
            return;
        }

        // Determinamos la fuente (Original o Result)
        Imagen imgAProcesar = this.padre.getPanel() ? this.padre.getImagenOriginal() : this.padre.getImagenResult();

        if (!this.padre.getPanel() && !this.padre.getHayResult()) {
            JOptionPane.showMessageDialog(this, "No hay ninguna imagen Result para hacer esta operacion");
            return;
        }

        switch (opc) {
            case 1: this.escalaGrises(imgAProcesar); break;
            case 2: this.brilloMas(imgAProcesar, true); break;
            case 3: this.brilloMas(imgAProcesar, false); break;
            case 4: this.transoformacion22(imgAProcesar); break;
            case 5: this.transformacioneslab(imgAProcesar); break;

            case 6: // EL CASO ESPECIAL (LAB)
                if (this.bufferLMSP3 != null) {
                    String[] canales = {"Canal l (Luminosidad)", "Canal α (Amarillo-Azul)", "Canal β (Rojo-Verde)"};
                    int seleccion = JOptionPane.showOptionDialog(null,
                            "Selecciona el canal Lab a visualizar:", "Punto 4: Lab",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, canales, canales[0]);

                    this.extraerCanalGrisLab(imgAProcesar, this.bufferLMSP3, (seleccion != -1 ? seleccion : 0));
                } else {
                    JOptionPane.showMessageDialog(this, "Buffer Lab vacío. Presiona 'Transformación lαβ' primero.");
                }
                break;

            // --- PRÁCTICA 2: MODELOS DE COLOR ---
            case 10: this.procesoRGBaYIQ(imgAProcesar); break;
            case 11: this.procesoYIQaRGB(imgAProcesar); break;
            case 12: this.procesoRGBaHSV(imgAProcesar); break;
            case 13: this.procesoHSVaRGB(imgAProcesar); break;
            case 14: this.procesoRGBaHSI(imgAProcesar); break;
            case 15: this.procesoHSIaRGB(imgAProcesar); break;
            //PROCESOS CRUZADOS (20-25)
            case 20: this.procesoYIQaHSV(imgAProcesar); break;
            // --- EXTRACCIONES DE CANALES P2 ---
            case 30: // Extracción YIQ
                gestionarExtraccion("YIQ", new String[]{"Y (Luminancia)", "I (Fase)", "Q (Cuadratura)"});
                break;
            case 31: // Extracción HSV
                gestionarExtraccion("HSV", new String[]{"H (Matiz)", "S (Saturación)", "V (Brillo)"});
                break;
            case 32: // Extracción HSI
                gestionarExtraccion("HSI", new String[]{"H (Matiz)", "S (Saturación)", "I (Intensidad)"});
                break;
            case 33:
                this.adicionarSP(imgAProcesar);
                break;
            case 34:
                this.adicionarGaussiano(imgAProcesar);
                break;
            case 35:
                this.adicionarUniforme(imgAProcesar);
                break;
            case 36:
                this.adicionarRayleigh(imgAProcesar);
                break;
            case 37:
                this.adicionarExponencial(imgAProcesar);
                break;
            case 38:
                this.acicionarErlang_Gamma(imgAProcesar);
                break;
            // ... dentro del switch ...
            case 50:
                this.aplicarFiltroMediana(imgAProcesar);
                this.regresarAModoRuido();
                break;
            case 51:
                this.aplicarFiltroMedia(imgAProcesar);
                this.regresarAModoRuido();
                break;
        }
    }

    private void adicionarSP(Imagen img) {
        // 8% de densidad: 0.04 sal, 0.04 pimienta
        aplicarRuidoYCambiarBotones(img, 33, "Sal y Pimienta");
    }

    private void adicionarGaussiano(Imagen img) {
        aplicarRuidoYCambiarBotones(img, 34, "Gaussiano");
    }

    private void adicionarUniforme(Imagen img) {
        aplicarRuidoYCambiarBotones(img, 35, "Uniforme");
    }

    private void adicionarRayleigh(Imagen img) {
        aplicarRuidoYCambiarBotones(img, 36, "Rayleigh");
    }

    private void adicionarExponencial(Imagen img) {
        aplicarRuidoYCambiarBotones(img, 37, "Exponencial");
    }

    private void acicionarErlang_Gamma(Imagen img) {
        aplicarRuidoYCambiarBotones(img, 38, "Erlang/Gamma");
    }
    private void regresarAModoRuido() {
        panelSeccionRuido.removeAll();
        for (JButton btn : botonesRuidoOriginales) {
            panelSeccionRuido.add(btn);
        }

        panelSeccionRuido.revalidate();
        panelSeccionRuido.repaint();
    }
    private void aplicarRuidoYCambiarBotones(Imagen img, int tipo, String nombreRuido) {
        int ancho = img.getAnchoLargo()[0];
        int largo = img.getAnchoLargo()[1];
        int canales = img.getnoCanales();
        byte[] pixeles = img.getPixelesImagen().clone();
        java.util.Random rnd = new java.util.Random();

        // Parámetros de dispersión (puedes ajustarlos para que se vea más o menos ruido)
        double a = 15.0;
        double b = 30.0;

        for (int i = 0; i < pixeles.length; i++) {
            double ruido = 0;
            double u = rnd.nextDouble();

            switch (tipo) {
                case 33: // Sal y Pimienta
                    if (u < 0.08) {
                        pixeles[i] = (rnd.nextBoolean()) ? (byte)255 : (byte)0;
                        continue;
                    }
                    break;
                case 34: // Gaussiano
                    ruido = rnd.nextGaussian() * b + a;
                    break;
                case 35: // Uniforme
                    ruido = a + (b - a) * u;
                    break;
                case 36: // Rayleigh
                    ruido = a + Math.sqrt(-2 * b * Math.log(1 - u));
                    break;
                case 37: // Exponencial
                    ruido = -(1.0 / a) * Math.log(1 - u);
                    break;
                case 38: // Erlang (Gamma) para k=2
                    ruido = -(1.0 / a) * (Math.log(1 - u) + Math.log(1 - rnd.nextDouble()));
                    break;
            }

            if (tipo != 33) {
                int val = (pixeles[i] & 0xFF) + (int)ruido;
                pixeles[i] = (byte) Math.max(0, Math.min(255, val));
            }
        }

        this.padre.setImagenResult(new Imagen(img.getNombreImagen() + "_Ruidosa", canales, ancho, largo, pixeles));

        // Cambiamos la interfaz al modo "Tratamiento"
        // Caso 50 para S&P (Mediana), Caso 51 para los demás (Media)
        int casoFiltro = (tipo == 33) ? 50 : 51;
        this.modoTratamiento(nombreRuido, casoFiltro);
    }
    private void modoTratamiento(String nombreRuido, int casoTratamiento) {
        // 1. Quita los 6 botones de ruido (SP, Gauss, etc.) del panel
        panelSeccionRuido.removeAll();

        // 2. Crea un botón "temporal" que solo sirve para limpiar
        JButton btnTratar = new JButton("Tratar Ruido: " + nombreRuido);
        btnTratar.setBackground(Color.GREEN); // Para que sepa que es la solución

        // 3. Le asignamos qué caso del switch debe ejecutar (50 o 51)
        btnTratar.addActionListener(e -> this.changeImagePanels(casoTratamiento));

        // 4. Lo ponemos en el panel y "refrescamos" la vista
        panelSeccionRuido.add(btnTratar);
        panelSeccionRuido.revalidate(); // Avisa que cambió la estructura
        panelSeccionRuido.repaint();    // Redibuja el componente
    }
    private void aplicarFiltroMediana(Imagen img) {
        int ancho = img.getAnchoLargo()[0];
        int largo = img.getAnchoLargo()[1];
        int canales = img.getnoCanales();
        byte[] original = img.getPixelesImagen();
        byte[] resultado = new byte[original.length];

        // Ventana 3x3
        for (int y = 1; y < largo - 1; y++) {
            for (int x = 1; x < ancho - 1; x++) {
                for (int c = 0; c < canales; c++) {
                    int[] ventana = new int[9];
                    int k = 0;
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            ventana[k++] = original[((y + dy) * ancho + (x + dx)) * canales + c] & 0xFF;
                        }
                    }
                    java.util.Arrays.sort(ventana);
                    resultado[(y * ancho + x) * canales + c] = (byte) ventana[4];
                }
            }
        }
        this.padre.setImagenResult(new Imagen("Limpia_Mediana", canales, ancho, largo, resultado));
    }

    private void aplicarFiltroMedia(Imagen img) {
        int ancho = img.getAnchoLargo()[0];
        int largo = img.getAnchoLargo()[1];
        int canales = img.getnoCanales();
        byte[] original = img.getPixelesImagen();
        byte[] resultado = new byte[original.length];

        for (int y = 1; y < largo - 1; y++) {
            for (int x = 1; x < ancho - 1; x++) {
                for (int c = 0; c < canales; c++) {
                    int suma = 0;
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            suma += original[((y + dy) * ancho + (x + dx)) * canales + c] & 0xFF;
                        }
                    }
                    resultado[(y * ancho + x) * canales + c] = (byte) (suma / 9);
                }
            }
        }
        this.padre.setImagenResult(new Imagen("Limpia_Media", canales, ancho, largo, resultado));
    }

    private void gestionarExtraccion(String modelo, String[] nombresCanales) {
        // 1. Validación de seguridad: ¿Hay datos de ese modelo en el buffer?
        if (this.padre.getBufferDecimal() == null || !this.padre.getFormatoBuffer().equals(modelo)) {
            JOptionPane.showMessageDialog(this, "El buffer no contiene datos de " + modelo + ". Realiza la conversión primero.");
            return;
        }

        // 2. Mostrar un diálogo para elegir el canal (0, 1 o 2)
        int seleccion = JOptionPane.showOptionDialog(
                this,
                "Selecciona el canal de " + modelo + " que deseas visualizar en niveles de gris:",
                "Visualización de Canales - " + modelo,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                nombresCanales,
                nombresCanales[0]
        );

        // 3. Si el usuario no cerró la ventana (seleccion != -1), extraemos
        if (seleccion != -1) {
            this.extraerCanalGenerico(seleccion, "Canal_" + nombresCanales[seleccion]);
        }
    }
    // --- "LA TRAMPA" (CONVERSIONES CRUZADAS) ---

    // Caso 20: YIQ -> HSV
    private void procesoYIQaHSV(Imagen img) {
        this.procesoYIQaRGB(img); // Paso 1: Volver a RGB (resultado queda en padre)
        if (this.padre.getHayResult()) {
            this.procesoRGBaHSV(this.padre.getImagenResult()); // Paso 2: De ese RGB a HSV
        }
    }

    // Caso 21: HSV -> YIQ
    private void procesoHSVaYIQ(Imagen img) {
        this.procesoHSVaRGB(img); // Paso 1: Volver a RGB
        if (this.padre.getHayResult()) {
            this.procesoRGBaYIQ(this.padre.getImagenResult()); // Paso 2: De ese RGB a YIQ
        }
    }

    // Caso 22: YIQ -> HSI
    private void procesoYIQaHSI(Imagen img) {
        this.procesoYIQaRGB(img);
        if (this.padre.getHayResult()) {
            this.procesoRGBaHSI(this.padre.getImagenResult());
        }
    }

    // Caso 23: HSI -> YIQ
    private void procesoHSIaYIQ(Imagen img) {
        this.procesoHSIaRGB(img);
        if (this.padre.getHayResult()) {
            this.procesoRGBaYIQ(this.padre.getImagenResult());
        }
    }

    // Caso 24: HSV -> HSI
    private void procesoHSVaHSI(Imagen img) {
        this.procesoHSVaRGB(img);
        if (this.padre.getHayResult()) {
            this.procesoRGBaHSI(this.padre.getImagenResult());
        }
    }

    // Caso 25: HSI -> HSV
    private void procesoHSIaHSV(Imagen img) {
        this.procesoHSIaRGB(img);
        if (this.padre.getHayResult()) {
            this.procesoRGBaHSV(this.padre.getImagenResult());
        }
    }
    private void procesoHSIaRGB(Imagen img) {
        if (this.padre.getBufferDecimal() == null || !this.padre.getFormatoBuffer().equals("HSI")) {
            JOptionPane.showMessageDialog(this, "Buffer vacío o no es HSI.");
            return;
        }

        int ancho = img.getAnchoLargo()[0];
        int largo = img.getAnchoLargo()[1];
        double[] buffer = this.padre.getBufferDecimal();
        byte[] pixelesRGB = new byte[ancho * largo * 3];

        for (int i = 0; i < ancho * largo; i++) {
            int idxD = i * 3;
            double h = Math.toRadians(buffer[idxD]);
            double s = buffer[idxD + 1];
            double intensity = buffer[idxD + 2];

            double r = 0, g = 0, b = 0;

            // Sector RG (0 <= H < 120°)
            if (h < 2 * Math.PI / 3) {
                b = intensity * (1 - s);
                r = intensity * (1 + (s * Math.cos(h)) / Math.cos(Math.PI / 3 - h));
                g = 3 * intensity - (r + b);
            }
            // Sector GB (120° <= H < 240°)
            else if (h < 4 * Math.PI / 3) {
                h = h - 2 * Math.PI / 3;
                r = intensity * (1 - s);
                g = intensity * (1 + (s * Math.cos(h)) / Math.cos(Math.PI / 3 - h));
                b = 3 * intensity - (r + g);
            }
            // Sector BR (240° <= H < 360°)
            else {
                h = h - 4 * Math.PI / 3;
                g = intensity * (1 - s);
                b = intensity * (1 + (s * Math.cos(h)) / Math.cos(Math.PI / 3 - h));
                r = 3 * intensity - (g + b);
            }

            pixelesRGB[idxD]     = (byte) Math.max(0, Math.min(255, r * 255));
            pixelesRGB[idxD + 1] = (byte) Math.max(0, Math.min(255, g * 255));
            pixelesRGB[idxD + 2] = (byte) Math.max(0, Math.min(255, b * 255));
        }

        this.padre.setImagenResult(new Imagen(img.getNombreImagen() + "_HSI_to_RGB", 3, ancho, largo, pixelesRGB));
    }

    private void procesoRGBaHSI(Imagen img) {
        int ancho = img.getAnchoLargo()[0];
        int largo = img.getAnchoLargo()[1];
        int numPixeles = ancho * largo;
        byte[] pixeles = img.getPixelesImagen();
        int canales = img.getnoCanales();

        double[] buffer = new double[numPixeles * 3];

        for (int i = 0; i < numPixeles; i++) {
            int idxB = i * canales;
            int idxD = i * 3;

            double r = (pixeles[idxB] & 0xFF) / 255.0;
            double g = (pixeles[idxB + 1] & 0xFF) / 255.0;
            double b = (pixeles[idxB + 2] & 0xFF) / 255.0;

            double num = 0.5 * ((r - g) + (r - b));
            double den = Math.sqrt(Math.pow(r - g, 2) + (r - b) * (g - b));

            // --- Cálculo de H (Hue) ---
            double h = 0;
            if (den != 0) {
                double theta = Math.acos(num / den);
                h = (b <= g) ? theta : (2 * Math.PI - theta);
            }
            h = Math.toDegrees(h); // Pasamos a grados [0, 360]

            // --- Cálculo de I (Intensity) ---
            double intensity = (r + g + b) / 3.0;

            // --- Cálculo de S (Saturation) ---
            double s = 0;
            if (intensity > 0) {
                s = 1 - (Math.min(r, Math.min(g, b)) / intensity);
            }

            buffer[idxD] = h;
            buffer[idxD + 1] = s;
            buffer[idxD + 2] = intensity;
        }

        this.padre.setBufferDecimal(buffer);
        this.padre.setFormatoBuffer("HSI");

        // Mostramos el canal I (Intensidad)
        this.extraerCanalGenerico(2, "Canal_I_Intensity");
    }

    private void procesoHSVaRGB(Imagen img) {
        if (this.padre.getBufferDecimal() == null || !this.padre.getFormatoBuffer().equals("HSV")) {
            JOptionPane.showMessageDialog(this, "No hay datos HSV en el buffer.");
            return;
        }

        int ancho = img.getAnchoLargo()[0];
        int largo = img.getAnchoLargo()[1];
        double[] buffer = this.padre.getBufferDecimal();
        byte[] pixelesRGB = new byte[ancho * largo * 3];

        for (int i = 0; i < ancho * largo; i++) {
            int idxD = i * 3;
            double h = buffer[idxD];
            double s = buffer[idxD + 1];
            double v = buffer[idxD + 2];

            double c = v * s;
            double x = c * (1 - Math.abs((h / 60.0) % 2 - 1));
            double m = v - c;

            double r = 0, g = 0, b = 0;

            if (h < 60)      { r = c; g = x; b = 0; }
            else if (h < 120) { r = x; g = c; b = 0; }
            else if (h < 180) { r = 0; g = c; b = x; }
            else if (h < 240) { r = 0; g = x; b = c; }
            else if (h < 300) { r = x; g = 0; b = c; }
            else             { r = c; g = 0; b = x; }

            pixelesRGB[idxD]     = (byte) ((r + m) * 255);
            pixelesRGB[idxD + 1] = (byte) ((g + m) * 255);
            pixelesRGB[idxD + 2] = (byte) ((b + m) * 255);
        }

        this.padre.setImagenResult(new Imagen(img.getNombreImagen() + "_HSV_to_RGB", 3, ancho, largo, pixelesRGB));
    }

    private void procesoRGBaHSV(Imagen img) {
        int ancho = img.getAnchoLargo()[0];
        int largo = img.getAnchoLargo()[1];
        int numPixeles = ancho * largo;
        byte[] pixeles = img.getPixelesImagen();
        int canales = img.getnoCanales();

        double[] buffer = new double[numPixeles * 3];

        for (int i = 0; i < numPixeles; i++) {
            int idxB = i * canales;
            int idxD = i * 3;

            // Normalizamos RGB a [0, 1] para facilitar el cálculo
            double r = (pixeles[idxB] & 0xFF) / 255.0;
            double g = (pixeles[idxB + 1] & 0xFF) / 255.0;
            double b = (pixeles[idxB + 2] & 0xFF) / 255.0;

            double max = Math.max(r, Math.max(g, b));
            double min = Math.min(r, Math.min(g, b));
            double delta = max - min;

            double h = 0, s, v;

            // --- Cálculo de H (Hue/Matiz) ---
            if (delta == 0) {
                h = 0;
            } else if (max == r) {
                h = 60 * (((g - b) / delta) % 6);
            } else if (max == g) {
                h = 60 * (((b - r) / delta) + 2);
            } else if (max == b) {
                h = 60 * (((r - g) / delta) + 4);
            }
            if (h < 0) h += 360;

            // --- Cálculo de S (Saturation) ---
            s = (max == 0) ? 0 : (delta / max);

            // --- Cálculo de V (Value/Brillo) ---
            v = max;

            // Guardamos (H en grados, S y V en [0, 1])
            buffer[idxD] = h;
            buffer[idxD + 1] = s;
            buffer[idxD + 2] = v;
        }

        this.padre.setBufferDecimal(buffer);
        this.padre.setFormatoBuffer("HSV");

        // Mostramos el canal V (Brillo) para ver algo en el panel de resultados
        // Como V está en [0, 1], multiplicamos por 255 en extraerCanalGenerico
        this.extraerCanalGenerico(2, "Canal_V_Value");
    }

    protected void procesoYIQaRGB(Imagen img) {
        if (this.padre.getBufferDecimal() == null || !this.padre.getFormatoBuffer().equals("YIQ")) {
            JOptionPane.showMessageDialog(this, "No hay datos YIQ válidos.");
            return;
        }

        int ancho = img.getAnchoLargo()[0];
        int largo = img.getAnchoLargo()[1];
        double[] buffer = this.padre.getBufferDecimal();
        byte[] pixelesRGB = new byte[ancho * largo * 3];

        for (int i = 0; i < ancho * largo; i++) {
            int idxD = i * 3;
            double y = buffer[idxD];
            double ii = buffer[idxD + 1];
            double q = buffer[idxD + 2];

            // Matriz YIQ -> RGB
            int r = (int) (1.0 * y + 0.956 * ii + 0.621 * q);
            int g = (int) (1.0 * y - 0.272 * ii - 0.647 * q);
            int b = (int) (1.0 * y - 1.106 * ii + 1.703 * q);

            pixelesRGB[idxD]     = (byte) Math.max(0, Math.min(255, r));
            pixelesRGB[idxD + 1] = (byte) Math.max(0, Math.min(255, g));
            pixelesRGB[idxD + 2] = (byte) Math.max(0, Math.min(255, b));
        }
        this.padre.setImagenResult(new Imagen(img.getNombreImagen() + "_RegresoRGB", 3, ancho, largo, pixelesRGB));
    }

    protected void procesoRGBaYIQ(Imagen img) {
        int ancho = img.getAnchoLargo()[0];
        int largo = img.getAnchoLargo()[1];
        int numPixeles = ancho * largo;
        byte[] pixeles = img.getPixelesImagen();
        int canales = img.getnoCanales();

        double[] buffer = new double[numPixeles * 3];

        for (int i = 0; i < numPixeles; i++) {
            int idxB = i * canales;
            int idxD = i * 3;

            double r = (pixeles[idxB] & 0xFF);
            double g = (pixeles[idxB + 1] & 0xFF);
            double b = (pixeles[idxB + 2] & 0xFF);

            // Matriz RGB -> YIQ
            buffer[idxD]     = 0.299 * r + 0.587 * g + 0.114 * b; // Y
            buffer[idxD + 1] = 0.596 * r - 0.274 * g - 0.322 * b; // I
            buffer[idxD + 2] = 0.211 * r - 0.523 * g + 0.312 * b; // Q
        }

        this.padre.setBufferDecimal(buffer);
        this.padre.setFormatoBuffer("YIQ");
        this.extraerCanalGenerico(0, "Canal_Y_Luminancia");
    }

    private void extraerCanalGenerico(int canal, String nombre) {
        if (this.padre.getBufferDecimal() == null) return;

        double[] buffer = this.padre.getBufferDecimal();
        String modelo = this.padre.getFormatoBuffer();
        int ancho = this.padre.getImagenOriginal().getAnchoLargo()[0];
        int largo = this.padre.getImagenOriginal().getAnchoLargo()[1];
        byte[] gris = new byte[ancho * largo * 3];

        for (int i = 0; i < ancho * largo; i++) {
            int idxD = i * 3;
            double val = buffer[idxD + canal];
            double visual = 0;

            if (modelo.equals("YIQ")) {
                visual = (canal == 0) ? val : val + 128;
            }
            else if (modelo.equals("HSV") || modelo.equals("HSI")) {
                if (canal == 0) visual = (val / 360.0) * 255; // Hue: de 360° a 255
                else visual = val * 255;                      // S, V, I: de [0,1] a 255
            }

            byte b = (byte) Math.max(0, Math.min(255, (int) visual));
            gris[idxD] = gris[idxD + 1] = gris[idxD + 2] = b;
        }
        this.padre.setImagenResult(new Imagen(nombre, 3, ancho, largo, gris));
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

