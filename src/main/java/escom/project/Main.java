package escom.project;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());

            // --- PERSONALIZACIÓN DEL SCROLLBAR ---
            UIManager.put("ScrollBar.thumbArc", 999);      // Bordes totalmente redondeados
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2)); // Margen para que se vea delgado
            UIManager.put("ScrollBar.width", 10);          // Grosor de la barra
            UIManager.put("ScrollBar.showButtons", false); // Quita las flechas de arriba/abajo

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        java.util.Locale.setDefault(java.util.Locale.US);
        MainWin app = new MainWin();
        app.setVisible(true);
    }
}