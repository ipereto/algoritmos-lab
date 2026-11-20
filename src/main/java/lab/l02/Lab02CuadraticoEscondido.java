package lab.l02;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lab.Lab;
import lab.util.Contador;
import lab.util.Medir;
import lab.util.Tabla;

/**
 * Lab 02 · El O(n²) escondido.
 *
 * Caso real: "¿qué clientes de la lista A también están en la lista B?".
 * Tres versiones: bucles anidados, un solo bucle con list.contains() (¡también O(n²)!)
 * y HashSet (O(n)).
 */
public class Lab02CuadraticoEscondido implements Lab {

    record Cliente(String id, String nombre) {
    }

    @Override
    public String id() {
        return "02";
    }

    @Override
    public String titulo() {
        return "El O(n²) escondido: cruzar dos listas de clientes";
    }

    @Override
    public void ejecutar(String[] args) {
        Medir.titulo("Lab 02 · " + titulo());

        Tabla t = new Tabla("n", "ops anidado", "factor", "ops HashSet", "factor",
                "ms anidado", "ms list.contains", "ms HashSet");
        long antAnidado = 0;
        long antSet = 0;
        for (int n = 1_000; n <= 16_000; n *= 2) {
            List<Cliente> a = generar(0, n);
            List<Cliente> b = generar(n / 2, n);
            List<String> idsB = b.stream().map(Cliente::id).toList();

            Contador cAnidado = new Contador();
            Contador cSet = new Contador();
            int r1 = interseccionAnidada(a, b, cAnidado).size();
            int r3 = interseccionConHashSet(a, b, cSet).size();

            double msAnidado = Medir.msMejorDe(1, () -> interseccionAnidada(a, b, new Contador()).size());
            double msContains = Medir.msMejorDe(1, () -> interseccionConContains(a, idsB).size());
            double msSet = Medir.msMejorDe(3, () -> interseccionConHashSet(a, b, new Contador()).size());

            if (r1 != r3 || r1 != n / 2) {
                throw new IllegalStateException("Las versiones no coinciden: " + r1 + " vs " + r3);
            }
            t.fila(n, cAnidado.valor(), Medir.factor(cAnidado.valor(), antAnidado),
                    cSet.valor(), Medir.factor(cSet.valor(), antSet),
                    msAnidado, msContains, msSet);
            antAnidado = cAnidado.valor();
            antSet = cSet.valor();
        }
        t.imprimir();
        Medir.nota("Anidado: duplicas n → x4 el trabajo. Es O(n²): con 16.000 clientes ya son 256 millones de comparaciones.");
        Medir.nota("list.contains() se ve como UN bucle, pero contains recorre la lista: también es O(n²).");
        Medir.nota("HashSet: construir el set (n) + n búsquedas O(1) = 2n → O(n).");
    }

    /** Versión 1: bucles anidados. O(n·m). */
    static List<Cliente> interseccionAnidada(List<Cliente> a, List<Cliente> b, Contador c) {
        List<Cliente> resultado = new ArrayList<>();
        for (Cliente x : a) {
            for (Cliente y : b) {
                c.inc(); // 🔴 BREAKPOINT 02-A (mira cuántas veces pasa por aquí por cada x)
                if (x.id().equals(y.id())) {
                    resultado.add(x);
                }
            }
        }
        return resultado;
    }

    /** Versión 2: "un solo bucle"... que esconde otro. */
    static List<Cliente> interseccionConContains(List<Cliente> a, List<String> idsB) {
        List<Cliente> resultado = new ArrayList<>();
        for (Cliente x : a) {
            if (idsB.contains(x.id())) { // 🔴 BREAKPOINT 02-B (Step Into → ArrayList.indexOfRange)
                resultado.add(x);
            }
        }
        return resultado;
    }

    /** Versión 3: cambiar memoria por tiempo. O(n + m). */
    static List<Cliente> interseccionConHashSet(List<Cliente> a, List<Cliente> b, Contador c) {
        Set<String> idsB = new HashSet<>();
        for (Cliente y : b) {
            idsB.add(y.id()); // 🔴 BREAKPOINT 02-C
            c.inc();
        }
        List<Cliente> resultado = new ArrayList<>();
        for (Cliente x : a) {
            c.inc();
            if (idsB.contains(x.id())) {
                resultado.add(x);
            }
        }
        return resultado;
    }

    static List<Cliente> generar(int desde, int cantidad) {
        List<Cliente> lista = new ArrayList<>(cantidad);
        for (int i = desde; i < desde + cantidad; i++) {
            lista.add(new Cliente("C" + i, "Cliente " + i));
        }
        return lista;
    }
}
