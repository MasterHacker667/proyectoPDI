package escom.project;

import com.formdev.flatlaf.FlatClientProperties;
import escom.project.Backend.*;
import escom.project.Backend.Imagen.Imagen;
import escom.project.Backend.Imagen.Tipo;

import javax.swing.*;
import java.awt.*;

public class CustomSidebar extends JPanel {

    private JPanel container;
    //Funcionamiento conjunto:
    private MainWin papa;
    private Mod1_Color diseccionadorDeColor;
    private Mod3_Punto diseccionaroDePunto;
    private Mod4_AlgebraGeometria diseccionadorAlgebraGeometria;
    private Mod5_Ruido diseccionadorRuido;
    private Mod6_ProcesamientoHistograma diseccionadorProcesamientoHistograma;
    private FiltrosPasaAltas filtrospasaAltas;
    private FiltrosPasaBajas filtrospasaBajas;
    private AnalisisEstadisticoP8 analisisEstadisticoP8;
    private FiltrosNoLineales_P9 p9;
    private MorfologiaMatematica_P10 p10;
    private TransformadaFourier_P12 p12;

    //Auxiliares:
    private int dx, dy;
    private double gradosRot;
    private double factorX;
    private double factorY;
    private double densidad;
    private int densidadGauss;
    public CustomSidebar(MainWin papa) {
        this.papa = papa;
        this.diseccionadorDeColor = new Mod1_Color();
        this.diseccionaroDePunto = new Mod3_Punto();
        this.diseccionadorAlgebraGeometria = new Mod4_AlgebraGeometria();
        this.diseccionadorRuido = new Mod5_Ruido();
        this.diseccionadorProcesamientoHistograma = new Mod6_ProcesamientoHistograma();
        this.filtrospasaBajas = new FiltrosPasaBajas();
        this.filtrospasaAltas = new FiltrosPasaAltas();
        this.analisisEstadisticoP8 = new AnalisisEstadisticoP8();
        this.p9 = new FiltrosNoLineales_P9();
        this.p10 = new MorfologiaMatematica_P10();
        this.p12 = new TransformadaFourier_P12();
        this.dx = 0;
        this.dy = 0;
        this.gradosRot = 0;
        this.factorX = 100;
        this.factorY = 100;
        this.densidad = 0;
        this.densidadGauss = 0;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(315, 0));
        setBackground(new Color(224, 224, 224)); // #E0E0E0

