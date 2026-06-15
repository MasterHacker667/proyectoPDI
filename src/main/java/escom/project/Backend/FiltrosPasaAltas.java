package escom.project.Backend;

import escom.project.Backend.Imagen.Imagen;

public class FiltrosPasaAltas {
    private FiltrosPasaBajas kernel;
    public FiltrosPasaAltas(){
        this.kernel = new FiltrosPasaBajas();
    }

    // 2.2 Aplicar filtros pasa altas (Gradientes de primer orden)[cite: 7]
    public Imagen operadorRoberts(Imagen img) {
        // Definimos las máscaras de 3x3 para que sean compatibles con tu motor
        // pero manteniendo los valores de Roberts del PDF
        double[][] h1 = {
                { 1,  0,  0},
                { 0, -1,  0},
                { 0,  0,  0}
        };

        double[][] h2 = {
                { 0,  1,  0},
                {-1,  0,  0},
                { 0,  0,  0}
        };

        // 1. Obtenemos el gradiente en la primera diagonal
        Imagen resH1 = kernel.aplicarConvolucion(img, h1, 3);

        // 2. Obtenemos el gradiente en la segunda diagonal
        Imagen resH2 = kernel.aplicarConvolucion(img, h2, 3);

        // 3. Combinamos los resultados (Magnitud)
        int ancho = img.getAncho();
        int alto = img.getAlto();
        Imagen resultado = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        for (int i = 0; i < ancho * alto; i++) {
            // El PDF sugiere la suma de valores absolutos para eficiencia
            int r = Math.min(255, Math.abs(resH1.getRed(i)) + Math.abs(resH2.getRed(i)));
            int g = Math.min(255, Math.abs(resH1.getGreen(i)) + Math.abs(resH2.getGreen(i)));
            int b = Math.min(255, Math.abs(resH1.getBlue(i)) + Math.abs(resH2.getBlue(i)));

            resultado.setPixel(i, r, g, b, img.getAlpha(i));
        }

        return resultado;
    }

    public Imagen operadorPrewitt(Imagen img) {
        // Máscara para bordes horizontales (detecta cambios verticales)
        double[][] hx = {
                {-1, -1, -1},
                { 0,  0,  0},
                { 1,  1,  1}
        };

        // Máscara para bordes verticales (detecta cambios horizontales)
        double[][] hy = {
                {-1, 0, 1},
                {-1, 0, 1},
                {-1, 0, 1}
        };

        // 1. Aplicamos la convolución para ambas direcciones usando tu motor
        Imagen resHx = kernel.aplicarConvolucion(img, hx, 3);
        Imagen resHy = kernel.aplicarConvolucion(img, hy, 3);

        int ancho = img.getAncho();
        int alto = img.getAlto();
        Imagen resultado = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        // 2. Combinamos usando la magnitud (Suma de valores absolutos según el PDF)
        for (int i = 0; i < ancho * alto; i++) {
            // Calculamos la magnitud para cada canal de color
            int r = Math.min(255, Math.abs(resHx.getRed(i)) + Math.abs(resHy.getRed(i)));
            int g = Math.min(255, Math.abs(resHx.getGreen(i)) + Math.abs(resHy.getGreen(i)));
            int b = Math.min(255, Math.abs(resHx.getBlue(i)) + Math.abs(resHy.getBlue(i)));

            resultado.setPixel(i, r, g, b, img.getAlpha(i));
        }

        return resultado;
    }

