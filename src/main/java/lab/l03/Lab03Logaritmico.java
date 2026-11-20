package lab.l03;

import lab.Lab;
import lab.util.Contador;
import lab.util.Medir;
import lab.util.Tabla;

/**
 * Lab 03 · O(log n): adivina el número y la búsqueda binaria.
 *
 * Cada paso descarta la mitad. Duplicar los datos cuesta UN paso más.
 */
public class Lab03Logaritmico implements Lab {

    private static final int MAX_PASOS = 100;

    @Override
    public String id() {
        return "03";
    }

    @Override
    public String titulo() {
        return "O(log n): adivina el número y búsqueda binaria";
    }

    @Override
    public void ejecutar(String[] args) {
        Medir.titulo("Lab 03 · " + titulo());

        Medir.seccion("A. Adivina el número entre 0 y n-1 (peor caso)");
        Tabla a = new Tabla("n", "pasos", "log2(n)");
        for (long n = 10; n <= 1_000_000_000L; n *= 10) {
            Contador c = new Contador();
            adivinar((int) (n - 1), 0, (int) (n - 1), c, false);
            a.fila(n, c.valor(), Medir.log2Techo(n));
        }
        a.imprimir();
        Medir.nota("Mil millones de opciones y bastan 30 preguntas de 'mayor o menor'.");

        Medir.seccion("B. Buscar en un arreglo ordenado: lineal vs binaria (valor que no existe)");
        Tabla b = new Tabla("n", "ops lineal", "factor", "ops binaria", "diferencia");
        long antLineal = 0;
        long antBinaria = 0;
        for (int n = 1 << 14; n <= 1 << 22; n <<= 2) {
            int[] ordenado = new int[n];
            for (int i = 0; i < n; i++) {
                ordenado[i] = i * 2; // solo pares
            }
            Contador cl = new Contador();
            Contador cb = new Contador();
            busquedaLineal(ordenado, -1, cl);
            busquedaBinaria(ordenado, 7, cb); // 7 es impar: no existe
            b.fila(n, cl.valor(), Medir.factor(cl.valor(), antLineal),
                    cb.valor(), Medir.diferencia(cb.valor(), antBinaria));
            antLineal = cl.valor();
            antBinaria = cb.valor();
        }
        b.imprimir();
        Medir.nota("Cada fila multiplica n por 4: la lineal hace x4 trabajo, la binaria solo +2 pasos.");

        Medir.seccion("C. El bug clásico: calcular el medio con (lo + hi) / 2");
        int secreto = 2_000_000_000;
        Contador seguro = new Contador();
        Contador ingenuo = new Contador();
        String rSeguro = adivinar(secreto, 0, Integer.MAX_VALUE - 1, seguro, false);
        String rIngenuo = adivinar(secreto, 0, Integer.MAX_VALUE - 1, ingenuo, true);
        new Tabla("forma de calcular el medio", "resultado", "pasos")
                .fila("lo + (hi - lo) / 2", rSeguro, seguro.valor())
                .fila("(lo + hi) / 2", rIngenuo, ingenuo.valor())
                .imprimir();
        Medir.nota("lo + hi supera Integer.MAX_VALUE, da negativo y la búsqueda se pierde.");
        Medir.nota("Este bug estuvo años en el JDK (Java Development Kit, kit de desarrollo de Java). Hoy Arrays.binarySearch usa (low + high) >>> 1.");

        Medir.seccion("D. Requisito: los datos DEBEN estar ordenados");
        int[] desordenado = {5, 1, 9, 3, 7};
        int pos = busquedaBinaria(desordenado, 3, new Contador());
        System.out.println("  Buscar 3 en {5, 1, 9, 3, 7} con binaria → posición " + pos
                + (pos < 0 ? "  (¡no lo encontró, aunque está en la posición 3!)" : ""));
        Medir.nota("En una entrevista, menciona siempre el requisito y su costo: ordenar es O(n log n) (lab 04).");
    }

    static int busquedaLineal(int[] a, int x, Contador c) {
        for (int i = 0; i < a.length; i++) {
            c.inc();
            if (a[i] == x) {
                return i;
            }
        }
        return -1;
    }

    static int busquedaBinaria(int[] a, int x, Contador c) {
        int lo = 0;
        int hi = a.length - 1;
        while (lo <= hi) {
            c.inc();
            int medio = lo + (hi - lo) / 2; // 🔴 BREAKPOINT 03-A (observa lo, hi y medio en cada vuelta)
            if (a[medio] == x) {
                return medio;
            }
            if (a[medio] < x) {
                lo = medio + 1;
            } else {
                hi = medio - 1;
            }
        }
        return -1;
    }

    /** Juego "adivina el número" sobre el rango [lo, hi]. */
    static String adivinar(int secreto, int lo, int hi, Contador c, boolean medioIngenuo) {
        while (lo <= hi) {
            c.inc();
            if (c.valor() > MAX_PASOS) {
                return "💥 se perdió (más de " + MAX_PASOS + " pasos)";
            }
            int medio = medioIngenuo ? (lo + hi) / 2 : lo + (hi - lo) / 2; // 🔴 BREAKPOINT 03-B
            if (medio == secreto) {
                return "encontrado";
            }
            if (medio < secreto) {
                lo = medio + 1;
            } else {
                hi = medio - 1;
            }
        }
        return "no encontrado";
    }
}
