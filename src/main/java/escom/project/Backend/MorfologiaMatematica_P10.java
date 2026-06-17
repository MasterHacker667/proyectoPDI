package escom.project.Backend;

import escom.project.Backend.Imagen.Imagen;

public class MorfologiaMatematica_P10 {
    public Imagen erosionar(Imagen img, double[][] kernel) {
        int ancho = img.getAncho();
        int alto = img.getAlto();

        // Creamos la imagen de destino
        Imagen res = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        int kAlto = kernel.length;
        int kAncho = kernel[0].length;

        // Calculamos el centro u origen del Elemento Estructurante
        int centroX = kAncho / 2;
        int centroY = kAlto / 2;

        // Recorremos cada píxel de la imagen original
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {

                boolean encajaPerfectamente = true;
                int posCentral = y * ancho + x;
                int alphaOriginal = img.getAlpha(posCentral);

                // Superponemos el Elemento Estructurante sobre la vecindad del píxel (x, y)
                for (int ky = 0; ky < kAlto; ky++) {
                    for (int kx = 0; kx < kAncho; kx++) {

                        // Si la posición del kernel está activa (es mayor a 0.5)
                        if (kernel[ky][kx] > 0.5) {

                            // Coordenada vecina en la imagen con mapeo periódico
                            int vecX = (x + (kx - centroX) + ancho) % ancho;
                            int vecY = (y + (ky - centroY) + alto) % alto;
                            int posVecino = vecY * ancho + vecX;

                            // Analizamos la intensidad en la imagen (canal azul como muestra)
                            int valorGris = img.getBlue(posVecino);

                            // Si el kernel pide un píxel de objeto (blanco), pero en la imagen hay fondo (negro < 128)
                            // entonces NO ENCAJA y el píxel debe ser erosionado.
                            if (valorGris < 128) {
                                encajaPerfectamente = false;
                                break;
                            }
                        }
                    }
                    if (!encajaPerfectamente) break;
                }

                // Asignamos el resultado final en la nueva imagen
                if (encajaPerfectamente) {
                    res.setPixel(posCentral, 255, 255, 255, alphaOriginal); // Mantiene Blanco
                } else {
                    res.setPixel(posCentral, 0, 0, 0, alphaOriginal);       // Se vuelve Negro
                }
            }
        }
        return res;
    }
    public Imagen dilatar(Imagen img, double[][] kernel) {
        int ancho = img.getAncho();
        int alto = img.getAlto();

        Imagen res = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        int kAlto = kernel.length;
        int kAncho = kernel[0].length;

        int centroX = kAncho / 2;
        int centroY = kAlto / 2;

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {

                boolean tocaObjeto = false;
                int posCentral = y * ancho + x;
                int alphaOriginal = img.getAlpha(posCentral);

                for (int ky = 0; ky < kAlto; ky++) {
                    for (int kx = 0; kx < kAncho; kx++) {

                        if (kernel[ky][kx] > 0.5) {

                            int vecX = (x + (kx - centroX) + ancho) % ancho;
                            int vecY = (y + (ky - centroY) + alto) % alto;
                            int posVecino = vecY * ancho + vecX;

                            int valorGris = img.getBlue(posVecino);

                            // Si el kernel está activo y toca al menos UN píxel blanco de la imagen (Objeto)
                            if (valorGris >= 128) {
                                tocaObjeto = true;
                                break; // Con uno que toque es suficiente para dilatar
                            }
                        }
                    }
                    if (tocaObjeto) break;
                }

                // Asignamos el resultado
                if (tocaObjeto) {
                    res.setPixel(posCentral, 255, 255, 255, alphaOriginal); // Se expande a Blanco
                } else {
                    res.setPixel(posCentral, 0, 0, 0, alphaOriginal);       // Se queda en Negro
                }
            }
        }
        return res;
    }
    public Imagen clausura(Imagen img, double[][] kernel) {
        // Paso 1: Dilatar la imagen original
        Imagen imagenDilatada = this.dilatar(img, kernel);

        // Paso 2: Erosionar el resultado de la dilatación
        Imagen resultadoFinal = this.erosionar(imagenDilatada, kernel);

        return resultadoFinal;
    }
    public Imagen esqueletizado(Imagen img, double[][] kernel) {
        int ancho = img.getAncho();
        int alto = img.getAlto();

        // 1. Crear la imagen que acumulará el esqueleto (inicializada completamente en negro/fondo)
        Imagen esqueleto = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());
        inicializarEnNegro(esqueleto);

        // 2. Hacer una copia de trabajo de la imagen original (Esta será nuestro conjunto A)
        Imagen tempA = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());
        System.arraycopy(img.getPixeles(), 0, tempA.getPixeles(), 0, img.getPixeles().length);

        int iteracion = 0;
        // El ciclo se repite hasta que la erosión sucesiva barra por completo la imagen (quede vacía)
        while (!esImagenVacia(tempA)) {

            // Paso A: Calcular la apertura de la imagen erosionada actual: (A o B)
            Imagen aperturaTemp = this.apertura(tempA, kernel);

            // Paso B: Calcular el residuo de esta etapa mediante la resta binaria: Sk(A) = A - (A o B)
            Imagen residuoSk = restarImagenesBinarias(tempA, aperturaTemp);

            // Paso C: Acumular el residuo en el esqueleto final mediante una unión lógica (OR)
            esqueleto = unirImagenesBinarias(esqueleto, residuoSk);

            // Paso D: Erosionar la imagen actual para la siguiente iteración: A = A ⊖ B
            tempA = this.erosionar(tempA, kernel);

            // Blindaje por seguridad académica (evita ciclos infinitos si el elemento estructurante está mal definido)
            iteracion++;
            if (iteracion > 500) {
                break;
            }
        }

        return esqueleto;
    }
    public Imagen apertura(Imagen img, double[][] kernel) {
        // Paso 1: Erosionar la imagen original
        Imagen imagenErosionada = this.erosionar(img, kernel);

        // Paso 2: Dilatar el resultado de la erosión
        Imagen resultadoFinal = this.dilatar(imagenErosionada, kernel);

        return resultadoFinal;
    }
    //----------------------------------------------------PRACTICA 11----------------------------------------------------
    public Imagen erosionarGris(Imagen img, double[][] kernel) {
        int ancho = img.getAncho();
        int alto = img.getAlto();
        Imagen res = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        int kAlto = kernel.length;
        int kAncho = kernel[0].length;
        int centroX = kAncho / 2;
        int centroY = kAlto / 2;

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {

                int minimo = 255; // Inicializamos con el valor más alto posible
                int posCentral = y * ancho + x;
                int alphaOriginal = img.getAlpha(posCentral);

                for (int ky = 0; ky < kAlto; ky++) {
                    for (int kx = 0; kx < kAncho; kx++) {

                        // Si la posición del elemento estructurante está activa
                        if (kernel[ky][kx] > 0.5) {
                            int vecX = (x + (kx - centroX) + ancho) % ancho;
                            int vecY = (y + (ky - centroY) + alto) % alto;
                            int posVecino = vecY * ancho + vecX;

                            // Obtenemos la intensidad gris actual (usando canal azul)
                            int valorGris = img.getBlue(posVecino);

                            // Buscamos el valor MÍNIMO en la vecindad
                            if (valorGris < minimo) {
                                minimo = valorGris;
                            }
                        }
                    }
                }
                // Asignamos el mínimo encontrado a todos los canales
                res.setPixel(posCentral, minimo, minimo, minimo, alphaOriginal);
            }
        }
        return res;
    }
    public Imagen dilatarGris(Imagen img, double[][] kernel) {
        int ancho = img.getAncho();
        int alto = img.getAlto();
        Imagen res = new Imagen(ancho, alto, img.getTipoActual(), img.getNumCanales());

        int kAlto = kernel.length;
        int kAncho = kernel[0].length;
        int centroX = kAncho / 2;
        int centroY = kAlto / 2;

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {

                int maximo = 0; // Inicializamos con el valor más bajo posible
                int posCentral = y * ancho + x;
                int alphaOriginal = img.getAlpha(posCentral);

                for (int ky = 0; ky < kAlto; ky++) {
                    for (int kx = 0; kx < kAncho; kx++) {

                        if (kernel[ky][kx] > 0.5) {
                            int vecX = (x + (kx - centroX) + ancho) % ancho;
                            int vecY = (y + (ky - centroY) + alto) % alto;
                            int posVecino = vecY * ancho + vecX;

                            int valorGris = img.getBlue(posVecino);

                            // Buscamos el valor MÁXIMO en la vecindad
                            if (valorGris > maximo) {
                                maximo = valorGris;
                            }
                        }
                    }
                }
                res.setPixel(posCentral, maximo, maximo, maximo, alphaOriginal);
            }
        }
        return res;
    }
    public Imagen aperturaGris(Imagen img, double[][] kernel) {
        Imagen erosionada = this.erosionarGris(img, kernel);
        return this.dilatarGris(erosionada, kernel);
    }

    public Imagen clausuraGris(Imagen img, double[][] kernel) {
        Imagen dilatada = this.dilatarGris(img, kernel);
        return this.erosionarGris(dilatada, kernel);
    }
    //Metodos auxiliares
    private void inicializarEnNegro(Imagen img) {
        int[] pix = img.getPixeles();
        for (int i = 0; i < pix.length; i++) {
            img.setPixel(i, 0, 0, 0, 255); // Fondo negro con Alpha opaco
        }
    }

    private boolean esImagenVacia(Imagen img) {
        int[] pix = img.getPixeles();
        for (int i = 0; i < pix.length; i++) {
            if (img.getBlue(i) >= 128) {
                return false; // Si encuentra al menos un píxel blanco, no está vacía
            }
        }
        return true; // Todos los píxeles son fondo negro
    }

    private Imagen restarImagenesBinarias(Imagen imgA, Imagen imgB) {
        int ancho = imgA.getAncho();
        int alto = imgA.getAlto();
        Imagen res = new Imagen(ancho, alto, imgA.getTipoActual(), imgA.getNumCanales());

        for (int i = 0; i < imgA.getPixeles().length; i++) {
            int valA = imgA.getBlue(i);
            int valB = imgB.getBlue(i);
            int alpha = imgA.getAlpha(i);

            // Resta binaria: píxel de A es blanco y el de B es negro
            if (valA >= 128 && valB < 128) {
                res.setPixel(i, 255, 255, 255, alpha); // Conserva blanco
            } else {
                res.setPixel(i, 0, 0, 0, alpha);       // Se vuelve negro
            }
        }
        return res;
    }

    private Imagen unirImagenesBinarias(Imagen imgA, Imagen imgB) {
        int ancho = imgA.getAncho();
        int alto = imgA.getAlto();
        Imagen res = new Imagen(ancho, alto, imgA.getTipoActual(), imgA.getNumCanales());

        for (int i = 0; i < imgA.getPixeles().length; i++) {
            int valA = imgA.getBlue(i);
            int valB = imgB.getBlue(i);
            int alpha = imgA.getAlpha(i);

            // Unión binaria (Operación OR lógica)
            if (valA >= 128 || valB >= 128) {
                res.setPixel(i, 255, 255, 255, alpha); // Si cualquiera es blanco, se queda blanco
            } else {
                res.setPixel(i, 0, 0, 0, alpha);
            }
        }
        return res;
    }
}
