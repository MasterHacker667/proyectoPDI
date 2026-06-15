package escom.project;

import com.formdev.flatlaf.FlatClientProperties;
import escom.project.Auxiliares.VentanaKernel;
import escom.project.Backend.Imagen.Imagen;
import escom.project.Backend.Imagen.Tipo;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CustomHeader extends JPanel {
    private boolean selectorImagen;
    private MainWin papa;
    public CustomHeader(MainWin papa) {
        this.papa = papa;
        this.selectorImagen = true; //true : original  &  false: result
        // Fondo oscuro #2C2C2C
        setBackground(new Color(44, 44, 44));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // --- SECCIÓN IZQUIERDA ---
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        JLabel title = new JLabel("Editor de Imágenes");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Poppins", Font.BOLD, 16));

        JButton btnCargar = createModernButton("Cargar Imagen", new Color(0, 123, 255)); // Azul #007BFF
        btnCargar.addActionListener(e -> {
            JFileChooser selector = new JFileChooser();
            selector.setDialogTitle("Seleccionar imagen");
            selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imágenes (JPG, PNG, BMP)", "jpg", "jpeg", "png", "bmp"));
            int estado = selector.showOpenDialog(this);
            if (estado == JFileChooser.APPROVE_OPTION) {
                try {
                    java.io.File archivo = selector.getSelectedFile();
                    java.awt.image.BufferedImage bi = javax.imageio.ImageIO.read(archivo);

                    if (bi != null) {
                        // Instanciamos TU clase de Backend
                        Imagen nuevaImagen = new escom.project.Backend.Imagen.Imagen(bi);
                        if(nuevaImagen.getNumCanales() ==3){
                            nuevaImagen.setTipoActial(Tipo.RGB);
                        }else{
                            nuevaImagen.setTipoActial(Tipo.RGBA);
                        }
                        // IMPORTANTE: Aquí mandamos la imagen a la ventana principal
                        // Suponiendo que 'parent' es tu MainWin
                        this.papa.setImagenOriginal(nuevaImagen);

                        System.out.println("Imagen cargada exitosamente en el Backend.");
                        System.out.println("Tipo: " + nuevaImagen.getTipoActual());
                    }
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + ex.getMessage());
                }
            }
        });
        leftPanel.add(title);
        leftPanel.add(btnCargar);

        // --- SECCIÓN DERECHA ---
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JButton btnSelect = createModernButton("Imagen: Original", new Color(40, 167, 69)); // Verde #28A745

        btnSelect.addActionListener( e -> {
            if(this.selectorImagen){
                this.selectorImagen = false;
                btnSelect.setText("Imagen: Result");
                btnSelect.setBackground(new Color(220, 53, 69));
            }else{
                this.selectorImagen = true;
                btnSelect.setText("Imagen: Original");
                btnSelect.setBackground(new Color(40, 167, 69));
            }
            this.papa.setSelectorImagen(this.selectorImagen);

        });
        JButton btnDownload = createModernButton("Download", new Color(40, 167, 69));
        btnDownload.addActionListener(e -> {
            //Comprobar que haya imagenes
            if(this.papa.hayErrores()){
                //Hay alguna imagen, ahora debemos ver si nos pide la imagen resulto o la original
                if(this.papa.hayErrores()) {
                    Imagen imagen1;
                    if (this.papa.isSelectorImagen()) {
                        //Trabajamos con la imagen Original
                        imagen1 = this.papa.getImageOriginal();
                    } else {
                        //Trabajamos con la imagen Result
                        if (this.papa.getHayIMagenResult()) {
                            imagen1 = this.papa.getImageResult();
                        } else {
                            JOptionPane.showMessageDialog(this, "No hay imagen result disponible");
                            return;
                        }
                    }
                    if (imagen1 == null) return;

                    // 2. Configurar el selector de archivos
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Guardar Imagen");

                    // Filtros para extensiones comunes
                    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imagen PNG", "png"));
                    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imagen JPG", "jpg"));

                    int userSelection = fileChooser.showSaveDialog(this);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        java.io.File archivoAGuardar = fileChooser.getSelectedFile();
                        String path = archivoAGuardar.getAbsolutePath();

                        // Asegurarse de que tenga extensión (por defecto .png si no pone nada)
                        if (!path.toLowerCase().endsWith(".png") && !path.toLowerCase().endsWith(".jpg")) {
                            path += ".png";
                        }

                        try {
                            // 3. Convertir nuestra clase Imagen a BufferedImage y guardar
                            BufferedImage bi = imagen1.toBufferedImage();
                            String formato = path.endsWith(".jpg") ? "jpg" : "png";

                            boolean exito = javax.imageio.ImageIO.write(bi, formato, new java.io.File(path));

                            if (exito) {
                                JOptionPane.showMessageDialog(this, "Imagen guardada exitosamente en:\n" + path);
                            }
                        } catch (java.io.IOException ex) {
                            JOptionPane.showMessageDialog(this, "Error al guardar la imagen: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                    //Aqui pondremos el codigo para descargar una imagen
                    //this.papa.getImageOriginal();
                    //this.papa.getImageResult();
                }
            }
        });

        JButton matrices = createModernButton("Matrices", new Color(76, 40, 130));
        matrices.addActionListener(e -> {
            VentanaKernel editor = new VentanaKernel(this.papa);
            editor.setVisible(true);
        });

        rightPanel.add(matrices);
        rightPanel.add(btnSelect);

        add(btnDownload);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    private JButton createModernButton(String text, Color bg) {
        JButton btn = new JButton(text);

        // Estilo moderno con FlatLaf
        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc: 6; " +                      // Bordes redondeados (6px como tu CSS)
                        "foreground: #FFFFFF; " +         // Texto blanco
                        "borderWidth: 0; " +              // Sin borde de línea
                        "focusWidth: 0; " +               // Sin borde de enfoque feo
                        "innerFocusWidth: 0;"
        );

        btn.setBackground(bg);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }
}