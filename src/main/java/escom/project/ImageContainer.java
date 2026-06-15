package escom.project;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ImageContainer extends JPanel {
    private String titulo;
    private Image imagenActual;

    public ImageContainer(String titulo) {
        this.titulo = titulo;
        setLayout(new BorderLayout());
        setBackground(new Color(224, 224, 224)); // #E0E0E0

        // Estilo moderno con FlatLaf (bordes redondeados y sombra suave)
        putClientProperty(FlatClientProperties.STYLE, "arc: 15");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (imagenActual == null) {
            // DIBUJAR BORDE PUNTEADO (Si no hay imagen)
            float[] dash = {10.0f};
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
            g2.setColor(new Color(192, 192, 192)); // #C0C0C0
            g2.draw(new RoundRectangle2D.Float(5, 5, w - 11, h - 11, 15, 15));

            // DIBUJAR TEXTO CENTRAL
            g2.setFont(new Font("Poppins", Font.BOLD, 18));
            g2.setColor(new Color(85, 85, 85)); // #555
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(titulo)) / 2;
            int ty = (h + fm.getAscent()) / 2 - 5;
            g2.drawString(titulo.toUpperCase(), tx, ty);
        } else {
            // DIBUJAR IMAGEN (Escalada para ajustar al cuadro)
            g2.drawImage(imagenActual, 0, 0, w, h, this);
        }
        g2.dispose();
    }

    public void setImagen(Image img) {
        this.imagenActual = img;
        repaint();
    }
    public void limpiar() {
        this.imagenActual = null;
        repaint();
    }
}