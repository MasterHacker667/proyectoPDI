package org.pdiProject;

import org.pdiProject.image.Imagen;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class configSuperiorBar extends JPanel{
    private Software padre;
    // Componentes del panel
    private JButton btnCargar;
    private JButton btnDownload;
    private JToggleButton btnSwitchVista;

    public configSuperiorBar(Software padre) {
        this.padre = padre;
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        this.setBorder(BorderFactory.createEtchedBorder());
        // 1. Configuración del Layout (Horizontal)
        // FlowLayout con alineación centrada y espacio de 20px entre elementos
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        this.setBorder(BorderFactory.createEtchedBorder());

        // 2. Inicializar Botón de Carga
        this.btnCargar = new JButton("Cargar Imagen");

        // 3. Inicializar Botón de Descarga (el círculo pequeño en tu Figma)
        this.btnDownload = new JButton("↓");
        this.btnDownload.setToolTipText("Download");

        // 4. Inicializar el Switch (JToggleButton)
        // Este es el que pediste que cambiara de color y texto al picarle
        this.btnSwitchVista = new JToggleButton("Mostrando: Original");
        this.btnSwitchVista.setBackground(Color.BLUE);
        this.btnSwitchVista.setForeground(Color.WHITE);
        this.btnSwitchVista.setFocusPainted(false); // Para que se vea más limpio

        // 5. Lógica del Switch para cambiar estado
        this.btnSwitchVista.addActionListener(e -> {
            if (this.btnSwitchVista.isSelected()) {
                this.btnSwitchVista.setText("Mostrando: Resultado");
                this.btnSwitchVista.setBackground(Color.RED);
                // Codigo para poner cosas en el panel de imagen result
                this.padre.setPanel(false);
            } else {
                this.btnSwitchVista.setText("Mostrando: Original");
                this.btnSwitchVista.setBackground(Color.BLUE);
                //Codigo para poner cosas en el panel de imagen original
                this.padre.setPanel(true);
            }
        });
        this.btnCargar.addActionListener(e -> {
            cargarImagen();
        });
        // 6. Agregar al panel siguiendo el orden del diseño
        this.add(btnCargar);
        this.add(btnDownload);
        this.add(btnSwitchVista);
    }



    public void cargarImagen(){

        // Creamos el objeto selector de archivos
        JFileChooser selector = new JFileChooser();

        // Le ponemos un título a la ventana que se va a abrir
        selector.setDialogTitle("Selecciona una imagen para procesar");
        // Creamos el filtro con los formatos permitidos
        FileNameExtensionFilter filtro = new FileNameExtensionFilter(
                "Imágenes permitidas",
                "svg", "bmp", "png", "jpg", "jpeg", "gif"
        );

        // Le asignamos el filtro al selector
        selector.setFileFilter(filtro);

        // Desactivamos la opción de "Todos los archivos" para obligar a usar los del filtro
        selector.setAcceptAllFileFilterUsed(false);
        // Mostramos la ventana y guardamos la respuesta del usuario
        int respuesta = selector.showOpenDialog(this);

        if (respuesta == JFileChooser.APPROVE_OPTION) {
            // Obtenemos el archivo físicamente
            File archivoElegido = selector.getSelectedFile();
            //Extraer los metadatos
            try{
                BufferedImage img = ImageIO.read(archivoElegido);
                if (img == null) {
                    throw new IOException("El archivo no es una imagen válida o está corrupto.");
                }
                int ancho = img.getWidth();
                int alto = img.getHeight();
                String nombre = archivoElegido.getName();
                int numCanales = img.getColorModel().getNumComponents();

                byte[] pixeles;

                if (img.getRaster().getDataBuffer() instanceof DataBufferByte) {
                    pixeles = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
                } else {
                    // Si la imagen viene en otro formato (ej. Int o UShort),
                    // la normalizamos a su versión de bytes pero respetando sus canales originales
                    BufferedImage byteImage = new BufferedImage(ancho, alto,
                            (numCanales == 4) ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);

                    Graphics2D g = byteImage.createGraphics();
                    g.drawImage(img, 0, 0, null);
                    g.dispose();

                    pixeles = ((DataBufferByte) byteImage.getRaster().getDataBuffer()).getData();
                    numCanales = byteImage.getColorModel().getNumComponents();

                }
                byte canalesFinales = (byte) numCanales;
                System.out.println("--- Reporte de Descomposición ---");
                System.out.println("Imagen: " + nombre);
                System.out.println("Canales detectados: " + canalesFinales);
                System.out.println("Resolución: " + ancho + "x" + alto);
                System.out.println("Tamaño del vector: " + pixeles.length + " bytes");
                System.out.println("Archivo seleccionado: " + archivoElegido.getAbsolutePath());
                Imagen imagenOrg = new Imagen(nombre, canalesFinales, ancho, alto, pixeles);
                this.padre.setImagenOriginal(imagenOrg);
                this.padre.setImagen(archivoElegido);

            }catch (Exception e){
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
            // De momento, solo imprimimos la ruta para saber que funciona


            // Aquí es donde en el siguiente paso validaremos la extensión
            // y lo pasaremos al padre (Software)
        }
    }
}
