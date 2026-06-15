package escom.project.Backend.Imagen;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Imagen {

    // Tipos de imagen (Enums para evitar errores de texto)


    private int ancho;
    private int alto;
    private int[] pixeles; // 0xAARRGGBB
    private int numCanales;
    private Tipo tipoActual;

    // Constructor desde una BufferedImage (Carga inicial)
    public Imagen(BufferedImage bi) {
        this.ancho = bi.getWidth();
        this.alto = bi.getHeight();
        this.numCanales = bi.getColorModel().hasAlpha() ? 4 : 3;
        this.tipoActual = (numCanales == 4) ? Tipo.RGBA : Tipo.RGB;

        // Extraemos los píxeles directamente para máxima velocidad
        this.pixeles = new int[ancho * alto];
        bi.getRGB(0, 0, ancho, alto, pixeles, 0, ancho);
    }

    // Constructor vacío (Para crear resultados)
    public Imagen(int ancho, int alto, Tipo tipo, int canales) {
        this.ancho = ancho;
        this.alto = alto;
        this.tipoActual = tipo;
        this.numCanales = canales;
        this.pixeles = new int[ancho * alto];
    }

    // --- MÉTODOS DE ACCESO RÁPIDO ---
    public int getAlpha(int i) { return (pixeles[i] >> 24) & 0xFF; }
    public int getRed(int i)   { return (pixeles[i] >> 16) & 0xFF; }
    public int getGreen(int i) { return (pixeles[i] >> 8)  & 0xFF; }
    public int getBlue(int i)  { return pixeles[i] & 0xFF; }

    public void setPixel(int i, int r, int g, int b, int a) {
        pixeles[i] = (a << 24) | (r << 16) | (g << 8) | b;
    }

    // --- CONVERSIÓN PARA MOSTRAR EN PANELES ---
    public BufferedImage toBufferedImage() {
        BufferedImage bi = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        bi.setRGB(0, 0, ancho, alto, pixeles, 0, ancho);
        return bi;
    }

    // Getters
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public Tipo getTipoActual() { return tipoActual; }
    public int getNumCanales() { return numCanales; }
    public int[] getPixeles() { return pixeles; }
    //Setters
    public void setTipoActial(Tipo n){
        this.tipoActual = n;
    }
}