    public Imagen operadorSobel(Imagen img, double[][] kx, double[][] ky) {
        int ancho = img.getAncho();
        int alto = img.getAlto();
        int tamanoKernel = kx.length; // Asumimos que ky.length es igual

        // 1. Aplicamos la convolución para el eje X y el eje Y por separado.
        // IMPORTANTE: Estos métodos deben devolver los valores "puros" (con negativos y > 255)
        // para poder hacer el cálculo de la magnitud correctamente.
        double[][] gradienteX = obtenerConvolucionPura(img, kx, tamanoKernel);
        double[][] gradienteY = obtenerConvolucionPura(img, ky, tamanoKernel);

        Imagen resultado = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        // 2. Combinar ambos gradientes
        for (int i = 0; i < ancho * alto; i++) {
            // Obtenemos el valor de X y Y para ese píxel
            double valX = gradienteX[0][i]; // Usando el canal rojo/gris como referencia
            double valY = gradienteY[0][i];

            // 3. Aplicar Pitágoras: Magnitud = sqrt(Gx^2 + Gy^2)
            int magnitud = (int) Math.sqrt(Math.pow(valX, 2) + Math.pow(valY, 2));

            // 4. Ahora sí, limitamos el resultado final a [0, 255]
            if (magnitud > 255) magnitud = 255;
            if (magnitud < 0) magnitud = 0;

            // Seteamos el píxel (en gris para detectar bordes)
            resultado.setPixel(i, magnitud, magnitud, magnitud, img.getAlpha(i));
        }

        return resultado;
    }

