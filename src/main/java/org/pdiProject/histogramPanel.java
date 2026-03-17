package org.pdiProject;

import org.pdiProject.graficWindow.grafficWindow;

import javax.swing.*;
import java.awt.*;

public class histogramPanel extends JPanel {
    private Software padre;
    private grafficWindow graffic;
    public histogramPanel(Software padre){
        this.graffic = new grafficWindow();
        this.padre = padre;
        setLayout(new GridLayout(2, 2, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Histogramas y Gráficos"));

        // Creamos los botones/espacios según tu diseño
        JButton btnGrafico1 = new JButton("Histogramas RGB/BInario/Escala de Grises");
        JButton btnGrafico2 = new JButton("Coming Soon");
        JButton btnGrafico3 = new JButton("Coming soon");
        JButton btnGrafico4 = new JButton("Coming soon");

        // Al agregarlos en este orden con GridLayout(2,2), se acomodan:
        // [1] [3]
        // [2] [4]
        // (Nota: Swing llena primero la fila, por lo que el orden de add importa)
        btnGrafico1.addActionListener(e -> {
            if(this.padre.gethayImagen()){
                this.graffic.setVisible(true);
                this.CrearHistograma(1);
            }else{
                JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
            }

        });
        this.add(btnGrafico1); // Fila 1, Col 1
        this.add(btnGrafico3); // Fila 1, Col 2
        this.add(btnGrafico2); // Fila 2, Col 1
        this.add(btnGrafico4); // Fila 2, Col 2
    }
    public void CrearHistograma(int opc){
        if(this.padre.gethayImagen()){
            if(this.padre.getPanel()){
                //Codigo para cambiar la imagen original y poner el resultado en result
                if(opc == 1){
                    //Obtener imagen orifinal y hacer su histograma con codigo 1 (1 = RGB).
                    this.graffic.histogramaElect(this.padre.getImagenOriginal());
                }

                /*if(opc == 2) this.brilloMas(this.padre.getImagenOriginal(), true);
                if(opc == 3) this.brilloMas(this.padre.getImagenOriginal(), false);*/
            } else if(!this.padre.getHayResult()){
                JOptionPane.showMessageDialog(this, "No hay ninguna imagen Result para hacer esta operacion");
            }else{
                //Codigo para cambiar la result y reemplazarlo por un result nuevo
                if(opc == 1) this.graffic.histogramaElect(this.padre.getImagenResult());
                /*if(opc == 2) this.brilloMas(this.padre.getImagenResult(), true);
                if(opc == 3) this.brilloMas(this.padre.getImagenResult(), false);*/
            }
        }else{
            JOptionPane.showMessageDialog(this, "No hay imagenes para trabajar");
        }

    }
}
