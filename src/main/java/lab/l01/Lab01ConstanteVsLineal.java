package lab.l01;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lab.Lab;
import lab.util.Contador;
import lab.util.Medir;
import lab.util.Tabla;

/**
 * Lab 01 · O(1) vs O(n): la fila de personas vs el casillero numerado.
 *
 * ArrayList.contains() recorre elemento por elemento.
 * HashSet.contains() calcula el hash y va directo al "casillero" (bucket).
 */
public class Lab01ConstanteVsLineal implements Lab {

    private static final int BUSQUEDAS = 200;

    @Override
    public String id() {
        return "01";
    }

    @Override
    public String titulo() {
        return "O(1) vs O(n): ArrayList.contains vs HashSet.contains";
    }

    @Override
    public void ejecutar(String[] args) {
        Medir.titulo("Lab 01 · " + titulo());
        Medir.seccion("Buscar un valor que NO existe (peor caso), " + BUSQUEDAS + " veces");

        Tabla t = new Tabla("n", "ops lineal (1 búsqueda)", "factor",
                "ms ArrayList.contains", "ms HashSet.contains");
        long anterior = 0;
        for (int n = 10_000; n <= 160_000; n *= 2) {
            List<Integer> lista = new ArrayList<>(n);
            Set<Integer> conjunto = new HashSet<>(n * 2);
            for (int i = 0; i < n; i++) {
                lista.add(i);
                conjunto.add(i);
            }

            Contador c = new Contador();
            buscarLineal(lista, -1, c);

            double msLista = Medir.msMejorDe(3, () -> {
                long encontrados = 0;
                for (int i = 1; i <= BUSQUEDAS; i++) {
                    if (lista.contains(-i)) {
                        encontrados++;
                    }
                }
                return encontrados;
            });
            double msSet = Medir.msMejorDe(3, () -> {
                long encontrados = 0;
                for (int i = 1; i <= BUSQUEDAS; i++) {
                    if (conjunto.contains(-i)) { // 🔴 BREAKPOINT 01-B (Step Into → HashMap.getNode)
                        encontrados++;
                    }
                }
                return encontrados;
            });

            t.fila(n, c.valor(), Medir.factor(c.valor(), anterior), msLista, msSet);
            anterior = c.valor();
        }
        t.imprimir();
        Medir.nota("ArrayList: duplicas n → duplicas el trabajo. Es O(n).");
        Medir.nota("HashSet: el tiempo casi no cambia con n. Es O(1) en promedio.");
        Medir.nota("En el debugger, entra a HashSet.contains → HashMap.getNode y busca '(n - 1) & hash':");
        Medir.nota("esa línea calcula el casillero directamente, sin recorrer nada.");
    }

    /** Lo mismo que hace ArrayList.contains por dentro, pero contando pasos. */
    static boolean buscarLineal(List<Integer> lista, int buscado, Contador c) {
        for (Integer valor : lista) {
            c.inc(); // 🔴 BREAKPOINT 01-A
            if (valor == buscado) {
                return true;
            }
        }
        return false;
    }
}
