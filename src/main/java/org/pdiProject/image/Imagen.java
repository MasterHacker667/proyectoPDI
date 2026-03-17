package org.pdiProject.image;

public class Imagen {
    //MetadatosImagenOriginal:
    private String nombreImagenO;
    private int canalesImagenO;
    private int anchoInmagenO;
    private int largoImagenO;
    private byte[] pixelesImagenO;
    public Imagen(String nombreImagenO, int canales, int ancho, int largo, byte[] pixelesImagenO){
        this.nombreImagenO = nombreImagenO;
        this.canalesImagenO = canales;
        this.anchoInmagenO = ancho;
        this.largoImagenO = largo;
        this.pixelesImagenO = pixelesImagenO;
    }
    //Getters y Setters
    public void setAnchoInmagenO(int ancho){
        this.anchoInmagenO = ancho;
    }
    public void setLargoImagenO(int largo){
        this.largoImagenO = largo;
    }
    public void setPixeles(byte[] pix){
        this.pixelesImagenO = pix;
    }
    public String getNombreImagen(){
        return this.nombreImagenO;
    }
    public int getnoCanales(){
        return this.canalesImagenO;
    }
    public int []getAnchoLargo(){
        return new int[]{this.anchoInmagenO, this.largoImagenO};
    }
    public byte[] getPixelesImagen(){
        return this.pixelesImagenO;
    }
    //Metodos auxiliares
    public int[] getPixel(int x, int y) {
        int numCanales = this.canalesImagenO;
        int index = (y * this.anchoInmagenO + x) * numCanales;
        int[] pixel = new int[numCanales];

        for (int i = 0; i < numCanales; i++) {
            // Usamos & 0xFF porque en Java el byte es signado (-128 a 127)
            // y queremos el valor real (0 a 255)
            pixel[i] = this.pixelesImagenO[index + i] & 0xFF;
        }
        return pixel;
    }
}
