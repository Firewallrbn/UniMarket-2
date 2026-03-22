package com.unimarket.ui;

/**
 * Utilidades de color ANSI para la terminal.
 * Proporciona constantes para dar estilo profesional a la consola.
 */
public final class ColorUtils {

    // Colores de texto
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CIAN = "\u001B[36m";
    public static final String BLANCO = "\u001B[37m";

    // Estilos
    public static final String NEGRITA = "\u001B[1m";
    public static final String SUBRAYADO = "\u001B[4m";
    public static final String DIM = "\u001B[2m";

    // Fondos
    public static final String FONDO_AZUL = "\u001B[44m";
    public static final String FONDO_VERDE = "\u001B[42m";
    public static final String FONDO_ROJO = "\u001B[41m";

    // Reset
    public static final String RESET = "\u001B[0m";

    private ColorUtils() {
        // Utilidad, no instanciable
    }

    // --- Métodos helpers ---

    public static String error(String msg) {
        return NEGRITA + ROJO + " " + msg + RESET;
    }

    public static String exito(String msg) {
        return NEGRITA + VERDE + " " + msg + RESET;
    }

    public static String info(String msg) {
        return CIAN + " " + msg + RESET;
    }

    public static String advertencia(String msg) {
        return AMARILLO + " " + msg + RESET;
    }

    public static String titulo(String msg) {
        return NEGRITA + AZUL + msg + RESET;
    }

    public static String resaltado(String msg) {
        return NEGRITA + MAGENTA + msg + RESET;
    }

    public static String separador() {
        return DIM + CIAN + "═══════════════════════════════════════════════════════════════" + RESET;
    }

    public static String separadorDoble() {
        return NEGRITA + AZUL + "╔═══════════════════════════════════════════════════════════════╗" + RESET;
    }

    public static String separadorCierre() {
        return NEGRITA + AZUL + "╚═══════════════════════════════════════════════════════════════╝" + RESET;
    }
}
