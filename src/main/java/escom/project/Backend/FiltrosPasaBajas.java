package escom.project.Backend;

import escom.project.Backend.Imagen.Imagen;

public class FiltrosPasaBajas {

    // 1. Programar la convolución (Motor general)
    public Imagen aplicarConvolucion(Imagen img, double[][] kernel, int tamano) {
        int ancho = img.getAncho();
        int alto = img.getAlto();
        // Creamos la imagen de destino con las mismas dimensiones
        Imagen resultado = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        // Radio del kernel para saber cuánto sobresale (ej. si tamaño es 3, radio es 1)
        int radio = tamano / 2;

        // Recorremos cada píxel de la imagen original
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {

                double sumR = 0, sumG = 0, sumB = 0;

                // Recorremos el kernel[cite: 8]
                for (int i = -radio; i <= radio; i++) {
                    for (int j = -radio; j <= radio; j++) {

                        // LÓGICA DE IMAGEN PERIÓDICA
                        // (x + j) para columnas, (y + i) para filas
                        int nx = (x + j + ancho) % ancho;
                        int ny = (y + i + alto) % alto;

                        // Índice en el arreglo unidimensional de la clase Imagen
                        int indiceVecino = ny * ancho + nx;

                        // Peso del kernel en la posición actual
                        double peso = kernel[i + radio][j + radio];

                        // Extraemos canales y aplicamos el peso[cite: 5, 8]
                        sumR += img.getRed(indiceVecino) * peso;
                        sumG += img.getGreen(indiceVecino) * peso;
                        sumB += img.getBlue(indiceVecino) * peso;
                    }
                }

                // Truncado de valores para asegurar el rango [0, 255]
                int r = Math.min(255, Math.max(0, (int) Math.round(sumR)));
                int g = Math.min(255, Math.max(0, (int) Math.round(sumG)));
                int b = Math.min(255, Math.max(0, (int) Math.round(sumB)));
                int a = img.getAlpha(y * ancho + x); // Preservamos el Alpha original

                // Guardamos el píxel procesado en la imagen de destino
                resultado.setPixel(y * ancho + x, r, g, b, a);
            }
        }
        return resultado;
    }

    // 2.1 Filtros para aberrar y suavizar[cite: 6]: BOTON 1
    public Imagen filtroPromediador(Imagen img, double[][] kernel, int tamano) {
        // 1. Verificamos si la matriz está vacía (llena de ceros)
        // Si está vacía, la llenamos con el promedio estándar para que no salga una imagen negra
        boolean esMatrizVacia = true;
        for (int i = 0; i < tamano; i++) {
            for (int j = 0; j < tamano; j++) {
                if (kernel[i][j] != 0) {
                    esMatrizVacia = false;
                    break;
                }
            }
        }

        // 2. Si la matriz es 0, aplicamos la lógica de normalización automática del PDF
        if (esMatrizVacia) {
            double valorNormalizado = 1.0 / (tamano * tamano);
            for (int i = 0; i < tamano; i++) {
                for (int j = 0; j < tamano; j++) {
                    kernel[i][j] = valorNormalizado;
                }
            }
        } else {
            // 3. Si el usuario puso valores (0-255), debemos NORMALIZAR
            // Para que la imagen no se queme (ponga blanca), la suma del kernel debe ser 1.0
            double suma = 0;
            for (double[] fila : kernel) {
                for (double valor : fila) suma += valor;
            }

            // Solo normalizamos si la suma es mayor a 0 para evitar división por cero
            if (suma > 0) {
                for (int i = 0; i < tamano; i++) {
                    for (int j = 0; j < tamano; j++) {
                        kernel[i][j] /= suma;
                    }
                }
            }
        }

        // 4. Enviar al motor de convolución general
        return aplicarConvolucion(img, kernel, tamano);
    }

    public Imagen filtroGaussiano(Imagen img, double[][] kernelOriginal, int tamano, double sigma) {
        int radio = tamano / 2;
        double sumaTotal = 0;
        // CREAMOS UNA COPIA para no arruinar la matriz original de papa
        double[][] kernelTrabajo = new double[tamano][tamano];

        boolean esMatrizVacia = true;
        for (int i = 0; i < tamano; i++) {
            for (int j = 0; j < tamano; j++) {
                if (kernelOriginal[i][j] != 0) {
                    esMatrizVacia = false;
                    kernelTrabajo[i][j] = kernelOriginal[i][j]; // Copiamos el valor manual
                }
            }
        }

        if (esMatrizVacia) {
            for (int i = -radio; i <= radio; i++) {
                for (int j = -radio; j <= radio; j++) {
                    double exponente = -(i * i + j * j) / (2 * sigma * sigma);
                    double valor = Math.exp(exponente);
                    kernelTrabajo[i + radio][j + radio] = valor;
                    sumaTotal += valor;
                }
            }
        } else {
            for (int i = 0; i < tamano; i++) {
                for (int j = 0; j < tamano; j++) {
                    sumaTotal += kernelTrabajo[i][j];
                }
            }
        }

        if (sumaTotal > 0) {
            for (int i = 0; i < tamano; i++) {
                for (int j = 0; j < tamano; j++) {
                    kernelTrabajo[i][j] /= sumaTotal;
                }
            }
        }

        return aplicarConvolucion(img, kernelTrabajo, tamano);
    }

    // Filtro para definir contornos (High-boost/Enfatizado)[cite: 6]
    public Imagen filtroEnfatizado(Imagen img, double[][] kernelOriginal, int tamano, double A) {
        double[][] kernelTrabajo = new double[tamano][tamano];
        boolean esMatrizVacia = true;

        for (int i = 0; i < tamano; i++) {
            for (int j = 0; j < tamano; j++) {
                if (kernelOriginal[i][j] != 0) {
                    esMatrizVacia = false;
                    kernelTrabajo[i][j] = kernelOriginal[i][j];
                }
            }
        }

        if (esMatrizVacia) {
            int totalCeldas = tamano * tamano;
            double valorCentral = A + (totalCeldas - 1);

            for (int i = 0; i < tamano; i++) {
                for (int j = 0; j < tamano; j++) {
                    kernelTrabajo[i][j] = -1.0;
                }
            }
            kernelTrabajo[tamano / 2][tamano / 2] = valorCentral;
        }

        return aplicarConvolucion(img, kernelTrabajo, tamano);
    }

    //AUXILIARES
    private int validarRango(int valor) {
        return Math.min(255, Math.max(0, valor));
    }
}