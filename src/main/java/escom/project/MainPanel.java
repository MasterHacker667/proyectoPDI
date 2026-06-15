package escom.project;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    private ImageContainer panelOriginal;
    private ImageContainer panelResultado;
    private MainWin papa;
    public MainPanel(MainWin papa) {
        this.papa = papa;
        setLayout(new GridLayout(1, 2, 20, 0)); // 1 fila, 2 columnas, 20px de gap
        setBackground(new Color(248, 249, 250)); // #F8F9FA
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelOriginal = new ImageContainer("Imagen Original");
        panelResultado = new ImageContainer("Imagen Resultado");

        add(panelOriginal);
        add(panelResultado);


    }

    // Métodos para actualizar las imágenes desde fuera
    public void cargarOriginal(Image img) { panelOriginal.setImagen(img); }
    public void cargarResultado(Image img) { panelResultado.setImagen(img); }

    //Metodos para reiniciarPaneles:
    public void reiniciarOriginal() {
        this.panelOriginal.limpiar();
    }

    public void reiniciarResultado() {
        this.panelResultado.limpiar();
    }


}