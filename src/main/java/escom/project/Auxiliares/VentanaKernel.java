package escom.project.Auxiliares;

import escom.project.MainWin;
import javax.swing.*;
import java.awt.*;

public class VentanaKernel extends JDialog {
    private JPanel panelCuadricula;
    private JTextField[][] camposTexto;
    private JComboBox<Integer> comboTamano;
    private JComboBox<Integer> comboPosicion;
    private JButton btnModo;
    private JLabel lblPosicion;
    private JPanel panelPresets; // El nuevo panel izquierdo

    private boolean esModoRepuesto = false;
    private MainWin papa;

    public VentanaKernel(MainWin papa) {
        this.papa = papa;
        setTitle("Editor de Kernels");
        setSize(750, 550); // Aumentamos un poco el ancho para la barra lateral
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL SUPERIOR ---
        JPanel panelSuperior = new JPanel(new FlowLayout());
        panelSuperior.add(new JLabel("Tamaño:"));
        comboTamano = new JComboBox<>(new Integer[]{3, 5, 7, 9, 11});
        comboTamano.addActionListener(e -> {
            actualizarCuadricula();
            gestionarVisibilidadPresets();
        });
        panelSuperior.add(comboTamano);

        btnModo = new JButton("Ir a Kerneles de Repuesto");
        lblPosicion = new JLabel("Posición (0-6):");
        comboPosicion = new JComboBox<>(new Integer[]{0, 1, 2, 3, 4, 5, 6});
        lblPosicion.setVisible(false);
        comboPosicion.setVisible(false);

        btnModo.addActionListener(e -> {
            esModoRepuesto = !esModoRepuesto;
            btnModo.setText(esModoRepuesto ? "Regresar a Kernel Principal" : "Ir a Kerneles de Repuesto");
            lblPosicion.setVisible(esModoRepuesto);
            comboPosicion.setVisible(esModoRepuesto);
            actualizarCuadricula();
        });

        comboPosicion.addActionListener(e -> actualizarCuadricula());

        panelSuperior.add(btnModo);
        panelSuperior.add(lblPosicion);
        panelSuperior.add(comboPosicion);
        add(panelSuperior, BorderLayout.NORTH);

        // --- PANEL IZQUIERDO: PRESETS (Solo para 3x3) ---
        panelPresets = new JPanel();
        panelPresets.setLayout(new BoxLayout(panelPresets, BoxLayout.Y_AXIS));
        panelPresets.setBorder(BorderFactory.createTitledBorder("Presets 3x3"));

        agregarBotonPreset("Bordes Suaves", new double[][]{{0,-1,0}, {-1,4,-1}, {0,-1,0}});
        agregarBotonPreset("Muy Sensible", new double[][]{{-1,-1,-1}, {-1,8,-1}, {-1,-1,-1}});
        agregarBotonPreset("V. Negativa", new double[][]{{0,1,0}, {1,-4,1}, {0,1,0}});

        add(panelPresets, BorderLayout.WEST);

        // --- PANEL CENTRAL ---
        panelCuadricula = new JPanel();
        add(new JScrollPane(panelCuadricula), BorderLayout.CENTER);

        // --- PANEL INFERIOR ---
        JButton btnAplicar = new JButton("Aplicar y Cerrar");
        btnAplicar.addActionListener(e -> dispose());
        add(btnAplicar, BorderLayout.SOUTH);

        actualizarCuadricula();
        gestionarVisibilidadPresets();
        agregarPresetsNoLineales();
    }

