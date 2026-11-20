package lab.l00;

import java.util.Random;

import lab.Lab;
import lab.util.Contador;
import lab.util.Medir;
import lab.util.Tabla;

/**
 * Lab 00 · Medir: operaciones vs milisegundos.
 *
 * Idea: el tiempo en ms es ruidoso (JIT, GC, otros procesos). Contar operaciones
 * es estable y es lo que realmente describe Big O.
 */
public class Lab00Medicion implements Lab {

    @Override
    public String id() {
        return "00";
    }

    @Override
    public String titulo() {
        return "Medir: operaciones vs milisegundos";
    }

    @Override
    public void ejecutar(String[] args) {
        Medir.titulo("Lab 00 · " + titulo());

        Medir.seccion("A. El mismo trabajo, ocho veces seguidas (n = 2.000.000)");
        int[] datos = generar(2_000_000);
        Tabla tiempos = new Tabla("corrida", "ops", "ms");
        for (int corrida = 1; corrida <= 8; corrida++) {
            Contador c = new Contador();
            double ms = Medir.ms(() -> Medir.sumidero = sumar(datos, c));
            tiempos.fila(corrida, c.valor(), ms);
        }
        tiempos.imprimir();
        Medir.nota("Las ops son idénticas en las 8 corridas; los ms no. Las primeras suelen ser más lentas");
        Medir.nota("porque la JVM (Java Virtual Machine, máquina virtual de Java) aún interpreta el código");
        Medir.nota("antes de que el JIT (Just-In-Time, compilación justo a tiempo) lo compile a código nativo.");

        Medir.seccion("B. ¿Qué pasa cuando n se duplica?");
        Tabla crecimiento = new Tabla("n", "ops sumar()", "factor", "ops sumarTresVeces()", "factor");
        long anteriorA = 0;
        long anteriorB = 0;
        for (int n = 1_000; n <= 64_000; n *= 2) {
            int[] d = generar(n);
            Contador a = new Contador();
            Contador b = new Contador();
            sumar(d, a);
            sumarTresVeces(d, b);
            crecimiento.fila(n, a.valor(), Medir.factor(a.valor(), anteriorA),
                    b.valor(), Medir.factor(b.valor(), anteriorB));
            anteriorA = a.valor();
            anteriorB = b.valor();
        }
        crecimiento.imprimir();
        Medir.nota("Las dos son O(n): al duplicar n, las ops se duplican (x2.0).");
        Medir.nota("sumarTresVeces hace 3n ops, pero la forma de la curva es la misma: Big O ignora la constante 3.");
    }

    static long sumar(int[] datos, Contador c) {
        long suma = 0;
        for (int x : datos) {
            suma += x; // 🔴 BREAKPOINT 00-A (condicional: x == datos[3])
            c.inc();
        }
        return suma;
    }

    static long sumarTresVeces(int[] datos, Contador c) {
        long suma = 0;
        for (int vuelta = 0; vuelta < 3; vuelta++) {
            for (int x : datos) {
                suma += x;
                c.inc();
            }
        }
        return suma;
    }

    static int[] generar(int n) {
        Random r = new Random(42);
        int[] d = new int[n];
        for (int i = 0; i < n; i++) {
            d[i] = r.nextInt(1_000);
        }
        return d;
    }
}
