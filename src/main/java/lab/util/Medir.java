package lab.util;

import java.util.function.LongSupplier;

/** Utilidades para medir tiempo e imprimir resultados. */
public final class Medir {

    /**
     * El JIT (Just-In-Time, compilación justo a tiempo) puede borrar código cuyo
     * resultado nadie usa. Guardar el resultado aquí evita ese "truco".
     */
    public static volatile long sumidero;

    private Medir() {
    }

    /** Ejecuta una vez y devuelve los milisegundos. */
    public static double ms(Runnable tarea) {
        long inicio = System.nanoTime();
        tarea.run();
        return (System.nanoTime() - inicio) / 1_000_000.0;
    }

    /** Ejecuta varias veces y devuelve el MEJOR tiempo (el menos afectado por ruido). */
    public static double msMejorDe(int repeticiones, LongSupplier tarea) {
        double mejor = Double.MAX_VALUE;
        for (int i = 0; i < repeticiones; i++) {
            long inicio = System.nanoTime();
            sumidero += tarea.getAsLong();
            double ms = (System.nanoTime() - inicio) / 1_000_000.0;
            mejor = Math.min(mejor, ms);
        }
        return mejor;
    }

    /** "x2.0", "x4.1"...: cuánto creció respecto a la fila anterior. */
    public static String factor(long actual, long anterior) {
        if (anterior <= 0) {
            return "-";
        }
        return String.format("x%.1f", (double) actual / anterior);
    }

    /** "+1", "+2"...: para algoritmos logarítmicos, la diferencia dice más que el factor. */
    public static String diferencia(long actual, long anterior) {
        if (anterior <= 0) {
            return "-";
        }
        long d = actual - anterior;
        return (d >= 0 ? "+" : "") + d;
    }

    public static int log2Techo(long n) {
        if (n <= 1) {
            return 0;
        }
        return 64 - Long.numberOfLeadingZeros(n - 1);
    }

    public static void titulo(String texto) {
        String linea = "═".repeat(Math.max(20, texto.length() + 4));
        System.out.println();
        System.out.println(linea);
        System.out.println("  " + texto);
        System.out.println(linea);
    }

    public static void seccion(String texto) {
        System.out.println();
        System.out.println("── " + texto + " ──");
    }

    public static void nota(String texto) {
        System.out.println("  💡 " + texto);
    }
}
