package lab.l04;

import java.util.Arrays;
import java.util.Random;

import lab.Lab;
import lab.util.Contador;
import lab.util.Medir;
import lab.util.Tabla;

/**
 * Lab 04 · O(n log n): ordenar.
 *
 * Burbuja compara todos contra todos (O(n²)). Merge sort parte el mazo a la mitad
 * (log n niveles) y en cada nivel mezcla n cartas: O(n log n).
 */
public class Lab04Ordenamiento implements Lab {

    @Override
    public String id() {
        return "04";
    }

    @Override
    public String titulo() {
        return "O(n log n): burbuja vs merge sort, y cuándo vale la pena ordenar";
    }

    @Override
    public void ejecutar(String[] args) {
        Medir.titulo("Lab 04 · " + titulo());

        Medir.seccion("A. Comparaciones al ordenar");
        Tabla a = new Tabla("n", "comp. burbuja", "factor", "comp. merge sort", "factor",
                "ms burbuja", "ms merge sort", "ms Arrays.sort");
        long antBurbuja = 0;
        long antMerge = 0;
        for (int n = 1_000; n <= 16_000; n *= 2) {
            int[] original = aleatorio(n);
            int[] esperado = original.clone();
            Arrays.sort(esperado);

            int[] x1 = original.clone();
            int[] x2 = original.clone();
            Contador cb = new Contador();
            Contador cm = new Contador();
            double msBurbuja = Medir.ms(() -> burbuja(x1, cb));
            double msMerge = Medir.ms(() -> mergeSort(x2, cm));
            double msJdk = Medir.msMejorDe(3, () -> {
                int[] x3 = original.clone();
                Arrays.sort(x3);
                return x3[0];
            });
            if (!Arrays.equals(x1, esperado) || !Arrays.equals(x2, esperado)) {
                throw new IllegalStateException("Algún algoritmo no ordenó bien");
            }
            a.fila(n, cb.valor(), Medir.factor(cb.valor(), antBurbuja),
                    cm.valor(), Medir.factor(cm.valor(), antMerge),
                    msBurbuja, msMerge, msJdk);
            antBurbuja = cb.valor();
            antMerge = cm.valor();
        }
        a.imprimir();
        Medir.nota("Burbuja: x4 al duplicar n → O(n²). Merge sort: algo más de x2 → O(n log n).");
        Medir.nota("Arrays.sort para int[] usa Dual-Pivot Quicksort; para objetos, List.sort usa TimSort. Ambos O(n log n).");

        Medir.seccion("B. ¿Ordenar primero o buscar lineal? (n = 100.000, ops estimadas)");
        int n = 100_000;
        int log = Medir.log2Techo(n);
        Tabla b = new Tabla("búsquedas (k)", "k · n (lineal)", "n·log n + k·log n (ordenar + binaria)", "gana");
        for (int k : new int[] {1, 5, 10, 17, 50, 1_000, 100_000}) {
            long lineal = (long) k * n;
            long ordenar = (long) n * log + (long) k * log;
            b.fila(k, lineal, ordenar, lineal <= ordenar ? "lineal" : "ordenar");
        }
        b.imprimir();
        Medir.nota("Si buscas pocas veces, ordenar no compensa. A partir de ~log n búsquedas (≈17), sí.");
        Medir.nota("Esa es la respuesta que buscan en entrevista: depende de cuántas veces vas a buscar.");
    }

    static void burbuja(int[] a, Contador c) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = 0; j < a.length - 1 - i; j++) {
                c.inc();
                if (a[j] > a[j + 1]) {
                    int tmp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = tmp;
                }
            }
        }
    }

    static void mergeSort(int[] a, Contador c) {
        mergeSort(a, new int[a.length], 0, a.length - 1, c);
    }

    private static void mergeSort(int[] a, int[] tmp, int lo, int hi, Contador c) {
        if (lo >= hi) {
            return;
        }
        int medio = lo + (hi - lo) / 2; // 🔴 BREAKPOINT 04-A (mira la pila de llamadas: profundidad ≈ log n)
        mergeSort(a, tmp, lo, medio, c);
        mergeSort(a, tmp, medio + 1, hi, c);
        mezclar(a, tmp, lo, medio, hi, c);
    }

    private static void mezclar(int[] a, int[] tmp, int lo, int medio, int hi, Contador c) {
        int i = lo;
        int j = medio + 1;
        int k = lo;
        while (i <= medio && j <= hi) {
            c.inc(); // 🔴 BREAKPOINT 04-B (condicional: hi - lo == 3)
            tmp[k++] = a[i] <= a[j] ? a[i++] : a[j++];
        }
        while (i <= medio) {
            tmp[k++] = a[i++];
        }
        while (j <= hi) {
            tmp[k++] = a[j++];
        }
        System.arraycopy(tmp, lo, a, lo, hi - lo + 1);
    }

    static int[] aleatorio(int n) {
        Random r = new Random(7);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = r.nextInt(1_000_000);
        }
        return a;
    }
}