    /**
     * Método auxiliar que hace la convolución pero NO corta los valores en 0-255.
     * Esto es vital para Sobel, Kirsch y Robinson.
     */
    private double[][] obtenerConvolucionPura(Imagen img, double[][] kernel, int tam) {
        int ancho = img.getAncho();
        int alto = img.getAlto();
        double[][] salida = new double[1][ancho * alto]; // Solo un canal para simplicidad de bordes
        int radio = tam / 2;

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                double suma = 0;
                for (int i = 0; i < tam; i++) {
                    for (int j = 0; j < tam; j++) {
                        // Usamos el % para imagen periódica (bordes infinitos)
                        int nx = (x - radio + j + ancho) % ancho;
                        int ny = (y - radio + i + alto) % alto;
                        suma += img.getRed(ny * ancho + nx) * kernel[i][j];
                    }
                }
                salida[0][y * ancho + x] = suma;
            }
        }
        return salida;
    }

    public Imagen operadorFreiChen(Imagen img, double[][] kx, double[][] ky) {
        int ancho = img.getAncho();
        int alto = img.getAlto();
        int tamanoKernel = kx.length;

        // 1. Obtenemos las convoluciones puras (con negativos y valores > 255)
        // Reutilizamos el método auxiliar que definimos en Sobel
        double[][] gradienteX = obtenerConvolucionPura(img, kx, tamanoKernel);
        double[][] gradienteY = obtenerConvolucionPura(img, ky, tamanoKernel);

        Imagen resultado = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        // 2. Combinar
        for (int i = 0; i < ancho * alto; i++) {
            double valX = gradienteX[0][i];
            double valY = gradienteY[0][i];

            // 3. Magnitud del Gradiente
            int magnitud = (int) Math.sqrt(Math.pow(valX, 2) + Math.pow(valY, 2));

            // 4. Sistema de seguridad final para la imagen
            if (magnitud > 255) magnitud = 255;
            if (magnitud < 0) magnitud = 0;

            resultado.setPixel(i, magnitud, magnitud, magnitud, img.getAlpha(i));
        }

        return resultado;
    }

    // Gradientes en Compás (8 direcciones)[cite: 7]
    public Imagen operadorKirsch(Imagen img, double[][] kernelDireccional) {
        int ancho = img.getAncho();
        int alto = img.getAlto();
        int n = kernelDireccional.length; // Normalmente 3

        // 1. Aplicamos la convolución pura con el kernel que nos mandaron
        // (Ya sea el Norte, Sur, Este, etc.)
        double[][] conv = obtenerConvolucionPura(img, kernelDireccional, n);

        Imagen resultado = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        // 2. Procesamos el resultado
        for (int i = 0; i < ancho * alto; i++) {
            // En Kirsch y Robinson se usa el valor absoluto del resultado
            int valor = (int) Math.abs(conv[0][i]);

            // Sistema de seguridad final para el píxel
            if (valor > 255) valor = 255;
            if (valor < 0) valor = 0;

            resultado.setPixel(i, valor, valor, valor, img.getAlpha(i));
        }

        return resultado;
    }

    public Imagen operadorRobinson(Imagen img, double[][] kernelDireccional) {
        int ancho = img.getAncho();
        int alto = img.getAlto();
        int n = kernelDireccional.length;

        // 1. Convolución pura (reutilizando tu método auxiliar)
        double[][] conv = obtenerConvolucionPura(img, kernelDireccional, n);

        Imagen resultado = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        // 2. Procesamiento de Magnitud
        for (int i = 0; i < ancho * alto; i++) {
            // Al igual que en Kirsch, tomamos el valor absoluto
            int valor = (int) Math.abs(conv[0][i]);

            // Seguridad [0, 255]
            if (valor > 255) valor = 255;
            if (valor < 0) valor = 0;

            resultado.setPixel(i, valor, valor, valor, img.getAlpha(i));
        }

        return resultado;
    }

    public Imagen operadorLaplaciano(Imagen img, double[][] kernelLaplaciano) {
        int ancho = img.getAncho();
        int alto = img.getAlto();

        // El tamaño (n) se extrae directamente de la matriz de entrada
        // Puede ser 3, 5, 7, 9 o 11
        int n = kernelLaplaciano.length;

        // 1. Aplicar convolución pura
        // Reutilizamos el motor que ya creamos, que maneja cualquier tamaño de kernel
        double[][] conv = obtenerConvolucionPura(img, kernelLaplaciano, n);

        Imagen resultado = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        // 2. Procesamiento de la respuesta de segunda derivada
        for (int i = 0; i < ancho * alto; i++) {
            // Obtenemos el valor resultante de la convolución
            double valorPuro = conv[0][i];

            // En procesamiento de bordes con Laplaciano, existen dos caminos comunes:
            // A) Valor Absoluto: Para ver los bordes como líneas blancas brillantes.
            // B) Desplazamiento: Para ver el "paso por cero" (menos común en visualización básica).

            int pixelFinal = (int) Math.abs(valorPuro);

            // Sistema de seguridad final [0, 255]
            if (pixelFinal > 255) pixelFinal = 255;
            if (pixelFinal < 0) pixelFinal = 0;

            resultado.setPixel(i, pixelFinal, pixelFinal, pixelFinal, img.getAlpha(i));
        }

        return resultado;
    }
    public Imagen laplacianoDeGauss(Imagen img, double[][] kernelLoG, double sigma, int tamanoKernel) {
        int ancho = img.getAncho();
        int alto = img.getAlto();
        int radio = tamanoKernel / 2;

        // Llenamos la matriz con la fórmula del Sombrero Mexicano
        for (int y = -radio; y <= radio; y++) {
            for (int x = -radio; x <= radio; x++) {
                // Distancia al cuadrado desde el centro
                double d2 = (x * x + y * y);
                double s2 = sigma * sigma;
                double s4 = s2 * s2;

                // Fórmula estándar del LoG (Laplacian of Gaussian)
                double exponente = -d2 / (2 * s2);
                double valor = (-1.0 / (Math.PI * s4)) * (1.0 - (d2 / (2.0 * s2))) * Math.exp(exponente);

                kernelLoG[y + radio][x + radio] = valor;
            }
        }

        // Nota: Para que sea un Laplaciano real, la suma de la matriz debería ser 0.
        // Opcionalmente se puede normalizar, pero el valor absoluto final lo resuelve.

        // 2. Aplicar la convolución pura con nuestro nuevo kernel
        double[][] conv = obtenerConvolucionPura(img, kernelLoG, tamanoKernel);

        Imagen resultado = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        // 3. Procesar magnitud
        for (int i = 0; i < ancho * alto; i++) {
            // Al igual que el Laplaciano, buscamos los cruces por cero o bordes realzados
            int valor = (int) Math.abs(conv[0][i]);

            // Seguridad [0, 255]
            if (valor > 255) valor = 255;
            if (valor < 0) valor = 0;

            resultado.setPixel(i, valor, valor, valor, img.getAlpha(i));
        }

        return resultado;
    }
    //AUXILIARES
    public double sugerirSigma(int n) {
        // Una aproximación simple basada en la regla n = 6sigma
        return (double) n / 6.0;
    }
}