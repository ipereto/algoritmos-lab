package lab.l05;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import lab.Lab;
import lab.util.Contador;
import lab.util.Medir;
import lab.util.Tabla;

/**
 * Lab 05 · Las colecciones de Java bajo la lupa.
 *
 * A. LinkedList.get(i) dentro de un for: O(n²) accidental.
 * B. ArrayList.add: O(1) "amortizado" (a veces copia todo, en promedio no).
 * C. TreeMap vs HashMap: O(log n) a cambio de mantener el orden.
 */
public class Lab05ColeccionesJava implements Lab {

    @Override
    public String id() {
        return "05";
    }

    @Override
    public String titulo() {
        return "Colecciones Java: LinkedList, ArrayList amortizado, TreeMap vs HashMap";
    }

    @Override
    public void ejecutar(String[] args) {
        Medir.titulo("Lab 05 · " + titulo());
        parteA();
        parteB();
        parteC();
    }

    private void parteA() {
        Medir.seccion("A. Recorrer con get(i) vs con for-each");
        Tabla t = new Tabla("n", "ms ArrayList get(i)", "ms LinkedList for-each", "ms LinkedList get(i)", "factor");
        double anterior = 0;
        for (int n = 5_000; n <= 40_000; n *= 2) {
            List<Integer> arreglo = new ArrayList<>();
            List<Integer> enlazada = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                arreglo.add(i);
                enlazada.add(i);
            }
            double msArray = Medir.msMejorDe(3, () -> sumarConIndice(arreglo));
            double msForEach = Medir.msMejorDe(3, () -> sumarForEach(enlazada));
            double msGet = Medir.msMejorDe(1, () -> sumarConIndice(enlazada));
            t.fila(n, msArray, msForEach, msGet,
                    anterior == 0 ? "-" : String.format("x%.1f", msGet / anterior));
            anterior = msGet;
        }
        t.imprimir();
        Medir.nota("LinkedList.get(i) camina nodo por nodo hasta i: es O(n). Dentro de un for → O(n²).");
        Medir.nota("Con for-each usa el iterador, que avanza de a un nodo: O(n) en total.");
        Medir.nota("Los ms son ruidosos; fíjate en la tendencia de la última columna (≈ x4).");
    }

    private void parteB() {
        Medir.seccion("B. ArrayList.add: ¿cuántas copias hace al crecer?");
        Tabla t = new Tabla("elementos agregados", "redimensionamientos", "elementos copiados", "copias por elemento");
        for (int n = 1_000; n <= 1_000_000; n *= 10) {
            ArregloDinamico arreglo = new ArregloDinamico();
            for (int i = 0; i < n; i++) {
                arreglo.agregar(i);
            }
            t.fila(n, arreglo.redimensionamientos.valor(), arreglo.copias.valor(),
                    String.format("%.2f", (double) arreglo.copias.valor() / n));
        }
        t.imprimir();
        Medir.nota("Copiar todo es O(n), pero pasa cada vez menos (crece x1,5 como ArrayList).");
        Medir.nota("En promedio cada add cuesta ~2 copias, sin importar n: eso es O(1) amortizado.");
    }

    private void parteC() {
        Medir.seccion("C. TreeMap: más lento que HashMap, pero ordenado");
        TreeMap<Integer, Integer> tarifas = new TreeMap<>(Map.of(
                0, 5_000, 1_000, 8_000, 5_000, 15_000, 20_000, 30_000));
        int peso = 3_200;
        Map.Entry<Integer, Integer> rango = tarifas.floorEntry(peso); // 🔴 BREAKPOINT 05-C (Step Into → TreeMap.getFloorEntry)
        System.out.printf("  Envío de %,d g → tarifa desde %,d g = $%,d (floorEntry, O(log n))%n",
                peso, rango.getKey(), rango.getValue());
        Medir.nota("Con HashMap esta pregunta ('el rango que contiene X') exigiría recorrer todo: O(n).");

        Tabla t = new Tabla("n", "ms n × HashMap.get", "ms n × TreeMap.get");
        for (int n = 50_000; n <= 800_000; n *= 4) {
            Map<Integer, Integer> hash = new HashMap<>();
            Map<Integer, Integer> arbol = new TreeMap<>();
            int[] claves = new Random(3).ints(n, 0, Integer.MAX_VALUE).toArray();
            for (int k : claves) {
                hash.put(k, k);
                arbol.put(k, k);
            }
            int[] consultas = Arrays.copyOf(claves, n);
            double msHash = Medir.msMejorDe(3, () -> consultar(hash, consultas));
            double msArbol = Medir.msMejorDe(3, () -> consultar(arbol, consultas));
            t.fila(n, msHash, msArbol);
        }
        t.imprimir();
        Medir.nota("HashMap: O(1) promedio. TreeMap (árbol rojo-negro): O(log n), pero te da orden,");
        Medir.nota("rangos (subMap, floorKey, ceilingKey) y primer/último elemento.");
    }

    private static long sumarConIndice(List<Integer> lista) {
        long suma = 0;
        for (int i = 0; i < lista.size(); i++) {
            suma += lista.get(i); // 🔴 BREAKPOINT 05-A (con LinkedList: Step Into → LinkedList.node)
        }
        return suma;
    }

    private static long sumarForEach(List<Integer> lista) {
        long suma = 0;
        for (int v : lista) {
            suma += v;
        }
        return suma;
    }

    private static long consultar(Map<Integer, Integer> mapa, int[] claves) {
        long suma = 0;
        for (int k : claves) {
            suma += mapa.get(k);
        }
        return suma;
    }

    /** Versión simplificada de lo que hace ArrayList por dentro. */
    static final class ArregloDinamico {
        private int[] datos = new int[10];
        private int tamano;
        final Contador redimensionamientos = new Contador();
        final Contador copias = new Contador();

        void agregar(int valor) {
            if (tamano == datos.length) {
                int nuevaCapacidad = datos.length + (datos.length >> 1); // x1,5 como ArrayList.grow
                redimensionamientos.inc();
                copias.sumar(tamano); // 🔴 BREAKPOINT 05-B (¿cada cuánto entra aquí?)
                datos = Arrays.copyOf(datos, nuevaCapacidad);
            }
            datos[tamano++] = valor;
        }
    }
}
