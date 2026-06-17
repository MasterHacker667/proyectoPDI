package escom.project.Backend;
import escom.project.Backend.Imagen.Imagen;
import escom.project.Backend.Imagen.Tipo;


public class TransformadaFourier_P12 {

    // ==========================================
    // 1. ALGORITMO FFT 1D (DIRECTA E INVERSA)
    // ==========================================
    private Complejo[] fft1D(Complejo[] x, boolean inversa) {
        int n = x.length;
        if (n <= 1) return x;

        // Verificar que sea potencia de 2
        if ((n & (n - 1)) != 0) {
            throw new IllegalArgumentException("El tamaño debe ser potencia de 2");
        }

        // Separar elementos pares e impares
        Complejo[] pares = new Complejo[n / 2];
        Complejo[] impares = new Complejo[n / 2];
        for (int i = 0; i < n / 2; i++) {
            pares[i] = x[2 * i];
            impares[i] = x[2 * i + 1];
        }

        Complejo[] rPares = fft1D(pares, inversa);
        Complejo[] rImpares = fft1D(impares, inversa);

        Complejo[] resultado = new Complejo[n];
        double angular = (inversa ? 2.0 : -2.0) * Math.PI / n;

        for (int k = 0; k < n / 2; k++) {
            double th = k * angular;
            // W_n^k = cos(th) + j*sin(th)
            Complejo wk = new Complejo(Math.cos(th), Math.sin(th));

            // Multiplicación compleja: rImpares[k] * wk
            double r = rImpares[k].real * wk.real - rImpares[k].imag * wk.imag;
            double im = rImpares[k].real * wk.imag + rImpares[k].imag * wk.real;
            Complejo t = new Complejo(r, im);

            resultado[k] = new Complejo(rPares[k].real + t.real, rPares[k].imag + t.imag);
            resultado[k + n / 2] = new Complejo(rPares[k].real - t.real, rPares[k].imag - t.imag);
        }
        return resultado;
    }

