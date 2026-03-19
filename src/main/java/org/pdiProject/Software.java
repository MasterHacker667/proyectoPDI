package org.pdiProject;
import org.pdiProject.image.Imagen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

public class Software extends JFrame{
    private boolean hayImagen;
    private File archivo;
    private File imagenCambio;
    private String direccionRust;

    //ImagenOficialOriginal
    private Imagen imagenOriginal;
    private Imagen imagenResult;

    // Paneles de Componentes (Botones y Gráficos)
    private configSuperiorBar configBar;
    private TransformationPanels transPanels;
    private histogramPanel histogramPanel;
    private boolean panel; //True original y False para tarns
    private boolean hayResult;

    //Paneles contenedores de imagenes
    private JPanel panelContenedorImagenes; //Este contiene a los dos de aqui abajo
    private JPanel espacioImagenOriginal;
    private JPanel espacioImagenResultado;

    //Auxiliares para manejar otros formatos de imagen a color
    private double[] bufferDecimal = null; // Aquí guardamos YIQ, HSV o HSI
    private String formatoBuffer = "NINGUNO"; //

    public Software(){
        this.panel = true;
        this.hayResult = false;
        this.setTitle("Software PDI");
        this.setSize(1200, 800);

        this.setLayout(new BorderLayout(5, 5));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Agregar los paneles
        this.transPanels = new TransformationPanels(this);
        JScrollPane scrollLateral = new JScrollPane(this.transPanels);

        // Configuramos para que solo salga el scroll vertical si es necesario
        scrollLateral.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollLateral.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollLateral.setBorder(null); // Quitar borde doble para que se vea limpio como en Figma

        //Agregamos el SCROLL, no el panel directamente
        this.add(scrollLateral, BorderLayout.WEST);
        this.setLocationRelativeTo(null);

        this.configBar = new configSuperiorBar(this);
        this.add(configBar, BorderLayout.NORTH);

        this.histogramPanel = new histogramPanel(this);
        this.add(histogramPanel, BorderLayout.SOUTH);


        //Agregando paneles de imagenes locales:
        this.panelContenedorImagenes = new JPanel(new GridLayout(1, 2, 10, 0));
        this.panelContenedorImagenes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.espacioImagenOriginal = new JPanel(new BorderLayout());
        this.espacioImagenOriginal.setBackground(Color.DARK_GRAY); // Color de fondo para que se note el área
        this.espacioImagenOriginal.add(new JLabel("Imagen Original", SwingConstants.CENTER), BorderLayout.CENTER);

        this.espacioImagenResultado = new JPanel(new BorderLayout());
        this.espacioImagenResultado.setBackground(Color.GRAY);
        this.espacioImagenResultado.add(new JLabel("Resultado", SwingConstants.CENTER), BorderLayout.CENTER);

        // 4. ENSAMBLAJE FINAL
        this.panelContenedorImagenes.add(this.espacioImagenOriginal);
        this.panelContenedorImagenes.add(this.espacioImagenResultado);

        // Agregamos el contenedor al centro del BorderLayout principal
        this.add(this.panelContenedorImagenes, BorderLayout.CENTER);
        this.setLocationRelativeTo(null);

    }
    private void mostrarImagenResult(){

    }