    private void agregarBotonPreset(String nombre, double[][] valores) {
        JButton btn = new JButton(nombre);
        btn.setMaximumSize(new Dimension(150, 30));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> cargarMatrizPreset(valores));
        panelPresets.add(btn);
        panelPresets.add(Box.createVerticalStrut(10)); // Espaciado
    }

    private void cargarMatrizPreset(double[][] valores) {
        int n = (int) comboTamano.getSelectedItem();
        int pos = (int) comboPosicion.getSelectedItem();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // Actualizamos visualmente
                camposTexto[i][j].setText(String.valueOf(valores[i][j]));
                // Guardamos en la lógica
                if (esModoRepuesto) {
                    this.papa.guardarEnBancoSecundario(n, pos, i, j, valores[i][j]);
                } else {
                    this.papa.getMisMatrices()[0][i][j] = valores[i][j];
                }
            }
        }
    }

    private void gestionarVisibilidadPresets() {
        int n = (int) comboTamano.getSelectedItem();
        // Solo mostramos los botones si el tamaño es 3
        panelPresets.setVisible(true);
        revalidate();
    }

    private void actualizarCuadricula() {
        panelCuadricula.removeAll();
        int n = (int) comboTamano.getSelectedItem();
        int pos = (int) comboPosicion.getSelectedItem();
        panelCuadricula.setLayout(new GridLayout(n, n, 5, 5));
        camposTexto = new JTextField[n][n];

        double[][] matrizActual;
        if (esModoRepuesto) {
            matrizActual = this.papa.darRespaldoDeseado(n, pos);
        } else {
            int idx = (n - 3) / 2;
            matrizActual = this.papa.getMisMatrices()[idx];
        }

        if (matrizActual == null) return;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                JTextField txt = new JTextField(String.valueOf(matrizActual[i][j]), 4);
                txt.setHorizontalAlignment(JTextField.CENTER);
                final int f = i, c = j;
                txt.addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusLost(java.awt.event.FocusEvent evt) {
                        guardarValor(n, pos, f, c, txt);
                    }
                });
                camposTexto[i][j] = txt;
                panelCuadricula.add(txt);
            }
        }
        panelCuadricula.revalidate();
        panelCuadricula.repaint();
    }

    private void guardarValor(int n, int pos, int f, int c, JTextField campo) {
        try {
            double valor = Double.parseDouble(campo.getText());
            if (valor > 255) valor = 255;
            if (valor < -255) valor = -255;
            campo.setText(String.valueOf(valor));

            if (esModoRepuesto) {
                this.papa.guardarEnBancoSecundario(n, pos, f, c, valor);
            } else {
                int idx = (n - 3) / 2;
                this.papa.getMisMatrices()[idx][f][c] = valor;
            }
        } catch (NumberFormatException e) {
            campo.setText("0.0");
        }
    }
    private void agregarPresetsNoLineales() {
        // Creamos un subpanel exclusivo para tus filtros no lineales
        JPanel subPanelNoLineal = new JPanel();
        subPanelNoLineal.setLayout(new BoxLayout(subPanelNoLineal, BoxLayout.Y_AXIS));
        subPanelNoLineal.setBorder(BorderFactory.createTitledBorder("Filtros No Lineales (1s y 0s)"));

        // 1. Botón Cuadrada
        JButton btnCuadrada = new JButton("M. Cuadrada");
        btnCuadrada.addActionListener(e -> generarGeometriaPreset((int) comboTamano.getSelectedItem(), "CUADRADA"));
        subPanelNoLineal.add(btnCuadrada);
        subPanelNoLineal.add(Box.createVerticalStrut(5));

        // 2. Botón Cruz
        JButton btnCruz = new JButton("M. Cruz");
        btnCruz.addActionListener(e -> generarGeometriaPreset((int) comboTamano.getSelectedItem(), "CRUZ"));
        subPanelNoLineal.add(btnCruz);
        subPanelNoLineal.add(Box.createVerticalStrut(5));

        // 3. Botón Equis (X)
        JButton btnEquis = new JButton("M. Equis (X)");
        btnEquis.addActionListener(e -> generarGeometriaPreset((int) comboTamano.getSelectedItem(), "EQUIS"));
        subPanelNoLineal.add(btnEquis);
        subPanelNoLineal.add(Box.createVerticalStrut(5));

        // 4. Botón Diamante
        JButton btnDiamante = new JButton("M. Diamante");
        btnDiamante.addActionListener(e -> generarGeometriaPreset((int) comboTamano.getSelectedItem(), "DIAMANTE"));
        subPanelNoLineal.add(btnDiamante);
        subPanelNoLineal.add(Box.createVerticalStrut(5));

        // 5. Botón Horizontal
        JButton btnHorizontal = new JButton("M. Horizontal");
        btnHorizontal.addActionListener(e -> generarGeometriaPreset((int) comboTamano.getSelectedItem(), "HORIZONTAL"));
        subPanelNoLineal.add(btnHorizontal);

        // Homogeneizar tamaño de los nuevos botones para que se vean simétricos
        for (Component c : subPanelNoLineal.getComponents()) {
            if (c instanceof JButton) {
                ((JButton) c).setMaximumSize(new Dimension(140, 28));
                ((JButton) c).setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        }

        // Agregamos un espacio de separación después de tus componentes viejos y metemos el subpanel
        panelPresets.add(Box.createVerticalStrut(15));
        panelPresets.add(subPanelNoLineal);

        panelPresets.revalidate();
        panelPresets.repaint();
    }
    private void generarGeometriaPreset(int n, String tipo) {
        double[][] nuevosValores = new double[n][n];
        int centro = n / 2;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                switch (tipo) {
                    case "CUADRADA":
                        nuevosValores[i][j] = 1.0;
                        break;
                    case "CRUZ":
                        nuevosValores[i][j] = (i == centro || j == centro) ? 1.0 : 0.0;
                        break;
                    case "HORIZONTAL":
                        nuevosValores[i][j] = (i == centro) ? 1.0 : 0.0;
                        break;
                    case "EQUIS":
                        nuevosValores[i][j] = (i == j || i == (n - 1 - j)) ? 1.0 : 0.0;
                        break;
                    case "DIAMANTE":
                        // Algoritmo de distancia Manhattan para formar un rombo perfecto
                        if (Math.abs(i - centro) + Math.abs(j - centro) <= centro) {
                            nuevosValores[i][j] = 1.0;
                        } else {
                            nuevosValores[i][j] = 0.0;
                        }
                        break;
                    default:
                        nuevosValores[i][j] = 0.0;
                }
            }
        }

        // Guardamos los datos en tus arreglos originales y refrescamos la GUI
        inyectarMatrizEnSistema(n, nuevosValores);
    }

    private void inyectarMatrizEnSistema(int n, double[][] valores) {
        int pos = (int) comboPosicion.getSelectedItem();
        int idx = (n - 3) / 2;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (esModoRepuesto) {
                    this.papa.guardarEnBancoSecundario(n, pos, i, j, valores[i][j]);
                } else {
                    this.papa.getMisMatrices()[idx][i][j] = valores[i][j];
                }
            }
        }
        // Llama a tu método original para pintar los números actualizados en los JTextFields
        actualizarCuadricula();
    }
}