    // ==========================================
    // 2. TRANSFORMADA DE FOURIER 2D (SEPARABLE)
    // ==========================================
    public Complejo[][] calcularFFT2D(Imagen img) {
        int ancho = img.getAncho();
        int alto = img.getAlto(); // Deben ser potencias de 2 (ej. 256x256, 512x512)

        Complejo[][] espectro = new Complejo[alto][ancho];

        // Inicializar matriz compleja con los niveles de gris de la imagen
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pos = y * ancho + x;
                espectro[y][x] = new Complejo(img.getBlue(pos), 0.0);
            }
        }

        // Paso A: FFT en las filas
        for (int y = 0; y < alto; y++) {
            espectro[y] = fft1D(espectro[y], false);
        }

        // Paso B: FFT en las columnas
        for (int x = 0; x < ancho; x++) {
            Complejo[] columna = new Complejo[alto];
            for (int y = 0; y < alto; y++) {
                columna[y] = espectro[y][x];
            }
            Complejo[] colTransformada = fft1D(columna, false);
            for (int y = 0; y < alto; y++) {
                espectro[y][x] = colTransformada[y];
            }
        }

        return espectro;
    }

    public Imagen calcularIFFT2D(Complejo[][] espectro, Tipo tipoOriginal, int canalesOriginal) {
        int alto = espectro.length;
        int ancho = espectro[0].length;

        Complejo[][] temp = new Complejo[alto][ancho];
        for (int y = 0; y < alto; y++) {
            System.arraycopy(espectro[y], 0, temp[y], 0, ancho);
        }

        // Paso A: IFFT en las filas
        for (int y = 0; y < alto; y++) {
            temp[y] = fft1D(temp[y], true);
        }

        // Paso B: IFFT en las columnas
        for (int x = 0; x < ancho; x++) {
            Complejo[] columna = new Complejo[alto];
            for (int y = 0; y < alto; y++) {
                columna[y] = temp[y][x];
            }
            Complejo[] colTransformada = fft1D(columna, true);
            for (int y = 0; y < alto; y++) {
                temp[y][x] = colTransformada[y];
            }
        }

        // Crear la imagen reconstruida en el dominio del espacio/tiempo
        Imagen res = new Imagen(ancho, alto, tipoOriginal, canalesOriginal);
        int total = ancho * alto;

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pos = y * ancho + x;
                // Dividir entre N*M por la fórmula de la transformada inversa
                double val = temp[y][x].real / total;

                // Truncar valores de gris espurios fuera del rango [0, 255]
                int gris = (int) Math.min(255, Math.max(0, val));
                res.setPixel(pos, gris, gris, gris, 255);
            }
        }
        return res;
    }

    // ==========================================
    // 3. CENTRADO DE CUADRANTES (SHIFT)
    // ==========================================
    public void intercambiarCuadrantes(Complejo[][] espectro) {
        int alto = espectro.length;
        int ancho = espectro[0].length;
        int mitadH = alto / 2;
        int mitadW = ancho / 2;

        for (int y = 0; y < mitadH; y++) {
            for (int x = 0; x < mitadW; x++) {
                // Intercambio de cuadrante I con III, y II con IV
                Complejo tempI = espectro[y][x];
                espectro[y][x] = espectro[y + mitadH][x + mitadW];
                espectro[y + mitadH][x + mitadW] = tempI;

                Complejo tempII = espectro[y][x + mitadW];
                espectro[y][x + mitadW] = espectro[y + mitadH][x];
                espectro[y + mitadH][x] = tempII;
            }
        }
    }

    // ==========================================
    // 4. DESPLIEGUE DE MAGNITUD (ESCALA LOGARÍTMICA)
    // ==========================================
    public Imagen obtenerImagenMagnitud(Complejo[][] espectro) {
        int alto = espectro.length;
        int ancho = espectro[0].length;
        Imagen res = new Imagen(ancho, alto, Tipo.RGBA, 1);

        double[][] magn = new double[alto][ancho];
        double maxLog = 0;

        // Paso 1: Calcular la magnitud de cada celda y aplicar transformación logarítmica c*log(1 + |F(u,v)|)
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                double m = espectro[y][x].magnitud();
                magn[y][x] = Math.log(1.0 + m);
                if (magn[y][x] > maxLog) {
                    maxLog = magn[y][x];
                }
            }
        }

        // Paso 2: Normalizar al rango [0, 255] para visualización en pantalla
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pos = y * ancho + x;
                int gris = 0;
                if (maxLog > 0) {
                    gris = (int) ((magn[y][x] / maxLog) * 255.0);
                }
                res.setPixel(pos, gris, gris, gris, 255);
            }
        }
        return res;
    }

    // ==========================================
    // 5. FILTRADO FRECUENCIAL: GAUSSIANO PASA-BAJAS
    // ==========================================
    public void aplicarFiltroGaussianoPasaBajas(Complejo[][] espectroCentrado, double doFrecuenciaCorte) {
        int alto = espectroCentrado.length;
        int ancho = espectroCentrado[0].length;

        // El centro geométrico (u=0, v=0) está en el medio tras el intercambio de cuadrantes
        int centroY = alto / 2;
        int centroX = ancho / 2;

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                // Distancia radial D(u,v) al centro del espectro
                double duv = Math.sqrt(Math.pow(x - centroX, 2) + Math.pow(y - centroY, 2));

                // Ecuación 5.26 del PDF de la profesora: H(u,v) = e^(-D^2 / (2 * Do^2))
                double huv = Math.exp(-Math.pow(duv, 2) / (2.0 * Math.pow(doFrecuenciaCorte, 2)));

                // Multiplicación píxel a píxel en el espectro complejo
                espectroCentrado[y][x].real *= huv;
                espectroCentrado[y][x].imag *= huv;
            }
        }
    }
    // =========================================================================
    // PUNTO 5: CALCULO DE MÉTRICA DE ERROR CUADRÁTICO MEDIO (MSE)
    // =========================================================================
    public double calcularMSE(Imagen imgConvolucion, Imagen imgFrecuencia) {
        if (imgConvolucion.getAncho() != imgFrecuencia.getAncho() ||
                imgConvolucion.getAlto() != imgFrecuencia.getAlto()) {
            throw new IllegalArgumentException("Las imágenes deben tener las mismas dimensiones para ser comparadas.");
        }

        int ancho = imgConvolucion.getAncho();
        int alto = imgConvolucion.getAlto();
        double sumaDiferenciasAlCuadrado = 0.0;
        int totalPixeles = ancho * alto;

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pos = y * ancho + x;

                // Tomamos el canal Blue como el nivel de gris
                int grisConv = imgConvolucion.getBlue(pos);
                int grisFrec = imgFrecuencia.getBlue(pos);

                // Diferencia (e) entre el píxel del espacio y de la frecuencia
                double diferencia = grisConv - grisFrec;

                // Sumatoria de e^2
                sumaDiferenciasAlCuadrado += diferencia * diferencia;
            }
        }

        // Dividir entre el total de píxeles (M * N)
        return sumaDiferenciasAlCuadrado / totalPixeles;
    }
}