        // El contenedor real de los botones
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(224, 224, 224));
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ScrollPane para que sea igual al diseño HTML
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll suave

        add(scrollPane, BorderLayout.CENTER);

        // Agregamos los botones de tu lista
        //agregarBotonesIniciales();
        this.createSidebarButton("Duplicar Imagen", e-> {
            if(this.papa.isSelectorImagen()){
                this.papa.setImagenResult(this.papa.getImageOriginal());
            }else{
                this.papa.setImagenOriginal(this.papa.getImageResult());
            }
            //this.papa.setImagenResult(this.papa.getImageOriginal());

        });
        this.createSidebarButton("Separacion de canales R.G.B", e->{
            if(this.papa.hayAlgunaImagen){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                if(imagen1.getTipoActual() != Tipo.RGB && imagen1.getTipoActual() != Tipo.RGBA){
                    JOptionPane.showMessageDialog(this, "Tipo no aceptado");
                    return;
                }
                System.out.println(imagen1.getTipoActual());
                String[] opciones;
                if (imagen1.getNumCanales() > 3) {
                    opciones = new String[]{"Rojo", "Verde", "Azul", "Alpha"};
                } else {
                    opciones = new String[]{"Rojo", "Verde", "Azul"};
                }
                int seleccion = JOptionPane.showOptionDialog(
                        this,
                        "Selecciona el canal que deseas extraer:",
                        "Separar Canales",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                );
                Imagen resultado = null;
                switch (seleccion) {
                    case 0 -> resultado = this.diseccionadorDeColor.extraerCanalRojo(imagen1);
                    case 1 -> resultado = this.diseccionadorDeColor.extraerCanalVerde(imagen1);
                    case 2 -> resultado = this.diseccionadorDeColor.extraerCanalAzul(imagen1);
                    case 3 -> resultado = this.diseccionadorDeColor.extraerCanalAlpha(imagen1);
                }
                if(resultado != null){
                    this.papa.setImagenResult(resultado);
                }
            }else{
                JOptionPane.showMessageDialog(this, "No hay imagenes en el sistema");
            }
        });
        //Boton para grises tradicional
        this.createSidebarButton("Escala de Grises R.G.B.", e -> {
            if(this.papa.hayErrores()){

                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                if(imagen1.getTipoActual() != Tipo.RGB && imagen1.getTipoActual() != Tipo.RGBA){
                    JOptionPane.showMessageDialog(this, "Tipo no aceptado");
                    return;
                }
                //Operacion
                Imagen resultado = this.diseccionadorDeColor.convertirGrisMedia(imagen1);
                this.papa.setImagenResult(resultado);

            }
        });
        this.createSidebarButton("Escala de Grises Luminancia R.G.B.", e-> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                if(imagen1.getTipoActual() != Tipo.RGB && imagen1.getTipoActual() != Tipo.RGBA){
                    JOptionPane.showMessageDialog(this, "Tipo no aceptado");
                    System.out.println(imagen1.getTipoActual());
                    return;
                }
                Imagen resultado = this.diseccionadorDeColor.convertirGrisLuminancia(imagen1);
                this.papa.setImagenResult(resultado);
            }
        });
        //Conversiones
        this.createSidebarButton("convertir R.G.B. -> Y.I.Q.", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                if(imagen1.getTipoActual() == Tipo.YIQ){
                    JOptionPane.showMessageDialog(this, "La imagen que tratas de convertir ya es de tipo Y.I.Q.");
                    return;
                }
                if(imagen1.getTipoActual() != Tipo.RGB && imagen1.getTipoActual() != Tipo.RGBA){
                    JOptionPane.showMessageDialog(this, "Formato de entrada inaceptable");
                    return;
                }
                //Operacion
                Imagen resultado = this.diseccionadorDeColor.convertirRGBaYIQ(imagen1);
                this.papa.setImagenResult(resultado);
            }
        });

        this.createSidebarButton("convertir Y.I.Q. -> R.G.B.", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                if(imagen1.getTipoActual() == Tipo.RGBA || imagen1.getTipoActual() == Tipo.RGB){
                    JOptionPane.showMessageDialog(this, "La imagen que tratas de convertir ya es de tipo R.G.B.");
                    return;
                }else if (imagen1.getTipoActual() != Tipo.YIQ){
                    JOptionPane.showMessageDialog(this, "Imagen de entrada inaceptable");
                    return;
                }
                //Operacion
                Imagen resultado = this.diseccionadorDeColor.convertirYIQaRGB(imagen1);
                this.papa.setImagenResult(resultado);
            }
        });
        this.createSidebarButton("Convertir R.G.B -> H.S.V.", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                if(imagen1.getTipoActual() == Tipo.HSV){
                    JOptionPane.showMessageDialog(this, "La imagen que tratas de convertir ya es de tipo H.S.V.");
                    return;
                }
                if(imagen1.getTipoActual() != Tipo.RGB && imagen1.getTipoActual() != Tipo.RGBA){
                    JOptionPane.showMessageDialog(this, "Formato de entrada inaceptable");
                    return;
                }
                //Operacion
                Imagen resultado = this.diseccionadorDeColor.convertirRGBaHSV(imagen1);
                this.papa.setImagenResult(resultado);
            }
        });
        this.createSidebarButton("Convertir H.S.V. -> R.G.B.", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                if(imagen1.getTipoActual() == Tipo.RGBA || imagen1.getTipoActual() == Tipo.RGB){
                    JOptionPane.showMessageDialog(this, "La imagen que tratas de convertir ya es de tipo R.G.B.");
                    return;
                }else if (imagen1.getTipoActual() != Tipo.HSV){
                    JOptionPane.showMessageDialog(this, "Imagen de entrada inaceptable");
                    return;
                }
                //Operacion
                Imagen resultado = this.diseccionadorDeColor.convertirHSVaRGB(imagen1);
                this.papa.setImagenResult(resultado);
            }
        });
        this.createSidebarButton("Convertir R.G.B. -> L.A.B", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                if(imagen1.getTipoActual() == Tipo.LAB){
                    JOptionPane.showMessageDialog(this, "La imagen que tratas de convertir ya es de tipo L.A.B.");
                    return;
                }
                if(imagen1.getTipoActual() != Tipo.RGB && imagen1.getTipoActual() != Tipo.RGBA){
                    JOptionPane.showMessageDialog(this, "Formato de entrada inaceptable");
                    return;
                }
                //Operacion
                Imagen resultado = this.diseccionadorDeColor.convertirRGBaLab(imagen1);
                this.papa.setImagenResult(resultado);
            }
        });
        this.createSidebarButton("Convertir L.A.B. -> R.G.B.", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                if(imagen1.getTipoActual() == Tipo.RGBA || imagen1.getTipoActual() == Tipo.RGB){
                    JOptionPane.showMessageDialog(this, "La imagen que tratas de convertir ya es de tipo R.G.B.");
                    return;
                }else if (imagen1.getTipoActual() != Tipo.LAB){
                    JOptionPane.showMessageDialog(this, "Imagen de entrada inaceptable");
                    return;
                }
                //Operacion
                Imagen resultado = this.diseccionadorDeColor.convertirLabaRGB(imagen1);
                this.papa.setImagenResult(resultado);
            }
        });

        //Umbralees, Contraste y Brillo, Modulo 3:
        JLabel labelBrillo = new JLabel("Nivel de Brillo:");
        labelBrillo.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar respecto al panel
        labelBrillo.setFont(new Font("Arial", Font.BOLD, 12));
        this.container.add(labelBrillo);
        JSlider sliderBrillo = new JSlider(-255, 255, 0);
        sliderBrillo.setMajorTickSpacing(127);
        sliderBrillo.setPaintTicks(true);
        sliderBrillo.setPaintLabels(true); // Para que se vean los números
        sliderBrillo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sliderBrillo.addChangeListener(e -> {
            if (!sliderBrillo.getValueIsAdjusting()){
                int valor = sliderBrillo.getValue();
                if(this.papa.hayErrores()){
                    Imagen imagen1;
                    if(this.papa.isSelectorImagen()){
                        //Trabajamos con la imagen Original
                        imagen1 = this.papa.getImageOriginal();
                    }else{
                        //Trabajamos con la imagen Result
                        if(this.papa.getHayIMagenResult()) {
                            imagen1 = this.papa.getImageResult();
                        }else{
                            JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                            return;
                        }
                    }
                    //Operacion

                    this.papa.setImagenResult(this.diseccionaroDePunto.ajustarBrillo(imagen1, valor));
                }
            }
        });
        container.add(sliderBrillo);
        //COntraste
        JLabel labelCOntraste = new JLabel("Contraste:");
        labelCOntraste.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar respecto al panel
        labelCOntraste.setFont(new Font("Arial", Font.BOLD, 12));
        this.container.add(labelCOntraste);

        JSlider sliderContraste = new JSlider(0, 100, 20);
        sliderContraste.setMajorTickSpacing(127);
        sliderContraste.setPaintTicks(true);
        sliderContraste.setPaintLabels(true); // Para que se vean los números
        sliderContraste.setAlignmentX(Component.CENTER_ALIGNMENT);
        sliderContraste.addChangeListener(e -> {
            if (!sliderContraste.getValueIsAdjusting()){
                double valor = sliderContraste.getValue();
                if(this.papa.hayErrores()){
                    Imagen imagen1;
                    if(this.papa.isSelectorImagen()){
                        //Trabajamos con la imagen Original
                        imagen1 = this.papa.getImageOriginal();
                    }else{
                        //Trabajamos con la imagen Result
                        if(this.papa.getHayIMagenResult()) {
                            imagen1 = this.papa.getImageResult();
                        }else{
                            JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                            return;
                        }
                    }
                    //Operacion
                    this.papa.setImagenResult(this.diseccionaroDePunto.ajustarContraste(imagen1, valor));
                }
            }
        });
        this.container.add(sliderContraste);
        this.createSidebarButton("Generar negativos", e-> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                //Operacion
                this.papa.setImagenResult(this.diseccionaroDePunto.generarNegativo(imagen1));
            }
        });
        this.createSidebarButton("Binarizar", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                //Verificar que la imagen no sea ya Binaria:
                if(imagen1.getTipoActual() == Tipo.BINARIA){
                    JOptionPane.showMessageDialog(this, "Esta imagen ya esta binarizada");
                    return;
                }
                //Operacion
                //Aqui debemos preguntar al usuario cuantos umbrales poner xd
                // 1. Pedir los umbrales al usuario
                String input = JOptionPane.showInputDialog(this,
                        "Introduce los umbrales separados por comas (ej: 128 o 50,150,200):",
                        "Binarización Multiumbral",
                        JOptionPane.QUESTION_MESSAGE);

                if (input != null && !input.trim().isEmpty()) {
                    try {
                        // 2. Limpiar y separar el texto
                        // El regex [, ]+ separa por comas o espacios, ignorando repeticiones
                        String[] partes = input.trim().split("[,\\s]+");
                        int[] umbrales = new int[partes.length];

                        for (int i = 0; i < partes.length; i++) {
                            umbrales[i] = Integer.parseInt(partes[i]);
                            // Validación de rango
                            if (umbrales[i] < 0 || umbrales[i] > 255) {
                                throw new NumberFormatException("Fuera de rango");
                            }
                        }
                        //3. Ejecutar.
                        Imagen r = this.diseccionaroDePunto.binarizarUmbral(imagen1, umbrales);
                        this.papa.setImagenResult(r);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Error: Por favor introduce solo números entre 0 y 255 separados por comas.",
                                "Entrada Inválida",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        this.createSidebarButton("Invertir Binarizacion", e-> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                //Verificar que la imagen no sea ya Binaria:
                if(imagen1.getTipoActual() != Tipo.BINARIA){
                    JOptionPane.showMessageDialog(this, "Esta imagen No es Binaria");
                    return;
                }
                this.papa.setImagenResult(this.diseccionaroDePunto.invertirBinarizacion(imagen1));
            }
        });
        //MODULO 4

        this.container.add(new JSeparator(JSeparator.HORIZONTAL));

        JSlider sliderX = new JSlider(0, 100, 20);
        JSlider sliderY = new JSlider(0, 100, 20);
        sliderX.setAlignmentX(Component.CENTER_ALIGNMENT);
        sliderY.setAlignmentX(Component.CENTER_ALIGNMENT);
        sliderX.addChangeListener(e -> {
            this.dx = sliderX.getValue();
        });
        sliderY.addChangeListener(e -> {
            this.dy = sliderY.getValue();
        });
        JLabel lblX = new JLabel("Desplazamiento X: ");
        lblX.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.container.add(lblX);
        this.container.add(sliderX);
        JLabel lblY = new JLabel("Desplazamiento X: ");
        lblY.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.container.add(lblY);
        this.container.add(sliderY);
        this.createSidebarButton("Trasladar", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                //Proceso:
                this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.trasladar(imagen1, this.dx, this.dy));
            }
        });
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));

        //ROTACION
        JSlider gradosSlider = new JSlider(0, 360, 0);
        gradosSlider.addChangeListener(e -> {
            this.gradosRot = gradosSlider.getValue();
        });
        this.container.add(gradosSlider);
        this.createSidebarButton("Rotar", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                //Proceso:
                this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.rotar(imagen1, this.gradosRot));
            }
        });
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        // ------------------------------------------INTERPOLACION------------------------------------------
        JSlider sliderX1 = new JSlider(101, 500, 150);
        JSlider sliderY1 = new JSlider(101, 500, 150);
        sliderX1.addChangeListener(e -> {
            this.factorX = sliderX1.getValue() / 100;
        });
        sliderY1.addChangeListener(e -> {
            this.factorY = sliderY1.getValue() / 100;
        });
        JLabel labelX = new JLabel("Factor X: ");
        labelX.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel labelY = new JLabel("Factor Y: ");
        labelY.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.container.add(labelX);
        this.container.add(sliderX1);
        this.container.add(labelY);
        this.container.add(sliderY1);

        JRadioButton rbVecino = new JRadioButton("Vecino más cercano");
        JRadioButton rbBilineal = new JRadioButton("Bilineal");
        ButtonGroup grupoMetodos = new ButtonGroup();
        grupoMetodos.add(rbVecino);
        grupoMetodos.add(rbBilineal);
        rbVecino.setSelected(true);
        this.container.add(rbVecino);
        this.container.add(rbBilineal);

        this.createSidebarButton("Interpolacion", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                //Proceso:
                String metodoSeleccionado = rbVecino.isSelected() ? "Vecino" : "Bilineal";
                this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.interpolacion(imagen1, this.factorX, this.factorY, metodoSeleccionado));
            }
        });
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        //-------------------------------------------Operaciones con imagenes------------------------------------------
        this.createSidebarButton("Sumar imagenes", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operar(this.papa.getImageOriginal(), this.papa.getImageResult(), "sumar"));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        this.createSidebarButton("Restar imagenes", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operar(this.papa.getImageOriginal(), this.papa.getImageResult(), "restar"));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        this.createSidebarButton("Multiplicar imagenes", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operar(this.papa.getImageOriginal(), this.papa.getImageResult(), "multiplicar"));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        this.createSidebarButton("Dividir imagenes", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operar(this.papa.getImageOriginal(), this.papa.getImageResult(), "dividir"));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        //Operaciones logicas:
        this.createSidebarButton("operacoin AND", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operacionLogica(this.papa.getImageOriginal(), this.papa.getImageResult(), "and"));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        this.createSidebarButton("operacoin OR", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operacionLogica(this.papa.getImageOriginal(), this.papa.getImageResult(), "or"));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        this.createSidebarButton("operacoin XOR", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operacionLogica(this.papa.getImageOriginal(), this.papa.getImageResult(), "xor"));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        this.createSidebarButton("operacoin NOT", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operacionLogica(this.papa.getImageOriginal(), this.papa.getImageResult(), "not"));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        //OPERACIONES RELACIONALES
        this.createSidebarButton("Operacion Relacional >", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operacionRelacional(this.papa.getImageOriginal(), this.papa.getImageResult(), ">"));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        this.createSidebarButton("Operacion Relacional <", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operacionRelacional(this.papa.getImageOriginal(), this.papa.getImageResult(), "<"));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        this.createSidebarButton("Operacion Relacional ==", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operacionRelacional(this.papa.getImageOriginal(), this.papa.getImageResult(), "=="));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        this.createSidebarButton("Operacion Relacional !=", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operacionRelacional(this.papa.getImageOriginal(), this.papa.getImageResult(), "!="));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        this.createSidebarButton("Operacion Relacional >=", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operacionRelacional(this.papa.getImageOriginal(), this.papa.getImageResult(), ">="));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        this.createSidebarButton("Operacion Relacional <=", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorAlgebraGeometria.operacionRelacional(this.papa.getImageOriginal(), this.papa.getImageResult(), "<="));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        //--------------------------RUIDO----------------------
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        JSlider sliderRuido = new JSlider(0, 100, 0);
        this.densidad = 0;
        sliderRuido.addChangeListener(e -> {
            this.densidad = sliderRuido.getValue() / 100.0;
            this.densidadGauss = sliderRuido.getValue();
        });
        this.container.add(sliderRuido);
        this.createSidebarButton("Agregar Ruido Sal y Pimienta", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                this.papa.setImagenResult(this.diseccionadorRuido.agregarRuidoSalYPimienta(imagen1, this.densidad));
            }

        });
        this.createSidebarButton("Agregar Ruido Sal Binario", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                this.papa.setImagenResult(this.diseccionadorRuido.agregarRuidoSalBinario(imagen1, this.densidad));
            }

        });
        this.createSidebarButton("Agregar Ruido Pimienta Binario", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                this.papa.setImagenResult(this.diseccionadorRuido.agregarRuidoPimientaBinario(imagen1, this.densidad));
            }

        });
        this.createSidebarButton("Agregar Ruido Sal Grises", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                this.papa.setImagenResult(this.diseccionadorRuido.agregarRuidoSalGris(imagen1, this.densidad));
            }

        });
        this.createSidebarButton("Agregar Ruido Pimienta Grises", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                this.papa.setImagenResult(this.diseccionadorRuido.agregarRuidoPimientaGris(imagen1, this.densidad));
            }

        });
        this.createSidebarButton("Agregar Ruido Gaussiano", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                this.papa.setImagenResult(this.diseccionadorRuido.agregarRuidoGaussiano(imagen1, 0, this.densidadGauss));

            }

        });
        this.createSidebarButton("Agregar Ruido Uniforme", e -> {
            if (this.papa.hayErrores()) {
                Imagen imagen1;
                if (this.papa.isSelectorImagen()) {
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if (this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }

                // --- MAPEO DE ESCALA PARA INTENSIDAD DEL RUIDO ---
                // Como 'this.densidad' va de 0.0 a 1.0, lo multiplicamos por 80.0 para que
                // el rango de variación máximo sea de [-80, 80] niveles de intensidad en los canales RGB.
                double amplitud = this.densidad * 80.0;
                double limiteA = -amplitud;
                double limiteB = amplitud;

                // 1. Generamos la imagen con el ruido uniforme escalado correctamente
                Imagen imagenRuidosa = this.diseccionadorRuido.agregarRuidoUniforme(imagen1, limiteA, limiteB);

                // 2. Almacenamos el resultado en la lógica principal para renderizarlo
                this.papa.setImagenResult(imagenRuidosa);

                // 3. ¡DESPLIEGUE DEL RECUADRO FLOTANTE DE MÉTRICAS!
                // Al ser un ruido continuo que afecta toda la matriz, verás que arroja el 100% de píxeles alterados,
                // cumpliendo perfectamente con la justificación matemática que requiere el reporte.
            }
        });
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        JSlider sliderDesplazamientoA = new JSlider(0, 254, 0);
        sliderRuido.addChangeListener(e -> {

        });
        this.container.add(sliderDesplazamientoA);
        this.createSidebarButton("Desplazar Histograma", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                this.papa.setImagenResult(this.diseccionadorProcesamientoHistograma.desplazarHistograma(imagen1, sliderDesplazamientoA.getValue()));
            }
        });
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        JLabel labelMin = new JLabel("Min:");
        this.container.add(labelMin);
        JSlider sliderMin = new JSlider(0, 254, 0);
        this.container.add(sliderMin);
        JLabel labelMax = new JLabel("Max");
        this.container.add(labelMax);
        JSlider sliderMax = new JSlider(0, 254, 0);
        this.container.add(sliderMax);
        this.createSidebarButton("Expandir Contraste", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                this.papa.setImagenResult(this.diseccionadorProcesamientoHistograma.expandirContraste(imagen1, sliderMin.getValue(), sliderMax.getValue()));
            }
        });
        this.createSidebarButton("Contraer Contraste", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                this.papa.setImagenResult(this.diseccionadorProcesamientoHistograma.contraerContraste(imagen1, sliderMin.getValue(), sliderMax.getValue()));
            }
        });
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        this.createSidebarButton("Ecualizar", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                this.papa.setImagenResult(this.diseccionadorProcesamientoHistograma.ecualizarImagen(imagen1));
            }
        });
        this.createSidebarButton("Correspondencia de Histogramas", e -> {
            if(this.papa.hayErrores()){
                if(this.papa.getHayIMagenResult() && this.papa.getHayImagenOriginal()) {
                    //Ejecutar la accion
                    this.papa.setImagenResult(this.diseccionadorProcesamientoHistograma.correspondenciaHistograma(this.papa.getImageOriginal(), this.papa.getImageResult()));
                }else if(this.papa.getHayImagenOriginal() && !(this.papa.getHayIMagenResult())){
                    JOptionPane.showMessageDialog(this, "No hay Imagen Result para Trabajar");
                } else if (!(this.papa.getHayImagenOriginal()) && this.papa.getHayIMagenResult()) {
                    JOptionPane.showMessageDialog(this, "No hay Imagen Original para Trabajar");
                }else{
                    JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
                }
            }
        });
        //PARA MAS CODIGO::
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        //FILTROS PASA ALTAS
        //Pedir datos:
        // Arreglo con los tamaños de kernel vistos en clase
        Integer[] tamañosImpares = {3, 5, 7, 9, 11};

        // Creación del JComboBox
        JComboBox<Integer> comboKernel = new JComboBox<>(tamañosImpares);
        container.add(comboKernel);
        this.createSidebarButton("Filtro Promediador", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }

                // Al presionar el botón de "Filtro Promediador", obtienes el valor así:
                int tamanoSeleccionado = (int) comboKernel.getSelectedItem();
                //Definir tamano
                this.papa.setImagenResult(this.filtrospasaBajas.filtroPromediador(imagen1, this.papa.darMatrizDeseada(tamanoSeleccionado), tamanoSeleccionado));
            }
        });

        //Boton 2:
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));

        JSlider sliderSigma = new JSlider(JSlider.HORIZONTAL, 5, 50, 10);
        sliderSigma.setMajorTickSpacing(10); // Marcas cada 1.0
        sliderSigma.setMinorTickSpacing(1);  // Marcas cada 0.1
        sliderSigma.setPaintTicks(true);
        sliderSigma.setPaintLabels(true);
        container.add(sliderSigma);
        // Creación del JComboBox
        JComboBox<Integer> comboKernel1 = new JComboBox<>(tamañosImpares);
        container.add(comboKernel1);
        this.createSidebarButton("Filtro Gaussiano", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }

                int tamanoSeleccionado = (int) comboKernel1.getSelectedItem();
                int indice = (tamanoSeleccionado - 3) / 2;

                // CORRECCIÓN: Obtener el sigma real del slider (0.5 a 5.0)
                double sigmaReal = sliderSigma.getValue() / 10.0;

                // Enviamos la matriz que está en memoria (misMatrices)
                this.papa.setImagenResult(this.filtrospasaBajas.filtroGaussiano(
                        imagen1,
                        this.papa.darMatrizDeseada(tamanoSeleccionado),
                        //this.papa.getMisMatrices()[indice],
                        tamanoSeleccionado,
                        sigmaReal
                ));
            }
        });
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        container.add(new JLabel("Tamaño del Kernel:"));
        JComboBox<Integer> comboKernel2 = new JComboBox<>(tamañosImpares);
        container.add(comboKernel2);
        //double A:
        container.add(new JLabel("Factor A (Enfatizado / High-Boost):"));
        JSlider sliderA = new JSlider(JSlider.HORIZONTAL, 10, 50, 10);

