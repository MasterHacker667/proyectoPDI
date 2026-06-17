package escom.project;
import escom.project.Backend.Imagen.Imagen;
import javax.swing.*;
import java.awt.*;

public class MainWin extends JFrame {
    //Partes del sistema
    private CustomHeader header;
    private CustomSidebar sidebar;
    private CustomFooter footer;
    private MainPanel centro;

    //COnfiguracion de imagenes
    private Imagen imageOriginal;
    private boolean hayImagenOriginal;
    private Imagen imageResult;
    private boolean hayImagenResult;
    private Imagen ultimaFrecuenciaFourier;
    //COnexiones con otros atributos
    private boolean selectorImagen;

    //Auxiliares
    public boolean hayAlgunaImagen;
    private boolean fourier;

    //Kernels originales
    private double[][][] misMatrices = { new double[3][3], new double[5][5], new double[7][7], new double[9][9], new double[11][11] };
    //kernels de respaldo:
    private double[][][][] bancoSecundario = new double[5][7][][];

    public MainWin() {
        this.hayImagenOriginal = false;
        this.hayImagenResult = false;
        this.hayAlgunaImagen = false;
        this.selectorImagen = true;
        this.ultimaFrecuenciaFourier = null;
        this.fourier = false;
        setTitle("PDI V2.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        this.inicializarBancoSecundario();
        /*System.out.println(misMatrices);
        for(double a[][] : misMatrices){
            System.out.println("Matrices: ");
            for (double b[] : a){
                for (double c : b){
                    System.out.print("[ " + c +" ] ");
                }
                System.out.println("");
            }
            System.out.println("----------------------------");
        }*/

        // Agregamos nuestra barra superior personalizada
        this.header = new CustomHeader(this);
        add(this.header, BorderLayout.NORTH);

        //Agregamos nuestro panel izquierdo
        this.sidebar = new CustomSidebar(this);
        add(this.sidebar, BorderLayout.WEST);

        //Agregamos nuestro panel SUR
        this.footer = new CustomFooter(this);
        add(this.footer, BorderLayout.SOUTH);

        //Panel central
        this.centro = new MainPanel(this);
        add(this.centro, BorderLayout.CENTER);
        // Aquí iría el resto de tu estructura (el SplitPane y el panel de estadísticas)
        // ...
    }
    public boolean getFourier(){
        return this.fourier;
    }
    public void setFourier(boolean a){
        this.fourier = a;
    }
    public void setImagenOriginal(Imagen image){
        this.imageOriginal = image;
        //Codigo para insertar esta imagen en MainPanel
        this.centro.cargarOriginal(image.toBufferedImage());
        this.setHayImageOriginal(true);
        /*if(this.hayImagenResult){
            this.centro.reiniciarResultado();
        }*/
        //this.setHayImageResult(false);
        hayAlgunaImagen = true;
    }
    public void setImagenResult(Imagen image){
        if(this.hayImagenOriginal){
            this.setHayImageResult(true);
            this.imageResult = image;
            this.centro.cargarResultado(image.toBufferedImage());
        }else{
            JOptionPane.showMessageDialog(this, "No hay imagen que mostrar");
        }

    }
    public void setHayImageOriginal(boolean v) {this.hayImagenOriginal = v;}
    public void setHayImageResult(boolean v) {this.hayImagenResult = v;}

    public Imagen getImageResult() {
        return imageResult;
    }


    public Imagen getImageOriginal() {
        return imageOriginal;
    }


    public boolean isSelectorImagen() {
        return selectorImagen;
        //true para Imagen original y false para Imagen Result
    }
    public boolean getHayImagenOriginal(){
        return this.hayImagenOriginal;
    }
    public boolean getHayIMagenResult(){
        return this.hayImagenResult;
    }
    public void setSelectorImagen(boolean selectorImagen) {
        this.selectorImagen = selectorImagen;
    }
    public boolean hayErrores(){
        if(!this.hayAlgunaImagen){
            JOptionPane.showMessageDialog(this, "No hay imagenes para mostrar");
            return false; //Hay error
        }
        return true; //no hay error
    }

    public double[][][] getMisMatrices() {
        return misMatrices;
    }

    public void setMisMatrices(double[][][] misMatrices) {
        this.misMatrices = misMatrices;
    }
    public double[][] darMatrizDeseada(int n){
        double [][][]matricesC = this.getMisMatrices();
        if(n <= 3){
            return matricesC[0];
        } else if (n <= 5) {
            return matricesC[1];
        } else if (n <= 7) {
            return matricesC[2];
        } else if (n <= 9) {
            return matricesC[3];
        }else{
            return matricesC[4];
        }
    }

    public double[][][][] getBancoSecundario() {
        return bancoSecundario;
    }

    public void setBancoSecundario(double[][][][] bancoSecundario) {
        this.bancoSecundario = bancoSecundario;
    }
    public double[][] darRespaldoDeseado(int tamano, int indiceRespaldo) {
        int filaTamano;

        // Traducimos el tamaño al índice del primer nivel del array
        switch (tamano) {
            case 3:  filaTamano = 0; break;
            case 5:  filaTamano = 1; break;
            case 7:  filaTamano = 2; break;
            case 9:  filaTamano = 3; break;
            case 11: filaTamano = 4; break;
            default: filaTamano = 0; // Por defecto 3x3
        }

        // Validamos que el índice de respaldo esté en rango (0-6)
        if (indiceRespaldo < 0 || indiceRespaldo > 6) {
            indiceRespaldo = 0;
        }

        return this.bancoSecundario[filaTamano][indiceRespaldo];
        /*
        Como invocar
        * double[][] k1 = this.papa.darMatrizDeseada(3);          // La principal (Dirección 1)
        * double[][] k2 = this.papa.darRespaldoDeseado(3, 0);     // Respaldo 0 (Dirección 2)
        * double[][] k3 = this.papa.darRespaldoDeseado(3, 1);
        * */
    }
    // En MainWin.java
    private void inicializarBancoSecundario() {
        int[] tams = {3, 5, 7, 9, 11};

        for (int i = 0; i < 5; i++) { // Para cada tamaño
            for (int j = 0; j < 7; j++) { // Para cada una de las 7 posiciones
                // ESTA ES LA LÍNEA QUE TE FALTA:
                bancoSecundario[i][j] = new double[tams[i]][tams[i]];

                // Opcional: Llenarla de ceros explícitamente (aunque Java lo hace solo)
                for(int f=0; f < tams[i]; f++) {
                    for(int c=0; c < tams[i]; c++) {
                        bancoSecundario[i][j][f][c] = 0.0;
                    }
                }
            }
        }
    }
    public void guardarEnBancoSecundario(int tamano, int posicion, int f, int c, double valor) {
        int idxTam;
        switch (tamano) {
            case 3: idxTam = 0; break;
            case 5: idxTam = 1; break;
            case 7: idxTam = 2; break;
            case 9: idxTam = 3; break;
            case 11: idxTam = 4; break;
            default: idxTam = 0;
        }
        // Guardamos en la estructura de 4 dimensiones: [tamaño][posición][fila][columna]
        this.bancoSecundario[idxTam][posicion][f][c] = valor;
    }

    public Imagen getUltimaFrecuenciaFourier() {
        return ultimaFrecuenciaFourier;
    }

    public void setUltimaFrecuenciaFourier(Imagen ultimaFrecuenciaFourier) {
        this.ultimaFrecuenciaFourier = ultimaFrecuenciaFourier;
    }
}
