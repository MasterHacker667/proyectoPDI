package escom.project;

import com.formdev.flatlaf.FlatClientProperties;
import escom.project.Backend.FiltrosNoLineales_P9;
import escom.project.Backend.Imagen.Imagen;
import escom.project.Backend.Mod2_Estadistica;

import javax.swing.*;
import java.awt.*;

public class CustomFooter extends JPanel {

    private JPanel gridContainer;
    private MainWin papa;
    private Mod2_Estadistica diseccionadoeEstadistico;
    private FiltrosNoLineales_P9 p9;
    public CustomFooter(MainWin papa) {
        this.papa = papa;
        this.p9 = new FiltrosNoLineales_P9();
        setLayout(new BorderLayout());
        this.diseccionadoeEstadistico = new Mod2_Estadistica();
        // Le damos una altura fija de 180px para que quepan bien un par de filas
        setPreferredSize(new Dimension(0, 180));
        setBackground(new Color(224, 224, 224));

        // Panel que contendrá la rejilla
        // 0 filas significa que puede crecer infinitamente, 6 columnas fijas
        gridContainer = new JPanel(new GridLayout(0, 6, 10, 10));
        gridContainer.setBackground(new Color(224, 224, 224));
        gridContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ScrollPane ahora VERTICAL (Mucho más estable en Linux)
        JScrollPane scrollPane = new JScrollPane(gridContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Estilo moderno para la barra vertical
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "width: 8; " +
                        "thumbArc: 999; " +
                        "trackArc: 999; " +
                        "track: #E0E0E0; " +
                        "thumb: #B0B0B0"
        );

        add(scrollPane, BorderLayout.CENTER);

        // Agregamos un montón de botones para probar que se creen las filas
        //agregarMuchosBotones();
        this.createFooterButton("Histograma de canales", e -> {
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
                //Accion
                this.diseccionadoeEstadistico.generarHistograma(imagen1);
                System.out.println(imagen1.getNumCanales());
            }

        });
        this.createFooterButton("Histograma de Probabilidades", e -> {
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
                //Accion
                this.diseccionadoeEstadistico.calcularProbabilidad(imagen1);
            }

        });
        //Practica 9:
        this.createFooterButton("Histograma de Probabilidades", e -> {
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
                //Accion
                this.p9.calcularMetricasRuidoFiltro(this.papa.getImageOriginal(), this.papa.getImageResult());
            }

        });

    }

    private void agregarMuchosBotones() { //Comentar este metodo una vez se termine el diseño
        // Probamos con 15 botones para ver cómo se crean 3 filas
        for (int i = 1; i <= 15; i++) {
            final int index = i;
            this.createFooterButton("Acción " + i, e -> {
                System.out.println("Ejecutando acción: " + index);
            });
        }
    }

    private void createFooterButton(String text, java.awt.event.ActionListener accion) {
        JButton btn = new JButton(text);

        Color colorAzulOriginal = new Color(0, 123, 255);
        Color colorHover = new Color(0, 86, 179);

        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; " +
                        "borderWidth: 0; " +
                        "focusWidth: 0; " +
                        "margin: 5,10,5,10; " + // Un poco más compacto para que quepan 6
                        "background: #007BFF; " +
                        "foreground: #FFFFFF"
        );

        btn.setBackground(colorAzulOriginal);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // En GridLayout, el componente intenta llenar la celda,
        // así que el PreferredSize es menos crítico pero ayuda a la altura
        btn.setPreferredSize(new Dimension(0, 45));

        if (accion != null) btn.addActionListener(accion);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(colorHover);
                btn.revalidate();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(colorAzulOriginal);
                btn.revalidate();
            }
        });
        this.gridContainer.add(btn);
        //return btn;
    }
}