// Configuración estética
        sliderA.setMajorTickSpacing(10); // Marcas cada 1.0 (10 unidades)
        sliderA.setMinorTickSpacing(5);  // Marcas cada 0.5 (5 unidades)
        sliderA.setPaintTicks(true);
        sliderA.setPaintLabels(true);

// Opcional: Personalizar etiquetas para que se vea 1.0, 2.0... en lugar de 10, 20
        java.util.Hashtable<Integer, JLabel> labelTable = new java.util.Hashtable<>();
        labelTable.put(10, new JLabel("1.0"));
        labelTable.put(20, new JLabel("2.0"));
        labelTable.put(30, new JLabel("3.0"));
        labelTable.put(40, new JLabel("4.0"));
        labelTable.put(50, new JLabel("5.0"));
        sliderA.setLabelTable(labelTable);
        container.add(sliderA);

        // Creación del JComboBox

        this.createSidebarButton("Filtro Enfatizado", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1 = this.papa.isSelectorImagen() ?
                        this.papa.getImageOriginal() : this.papa.getImageResult();

                if (imagen1 == null) {
                    JOptionPane.showMessageDialog(this, "No hay imagen disponible");
                    return;
                }

                int tamanoSeleccionado = (int) comboKernel2.getSelectedItem();
                int indice = (tamanoSeleccionado - 3) / 2;
                double valorA = sliderA.getValue() / 10.0;

                // Enviamos la matriz manual de 'papa'
                this.papa.setImagenResult(this.filtrospasaBajas.filtroEnfatizado(
                        imagen1,
                        this.papa.darMatrizDeseada(tamanoSeleccionado),
                        tamanoSeleccionado,
                        valorA
                ));
            }
        });

        //FILTRO PASA ALTAS:::
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        this.createSidebarButton("Operador Roberts", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                // Al presionar el botón de "Filtro Promediador", obtienes el valor así:
                int tamanoSeleccionado = (int) comboKernel2.getSelectedItem();
                this.papa.setImagenResult(this.filtrospasaAltas.operadorRoberts(imagen1));
            }
        });


        //this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        this.createSidebarButton("Operador Prewitt", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                // Al presionar el botón de "Filtro Promediador", obtienes el valor así:
                int tamanoSeleccionado = (int) comboKernel2.getSelectedItem();
                this.papa.setImagenResult(this.filtrospasaAltas.operadorPrewitt(imagen1));
            }
        });
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        container.add(new JLabel("Tamaño del Kernel:"));
        JComboBox<Integer> comboKernel3 = new JComboBox<>(tamañosImpares);
        container.add(comboKernel3);
        this.createSidebarButton("Operador Sobel", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                //Acciones
                int tamano = (int) comboKernel3.getSelectedIndex();
                this.papa.setImagenResult(this.filtrospasaAltas.operadorSobel(imagen1, this.papa.darMatrizDeseada(tamano), this.papa.darRespaldoDeseado(tamano, 0)));
            }
        });
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        container.add(new JLabel("Tamaño del Kernel:"));
        JComboBox<Integer> comboKernel4 = new JComboBox<>(tamañosImpares);
        container.add(comboKernel4);
        this.createSidebarButton("Operador Frei-Chen", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                //Acciones
                int tamano = (int) comboKernel4.getSelectedIndex();
                this.papa.setImagenResult(this.filtrospasaAltas.operadorFreiChen(imagen1, this.papa.darMatrizDeseada(tamano), this.papa.darRespaldoDeseado(tamano, 0)));
            }
        });
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        container.add(new JLabel("Tamaño del Kernel:"));
        JComboBox<Integer> comboKernel5 = new JComboBox<>(tamañosImpares);
        container.add(comboKernel5);
        this.createSidebarButton("Operador Kirsch", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                //Acciones
                int tamano = (int) comboKernel5.getSelectedIndex();
                //Seleccionar Kernel:
                String[] opciones = {
                        "NORTE (Kernel Principal)",
                        "NORESTE (Kernel Repuesto 0)",
                        "ESTE (Kernel Repuesto 1)",
                        "SURESTE (Kernel Repuesto 2)",
                        "SUR (Kernel Repuesto 3)",
                        "SUROESTE (Kernel Repuesto 4)",
                        "OESTE (Kernel Repuesto 5)",
                        "NOROESTE (Kernel Repuesto 6)"
                };
                String seleccion = (String) JOptionPane.showInputDialog(
                        this,
                        "Seleccione la dirección del borde a detectar:",
                        "Direcciones de Compás",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0] // Opción por defecto
                );
                if (seleccion != null){
                    double[][] kernelElegido;
                    int indice = -1;
                    for (int i = 0; i < opciones.length; i++) {
                        if (opciones[i].equals(seleccion)) {
                            indice = i;
                            break;
                        }
                    }

                    if (indice == 0) {
                        kernelElegido = papa.darMatrizDeseada(tamano);
                    } else {
                        // Los repuestos van de 0 a 6, por eso restamos 1 al índice
                        kernelElegido = papa.darRespaldoDeseado(tamano, indice - 1);
                    }
                    this.papa.setImagenResult(this.filtrospasaAltas.operadorKirsch(imagen1, kernelElegido));
                }
                //this.papa.setImagenResult(this.filtrospasaAltas.operadorKirsch(imagen1, this.papa.darMatrizDeseada(tamano)));
            }
        });
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        container.add(new JLabel("Tamaño del Kernel:"));
        JComboBox<Integer> comboKernel6 = new JComboBox<>(tamañosImpares);
        container.add(comboKernel6);
        this.createSidebarButton("Operador Robinson", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                //Acciones
                int tamano = (int) comboKernel6.getSelectedIndex();
                //Seleccionar Kernel:
                String[] opciones = {
                        "NORTE (Kernel Principal)",
                        "NORESTE (Kernel Repuesto 0)",
                        "ESTE (Kernel Repuesto 1)",
                        "SURESTE (Kernel Repuesto 2)",
                        "SUR (Kernel Repuesto 3)",
                        "SUROESTE (Kernel Repuesto 4)",
                        "OESTE (Kernel Repuesto 5)",
                        "NOROESTE (Kernel Repuesto 6)"
                };
                String seleccion = (String) JOptionPane.showInputDialog(
                        this,
                        "Seleccione la dirección del borde a detectar:",
                        "Direcciones de Compás",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0] // Opción por defecto
                );
                if (seleccion != null){
                    double[][] kernelElegido;
                    int indice = -1;
                    for (int i = 0; i < opciones.length; i++) {
                        if (opciones[i].equals(seleccion)) {
                            indice = i;
                            break;
                        }
                    }

                    if (indice == 0) {
                        kernelElegido = papa.darMatrizDeseada(tamano);
                    } else {
                        // Los repuestos van de 0 a 6, por eso restamos 1 al índice
                        kernelElegido = papa.darRespaldoDeseado(tamano, indice - 1);
                    }
                    this.papa.setImagenResult(this.filtrospasaAltas.operadorRobinson(imagen1, kernelElegido));
                }
                //this.papa.setImagenResult(this.filtrospasaAltas.operadorKirsch(imagen1, this.papa.darMatrizDeseada(tamano)));
            }
        });

        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        container.add(new JLabel("Tamaño del Kernel:"));
        JComboBox<Integer> comboKernel7 = new JComboBox<>(tamañosImpares);
        container.add(comboKernel7);
        this.createSidebarButton("Operador Lapiciano", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    //Trabajamos con la imagen Original
                    imagen1 = this.papa.getImageOriginal();
                }else{
                    //Trabajamos con la imagen Result
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    }else{
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                //Acciones
                int tamano = (int) comboKernel7.getSelectedIndex();
                this.papa.setImagenResult(this.filtrospasaAltas.operadorLaplaciano(imagen1, this.papa.darMatrizDeseada(tamano)));
            }
        });
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        container.add(new JLabel("Tamaño del Kernel:"));
        JComboBox<Integer> comboKernel8 = new JComboBox<>(tamañosImpares);
        container.add(comboKernel8);
        this.createSidebarButton("Operador Laplaciano Gauss", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }

                // --- CORRECCIÓN AQUÍ ---
                // 1. Obtener el valor real (3, 5, 7...) no el índice (0, 1, 2...)
                int tamanoReal = (int) comboKernel8.getSelectedItem();

                // 2. Pedir un Sigma al usuario (Opcional, pero recomendado para que experimente)
                // Si no quieres pedirlo, puedes usar el sugerido directamente.
                double sigmaSugerido = this.filtrospasaAltas.sugerirSigma(tamanoReal);

                String inputSigma = JOptionPane.showInputDialog(this,
                        "Introduce el valor de Sigma (Sugerido para " + tamanoReal + "x" + tamanoReal + "):",
                        sigmaSugerido);

                double sigmaFinal;
                try {
                    sigmaFinal = (inputSigma == null) ? sigmaSugerido : Double.parseDouble(inputSigma);
                } catch (NumberFormatException ex) {
                    sigmaFinal = sigmaSugerido;
                }

                // 3. Ejecutar el filtro
                // Nota: Le pasamos la matriz de 'papa' para que el método la llene y se vea reflejada
                // en la Ventana de Matrices si el usuario la abre.
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);

                Imagen resultado = this.filtrospasaAltas.laplacianoDeGauss(
                        imagen1,
                        matrizParaLlenar,
                        sigmaFinal,
                        tamanoReal
                );

                this.papa.setImagenResult(resultado);
            }
        });
        //--------------PRACTICA 9----------------
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        container.add(new JLabel("Tamaño del Kernel:"));
        JComboBox<Integer> comboKernel9 = new JComboBox<>(tamañosImpares);
        container.add(comboKernel9);
        this.createSidebarButton("FIltro Media aritmetica", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel9.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);
                Imagen imagenr = this.p9.filtroMediaAritmetica(imagen1, matrizParaLlenar);
                this.papa.setImagenResult(imagenr);
            }
        });
        //2.

        this.createSidebarButton("FIltro Mediana", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel9.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);
                Imagen imagenr = this.p9.filtroMediana(imagen1, matrizParaLlenar);
                this.papa.setImagenResult(imagenr);
            }
        });

        this.createSidebarButton("FIltro Maximo", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel9.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);
                Imagen imagenr = this.p9.filtroMaximo(imagen1, matrizParaLlenar);
                this.papa.setImagenResult(imagenr);
            }
        });
        this.createSidebarButton("FIltro Minimo", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel9.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);
                Imagen imagenr = this.p9.filtroMinimo(imagen1, matrizParaLlenar);
                this.papa.setImagenResult(imagenr);
            }
        });

        this.createSidebarButton("FIltro Punto Medio", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel9.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);
                Imagen imagenr = this.p9.filtroPuntoMedio(imagen1, matrizParaLlenar);
                this.papa.setImagenResult(imagenr);
            }
        });

        this.createSidebarButton("FIltro Maximo Minimo", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel9.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);
                Imagen imagenr = this.p9.filtroMaximoMinimo(imagen1, matrizParaLlenar);
                this.papa.setImagenResult(imagenr);
            }
        });
        this.createSidebarButton("FIltro inferior Geometrico", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel9.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);
                Imagen imagenr = this.p9.filtroInferiorGeometrico(imagen1, matrizParaLlenar);
                this.papa.setImagenResult(imagenr);
            }
        });
        this.createSidebarButton("FIltro inferior Armonico", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel9.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);
                Imagen imagenr = this.p9.filtroInferiorArmonico(imagen1, matrizParaLlenar);
                this.papa.setImagenResult(imagenr);
            }
        });
        JLabel lblP = new JLabel("Píxeles a recortar (P):");
        JSpinner spinnerP = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
        this.container.add(spinnerP);
        this.createSidebarButton("FIltro alfa trimmed", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel9.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);

                int N = 0;
                for (int i = 0; i < matrizParaLlenar.length; i++) {
                    for (int j = 0; j < matrizParaLlenar[i].length; j++) {
                        if (matrizParaLlenar[i][j] > 0.5) N++;
                    }
                }
                int valorP = (int) spinnerP.getValue();
                if (2 * valorP >= N) {
                    int pMaximoSeguro = (N - 1) / 2;
                    JOptionPane.showMessageDialog(this,
                            "El valor de P (" + valorP + ") es demasiado grande para esta máscara.\n" +
                                    "Para una máscara con " + N + " píxeles activos, el máximo P permitido es " + pMaximoSeguro + ".\n" +
                                    "Se ajustará automáticamente.",
                            "Error de Parámetro", JOptionPane.WARNING_MESSAGE);

                    // Corregimos el valor en la interfaz y en nuestra variable
                    spinnerP.setValue(pMaximoSeguro);
                    valorP = pMaximoSeguro;
                }

                Imagen imagenr = this.p9.filtroAlfaTrimmed(imagen1, matrizParaLlenar, valorP);
                this.papa.setImagenResult(imagenr);
            }
        });
        this.createSidebarButton("FIltro inferior contra armónico", e -> {
            if(this.papa.hayErrores()){
                Imagen imagen1;
                if(this.papa.isSelectorImagen()){
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if(this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel9.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);

                int N = 0;
                for (int i = 0; i < matrizParaLlenar.length; i++) {
                    for (int j = 0; j < matrizParaLlenar[i].length; j++) {
                        if (matrizParaLlenar[i][j] > 0.5) N++;
                    }
                }
                int valorP = (int) spinnerP.getValue();
                if (2 * valorP >= N) {
                    int pMaximoSeguro = (N - 1) / 2;
                    JOptionPane.showMessageDialog(this,
                            "El valor de P (" + valorP + ") es demasiado grande para esta máscara.\n" +
                                    "Para una máscara con " + N + " píxeles activos, el máximo P permitido es " + pMaximoSeguro + ".\n" +
                                    "Se ajustará automáticamente.",
                            "Error de Parámetro", JOptionPane.WARNING_MESSAGE);

                    // Corregimos el valor en la interfaz y en nuestra variable
                    spinnerP.setValue(pMaximoSeguro);
                    valorP = pMaximoSeguro;
                }

                Imagen imagenr = this.p9.filtroContraArmonico(imagen1, matrizParaLlenar, valorP);
                this.papa.setImagenResult(imagenr);
            }
        });
        //========================= 0PRACTICA 10 =========================
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        container.add(new JLabel("Tamaño del Kernel:"));
        JComboBox<Integer> comboKernel10 = new JComboBox<>(tamañosImpares);
        container.add(comboKernel10);
        this.createSidebarButton("Erosionar", e-> {
            if(this.papa.hayErrores()) {
                Imagen imagen1;
                if (this.papa.isSelectorImagen()) {
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if (this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel10.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);

                this.papa.setImagenResult(this.p10.erosionar(imagen1,  matrizParaLlenar));
            }
        });

        this.createSidebarButton("Dilatar", e-> {
            if(this.papa.hayErrores()) {
                Imagen imagen1;
                if (this.papa.isSelectorImagen()) {
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if (this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel10.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);

                this.papa.setImagenResult(this.p10.dilatar(imagen1,  matrizParaLlenar));
            }
        });
        this.createSidebarButton("Clausura", e-> {
            if(this.papa.hayErrores()) {
                Imagen imagen1;
                if (this.papa.isSelectorImagen()) {
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if (this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel10.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);

                this.papa.setImagenResult(this.p10.clausura(imagen1,  matrizParaLlenar));
            }

        });
        this.createSidebarButton("Apertura", e-> {
            if(this.papa.hayErrores()) {
                Imagen imagen1;
                if (this.papa.isSelectorImagen()) {
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if (this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel10.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);

                this.papa.setImagenResult(this.p10.apertura(imagen1,  matrizParaLlenar));
            }
        });
        this.createSidebarButton("Esqueletizado", e-> {
            if(this.papa.hayErrores()) {
                Imagen imagen1;
                if (this.papa.isSelectorImagen()) {
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if (this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel10.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);

                this.papa.setImagenResult(this.p10.esqueletizado(imagen1,  matrizParaLlenar));
            }
        });
        //========================= 0PRACTICA 11 =========================
        this.container.add(new JSeparator(JSeparator.HORIZONTAL));
        this.createSidebarButton("Erosion gris", e-> {
            if(this.papa.hayErrores()) {
                Imagen imagen1;
                if (this.papa.isSelectorImagen()) {
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if (this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel10.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);

                this.papa.setImagenResult(this.p10.erosionarGris(imagen1,  matrizParaLlenar));
            }
        });
        this.createSidebarButton("Dilatacion Gris", e -> {
            if(this.papa.hayErrores()) {
                Imagen imagen1;
                if (this.papa.isSelectorImagen()) {
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if (this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel10.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);

                this.papa.setImagenResult(this.p10.dilatarGris(imagen1,  matrizParaLlenar));
            }
        });
        this.createSidebarButton("Apertura gris", e-> {
            if(this.papa.hayErrores()) {
                Imagen imagen1;
                if (this.papa.isSelectorImagen()) {
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if (this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel10.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);

                this.papa.setImagenResult(this.p10.aperturaGris(imagen1,  matrizParaLlenar));
            }
        });
        this.createSidebarButton("Clausura Gris", e-> {
            if(this.papa.hayErrores()) {
                Imagen imagen1;
                if (this.papa.isSelectorImagen()) {
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if (this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }
                int tamanoReal = (int) comboKernel10.getSelectedItem();
                double[][] matrizParaLlenar = this.papa.darMatrizDeseada(tamanoReal);

                this.papa.setImagenResult(this.p10.clausuraGris(imagen1,  matrizParaLlenar));
            }
        });
        //Practica 12:
        JSlider sliderRadioCorte;
        JLabel lblValorRadio;
        sliderRadioCorte = new JSlider(JSlider.HORIZONTAL, 10, 150, 45);
        sliderRadioCorte.setMajorTickSpacing(20);
        sliderRadioCorte.setPaintTicks(true);
        sliderRadioCorte.setPaintLabels(true);
        sliderRadioCorte.setBackground(this.getBackground());
        lblValorRadio = new JLabel("Frecuencia de Corte (Do): 45");
        lblValorRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        sliderRadioCorte.addChangeListener(e -> {
            lblValorRadio.setText("Frecuencia de Corte (Do): " + sliderRadioCorte.getValue());
        });
        this.container.add(sliderRadioCorte);
        this.createSidebarButton("Filtrar en Frecuencia", e -> {
            // Verificar si el sistema de la app reporta un estado correcto/errores
            if (this.papa.hayErrores()) {
                Imagen imagen1;

                // Determinar si procesamos la original o un resultado de un paso previo
                if (this.papa.isSelectorImagen()) {
                    imagen1 = this.papa.getImageOriginal();
                } else {
                    if (this.papa.getHayIMagenResult()) {
                        imagen1 = this.papa.getImageResult();
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                        return;
                    }
                }

                // 1. Leer el parámetro de entrada (Radio de corte Do) desde tu JSlider
                double radioSeleccionado = (double) sliderRadioCorte.getValue();

                // 2. Ejecutar el pipeline completo y actualizar el lienzo de salida a través de this.papa
                Imagen resultadoFiltrado = this.filtrarCompleto(imagen1, radioSeleccionado, this.papa);

                this.papa.setImagenResult(resultadoFiltrado);

                // Guardamos una copia en tu barra lateral de esta última imagen procesada en frecuencia
                // para cuando configuremos el botón 2 (Métricas).
                //thisultimaImagenFrecuencia = resultadoFiltrado;
                this.papa.setFourier(true);
                this.papa.setUltimaFrecuenciaFourier(resultadoFiltrado);
            }
        });
        this.container.add(lblValorRadio);

    }
    // =========================================================================
    // MÉTODO COORDINADOR PARA TU ARQUITECTURA
    // =========================================================================
    public Imagen filtrarCompleto(Imagen imgOriginal, double radioCorte, Object ventanaPapa) {
        // 1. Calcular FFT 2D Directa
        Complejo[][] espectro = this.p12.calcularFFT2D(imgOriginal);

        // 2. Centrar cuadrantes (Bajas frecuencias al medio)
        this.p12.intercambiarCuadrantes(espectro);

        // 3. Obtener la magnitud visual del espectro en niveles de gris
        Imagen imgMagnitud = this.p12.obtenerImagenMagnitud(espectro);

        // [OPCIONAL] Si tu ventana principal (this.papa) tiene un método para mostrar el espectro
        // en un tercer panel o pestaña, se lo puedes inyectar aquí. Por ejemplo:
        // ((TuVentanaPrincipalClass) ventanaPapa).setImagenEspectro(imgMagnitud);

        // 4. Aplicar el Filtro Gaussiano Pasa-Bajas sobre la matriz compleja
        this.p12.aplicarFiltroGaussianoPasaBajas(espectro, radioCorte);

        // 5. Deshacer el centrado (Revertir cuadrantes antes de la inversa)
        this.p12.intercambiarCuadrantes(espectro);

        // 6. Calcular la FFT 2D Inversa para regresar al espacio/tiempo
        Imagen imgFiltradaEspacio = this.p12.calcularIFFT2D(espectro, imgOriginal.getTipoActual(), imgOriginal.getNumCanales());

        return imgFiltradaEspacio;
    }

    /*private void agregarBotonesIniciales() {
        String[] botones = {
                "Botón de ejemplo", "Otro botón", "Más opciones",
                "Configuración", "Filtros", "Brillo",
                "Contraste", "Escala de grises", "Saturación", "Nitidez"
        };

        for (String texto : botones) {
            JButton btn = createSidebarButton(texto);
            container.add(btn);
            container.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio entre botones
        }
    }*/

    private void createSidebarButton(String text) {
        this.createSidebarButton(text, null); // Llama al principal con acción nula
    }

    private void createSidebarButton(String text, java.awt.event.ActionListener accion) {
        JButton btn = new JButton(text);

        // 1. COLORES BASE (Asegúrate de que sean consistentes)
        Color colorAzulOriginal = new Color(0, 123, 255);
        Color colorTextoNormal = Color.WHITE;
        Color colorHover = new Color(0, 86, 179);

        // 2. CONFIGURACIÓN DE FLATLAF (Propiedades de cliente)
        // Agregamos "background" y "foreground" directamente en el String de estilo
        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; " +
                        "borderWidth: 0; " +
                        "focusWidth: 0; " +
                        "innerFocusWidth: 0; " +
                        "margin: 8,15,8,15; " +
                        "background: #007BFF; " +
                        "foreground: #FFFFFF"
        );

        // 3. FUERZA BRUTA DE SWING (Esto es lo que evita el gris inicial)
        btn.setBackground(colorAzulOriginal);
        btn.setForeground(colorTextoNormal);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        if (accion != null) btn.addActionListener(accion);

        // 4. MOUSE LISTENER (Mantiene el diseño azul al salir)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(colorHover);
                btn.putClientProperty(FlatClientProperties.STYLE,
                        "arc: 12; margin: 10,20,10,20; background: #0056b3; foreground: #FFFFFF");
                btn.revalidate();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // AL SALIR: Forzamos que vuelva al azul exacto y margen original
                btn.setBackground(colorAzulOriginal);
                btn.putClientProperty(FlatClientProperties.STYLE,
                        "arc: 8; margin: 8,15,8,15; background: #007BFF; foreground: #FFFFFF");
                btn.revalidate();
            }
        });
        container.add(btn);
        container.add(Box.createRigidArea(new Dimension(0, 10)));
        //return btn;
    }
}