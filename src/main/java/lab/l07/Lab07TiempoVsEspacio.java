package lab.l07;

import java.util.HashMap;
import java.util.Map;

import lab.Lab;
import lab.util.Contador;
import lab.util.Medir;
import lab.util.Tabla;

/**
 * Lab 07 · Cambiar memoria por tiempo.
 *
 * A. Two Sum: O(n²) sin memoria extra vs O(n) con un HashMap.
 * B. Fibonacci: recursión ingenua O(2^n) vs memoización O(n).
 */
public class Lab07TiempoVsEspacio implements Lab {

    @Override
    public String id() {
        return "07";
    }

    @Override
    public String titulo() {
        return "Tiempo vs espacio: Two Sum y Fibonacci con memoización";
    }

    @Override
    public void ejecutar(String[] args) {
        Medir.titulo("Lab 07 · " + titulo());

        Medir.seccion("A. Two Sum: ¿hay dos números que sumen el objetivo? (peor caso: no los hay)");
        Tabla a = new Tabla("n", "ops anidado", "memoria extra", "ops HashMap", "memoria extra");
        for (int n = 1_000; n <= 16_000; n *= 2) {
            int[] pares = new int[n];
            for (int i = 0; i < n; i++) {
                pares[i] = i * 2;
            }
            int objetivoImpar = 7_777; // la suma de dos pares nunca es impar
            Contador c1 = new Contador();
            Contador c2 = new Contador();
            int[] r1 = sumaDeDosAnidado(pares, objetivoImpar, c1);
            Map<Integer, Integer> vistos = new HashMap<>();
            int[] r2 = sumaDeDosHashMap(pares, objetivoImpar, c2, vistos);
            if (r1 != null || r2 != null) {
                throw new IllegalStateException("No debería existir solución");
            }
            a.fila(n, c1.valor(), "O(1)", c2.valor(), String.format("%,d entradas", vistos.size()));
        }
        a.imprimir();
        Medir.nota("Respuesta modelo: 'Paso de O(n²) a O(n) en tiempo a cambio de O(n) en memoria'.");

        Medir.seccion("B. Fibonacci: ¿cuántas llamadas hace?");
        Tabla b = new Tabla("n", "fib(n)", "llamadas recursivo", "factor (+5)", "llamadas memo");
        long anterior = 0;
        for (int n = 10; n <= 35; n += 5) {
            Contador rec = new Contador();
            Contador memo = new Contador();
            long valor = fibRecursivo(n, rec);
            long valorMemo = fibMemo(n, new HashMap<>(), memo);
            if (valor != valorMemo) {
                throw new IllegalStateException("Resultados distintos");
            }
            b.fila(n, valor, rec.valor(), Medir.factor(rec.valor(), anterior), memo.valor());
            anterior = rec.valor();
        }
        b.imprimir();
        Medir.nota("Recursivo: cada +5 en n multiplica las llamadas por ~11 → crecimiento exponencial, O(2^n).");
        Medir.nota("Memo: guarda cada resultado una vez → O(n) llamadas y O(n) memoria.");
        Medir.nota("Exponencial es peor que cuadrático: fib(50) recursivo tardaría minutos; con memo, microsegundos.");
    }

    static int[] sumaDeDosAnidado(int[] nums, int objetivo, Contador c) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                c.inc();
                if (nums[i] + nums[j] == objetivo) {
                    return new int[] {i, j};
                }
            }
        }
        return null;
    }

    static int[] sumaDeDosHashMap(int[] nums, int objetivo, Contador c, Map<Integer, Integer> vistos) {
        for (int i = 0; i < nums.length; i++) {
            c.inc();
            Integer complemento = vistos.get(objetivo - nums[i]);
            if (complemento != null) {
                return new int[] {complemento, i};
            }
            vistos.put(nums[i], i); // 🔴 BREAKPOINT 07-A (observa cómo crece 'vistos')
        }
        return null;
    }

    static long fibRecursivo(int n, Contador c) {
        c.inc(); // 🔴 BREAKPOINT 07-B (condicional: n == 2; mira cuántas veces se repite fib(2))
        if (n < 2) {
            return n;
        }
        return fibRecursivo(n - 1, c) + fibRecursivo(n - 2, c);
    }

    static long fibMemo(int n, Map<Integer, Long> memo, Contador c) {
        c.inc();
        if (n < 2) {
            return n;
        }
        Long guardado = memo.get(n);
        if (guardado != null) {
            return guardado;
        }
        long valor = fibMemo(n - 1, memo, c) + fibMemo(n - 2, memo, c);
        memo.put(n, valor);
        return valor;
    }
}