    //Setters y Getters
    public boolean gethayImagen(){
        return this.hayImagen;
    }
    public void setHayImagen(boolean cosa){
        this.hayImagen = cosa;
    }
    //imagen Original
    public File getImagen(){
        return this.archivo;
    }
    public void setImagen(File archivo){
        this.archivo = archivo;
        this.mostrarImagenOriginal();
    }
    private void mostrarImagenOriginal(){
        if (this.archivo == null) return;

        try {
            // 1. Leer la imagen del archivo local
            ImageIcon iconoOriginal = new ImageIcon(this.archivo.getAbsolutePath());
            Image imgOriginal = iconoOriginal.getImage();

            // 2. Calcular el escalado para que quepa en el panel sin deformarse
            // Obtenemos el tamaño actual del contenedor
            int anchoPanel = espacioImagenOriginal.getWidth();
            int altoPanel = espacioImagenOriginal.getHeight();

            // Si el panel aún no se dibuja (tamaño 0), usamos un valor por defecto o esperamos
            if (anchoPanel == 0) anchoPanel = 500;
            if (altoPanel == 0) altoPanel = 600;

            // Escalado suave (Smooth)
            Image imgEscalada = imgOriginal.getScaledInstance(anchoPanel, altoPanel, Image.SCALE_SMOOTH);

            // 3. Crear el JLabel que contendrá la imagen
            JLabel etiquetaImagen = new JLabel(new ImageIcon(imgEscalada));

            // 4. Limpiar el panel (quitar el texto "Imagen Original") y poner la foto
            espacioImagenOriginal.removeAll();
            espacioImagenOriginal.add(etiquetaImagen, BorderLayout.CENTER);

            // 5. Refrescar la interfaz
            espacioImagenOriginal.revalidate();
            espacioImagenOriginal.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al mostrar imagen: " + e.getMessage());
        }
    }
    //Imagen de Cambio
    public File getImagenR(){
        return this.imagenCambio;
    }
    public void setImagenR(File imagen){
        this.imagenCambio = imagen;
    }
    public String getDireccionRust(){
        return this.direccionRust;
    }
    public Imagen getImagenOriginal(){
        return this.imagenOriginal;
    }
    public void setImagenOriginal(Imagen img){
        this.imagenOriginal = img;
        this.hayResult = false;
        this.hayImagen = true;
    }

    public Imagen getImagenResult(){
        return this.imagenResult;
    }
    public void setImagenResult(Imagen img){
        this.imagenResult = img;
        this.hayResult = true;
        //Imagen para colocar esto en el panelResult
        try {
            int ancho = imagenResult.getAnchoLargo()[0];
            int alto = imagenResult.getAnchoLargo()[1];
            int canales = imagenResult.getnoCanales();
            byte[] pixeles = imagenResult.getPixelesImagen();

            // 1. Determinar el tipo de imagen según los canales
            int tipoImagen;
            if (canales == 4) {
                tipoImagen = BufferedImage.TYPE_4BYTE_ABGR;
            } else if (canales == 1) {
                tipoImagen = BufferedImage.TYPE_BYTE_GRAY;
            } else {
                tipoImagen = BufferedImage.TYPE_3BYTE_BGR;
            }

            // 2. Crear un BufferedImage que apunte directamente a nuestros bytes
            BufferedImage bi = new BufferedImage(ancho, alto, tipoImagen);

            // Copiamos los datos del arreglo de tu clase Imagen al buffer de la imagen de Java
            byte[] targetPixels = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
            System.arraycopy(pixeles, 0, targetPixels, 0, pixeles.length);

            // 3. Calcular escalado (reutilizando tu lógica de la original)
            int anchoPanel = espacioImagenResultado.getWidth();
            int altoPanel = espacioImagenResultado.getHeight();
            if (anchoPanel <= 0) anchoPanel = 500; // Valores seguros por si el panel no ha cargado
            if (altoPanel <= 0) altoPanel = 600;

            Image imgEscalada = bi.getScaledInstance(anchoPanel, altoPanel, Image.SCALE_SMOOTH);

            // 4. Actualizar el componente visual
            espacioImagenResultado.removeAll();
            espacioImagenResultado.add(new JLabel(new ImageIcon(imgEscalada)), BorderLayout.CENTER);

            // 5. Forzar a Swing a redibujar
            espacioImagenResultado.revalidate();
            espacioImagenResultado.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al procesar resultado: " + e.getMessage());
        }
    }
    public void setPanel(boolean cosa){
        this.panel = cosa;
    }
    public boolean getPanel(){
        return this.panel;
    }
    public boolean getHayResult(){
        return this.hayResult;
    }
    // --- MÉTODOS PARA MANEJO DE BUFFER (PRÁCTICA 2) ---
    public double[] getBufferDecimal() {
        return this.bufferDecimal;
    }

    public void setBufferDecimal(double[] buffer) {
        this.bufferDecimal = buffer;
    }

    public String getFormatoBuffer() {
        return this.formatoBuffer;
    }

    public void setFormatoBuffer(String formato) {
        this.formatoBuffer = formato;
    }

}
