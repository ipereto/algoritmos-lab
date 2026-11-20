package lab.l06;

import java.util.HashMap;
import java.util.Map;

import lab.Lab;
import lab.util.Contador;
import lab.util.Medir;
import lab.util.Tabla;

/**
 * Lab 06 · ¿HashMap siempre es O(1)? No: depende de hashCode().
 *
 * Si todas las claves devuelven el mismo hash, todas caen en el mismo bucket.
 * Desde Java 8, un bucket con muchas colisiones se convierte en árbol (treeify):
 *  - si la clave es Comparable, la búsqueda baja a O(log n);
 *  - si no lo es, el árbol no sabe hacia dónde ir y termina en O(n).
 */
public class Lab06ColisionesHashMap implements Lab {

    /** Cuenta las llamadas a equals() y compareTo() de todas las claves. */
    static final Contador COMPARACIONES = new Contador();

    /** hashCode bien implementado. */
    static final class ClaveBuena {
        final int valor;

        ClaveBuena(int valor) {
            this.valor = valor;
        }

        @Override
        public boolean equals(Object o) {
            COMPARACIONES.inc();
            return o instanceof ClaveBuena c && c.valor == valor;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(valor);
        }
    }

    /** hashCode constante: TODAS colisionan. Pero es Comparable. */
    static final class ClaveMalaComparable implements Comparable<ClaveMalaComparable> {
        final int valor;

        ClaveMalaComparable(int valor) {
            this.valor = valor;
        }

        @Override
        public boolean equals(Object o) {
            COMPARACIONES.inc();
            return o instanceof ClaveMalaComparable c && c.valor == valor;
        }

        @Override
        public int hashCode() {
            return 42;
        }

        @Override
        public int compareTo(ClaveMalaComparable otra) {
            COMPARACIONES.inc();
            return Integer.compare(valor, otra.valor);
        }
    }

    /** hashCode constante y NO Comparable: el peor caso. */
    static final class ClaveMala {
        final int valor;

        ClaveMala(int valor) {
            this.valor = valor;
        }

        @Override
        public boolean equals(Object o) {
            COMPARACIONES.inc(); // 🔴 BREAKPOINT 06-A (mira la pila: HashMap.putVal → TreeNode.find)
            return o instanceof ClaveMala c && c.valor == valor;
        }

        @Override
        public int hashCode() {
            return 42;
        }
    }

    @Override
    public String id() {
        return "06";
    }

    @Override
    public String titulo() {
        return "Colisiones en HashMap: cuando O(1) deja de serlo";
    }

    @Override
    public void ejecutar(String[] args) {
        Medir.titulo("Lab 06 · " + titulo());
        Medir.seccion("Insertar n claves y luego buscarlas todas (cuenta equals + compareTo)");

        Tabla t = new Tabla("n", "hash bueno", "factor", "hash malo + Comparable", "factor",
                "hash malo", "factor");
        long antBuena = 0;
        long antComparable = 0;
        long antMala = 0;
        for (int n = 1_000; n <= 16_000; n *= 2) {
            long buena = medir(n, ClaveBuena::new);
            long comparable = medir(n, ClaveMalaComparable::new);
            long mala = medir(n, ClaveMala::new);
            t.fila(n, buena, Medir.factor(buena, antBuena),
                    comparable, Medir.factor(comparable, antComparable),
                    mala, Medir.factor(mala, antMala));
            antBuena = buena;
            antComparable = comparable;
            antMala = mala;
        }
        t.imprimir();
        Medir.nota("Hash bueno: ~1 comparación por operación → O(1), total O(n).");
        Medir.nota("Hash malo + Comparable: el bucket se volvió árbol → O(log n) por operación.");
        Medir.nota("Hash malo sin Comparable: x4 al duplicar n → cada operación es O(n), total O(n²).");
        Medir.nota("Moraleja para entrevista: implementa siempre equals() y hashCode() juntos y bien distribuidos.");
    }

    private static <K> long medir(int n, java.util.function.IntFunction<K> fabrica) {
        Map<K, Integer> mapa = new HashMap<>();
        COMPARACIONES.reiniciar();
        for (int i = 0; i < n; i++) {
            mapa.put(fabrica.apply(i), i);
        }
        for (int i = 0; i < n; i++) {
            if (mapa.get(fabrica.apply(i)) != i) {
                throw new IllegalStateException("No encontró la clave " + i);
            }
        }
        return COMPARACIONES.valor();
    }
}
