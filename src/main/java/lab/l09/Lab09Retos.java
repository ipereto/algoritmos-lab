package lab.l09;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lab.Lab;
import lab.util.Medir;

/**
 * Lab 09 · Retos tipo entrevista. Tú escribes {@link Retos}; este verificador
 * revisa corrección y rendimiento.
 */
public class Lab09Retos implements Lab {

    private static final long LIMITE_MS = 3_000;

    @FunctionalInterface
    interface Prueba {
        void ejecutar() throws Exception;
    }

    record Reto(int numero, String nombre, String objetivo, Prueba correccion, Prueba rendimiento) {
    }

    @Override
    public String id() {
        return "09";
    }

    @Override
    public String titulo() {
        return "Retos de entrevista (tú implementas, el verificador evalúa)";
    }

    @Override
    public void ejecutar(String[] args) {
        Medir.titulo("Lab 09 · " + titulo());
        Integer soloUno = args.length > 0 ? Integer.valueOf(args[0]) : null;

        int aprobados = 0;
        int total = 0;
        for (Reto reto : retos()) {
            if (soloUno != null && reto.numero() != soloUno) {
                continue;
            }
            total++;
            String resultado = evaluar(reto);
            if (resultado.startsWith("✅")) {
                aprobados++;
            }
            System.out.printf("  %d. %-28s %-14s %s%n", reto.numero(), reto.nombre(), reto.objetivo(), resultado);
        }
        System.out.printf("%n  Aprobados: %d de %d%n", aprobados, total);
        Medir.nota("🐢 = correcto pero lento: probablemente hay un bucle anidado o un contains() escondido.");
        Medir.nota("Si uno sale 🐢, puede seguir ocupando CPU y afectar a los siguientes: corre './run.sh retos N'.");
    }

    private String evaluar(Reto reto) {
        try {
            reto.correccion().ejecutar();
        } catch (UnsupportedOperationException e) {
            return "⏳ pendiente";
        } catch (AssertionError e) {
            return "❌ incorrecto: " + e.getMessage();
        } catch (Exception | StackOverflowError e) {
            return "💥 " + e.getClass().getSimpleName() + ": " + e.getMessage();
        }

        ExecutorService ejecutor = Executors.newSingleThreadExecutor(r -> {
            Thread hilo = new Thread(r, "reto-" + reto.numero());
            hilo.setDaemon(true);
            return hilo;
        });
        try {
            long inicio = System.nanoTime();
            Future<?> futuro = ejecutor.submit(() -> {
                reto.rendimiento().ejecutar();
                return null;
            });
            futuro.get(LIMITE_MS, TimeUnit.MILLISECONDS);
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            return "✅ (" + ms + " ms con datos grandes)";
        } catch (TimeoutException e) {
            return "🐢 correcto, pero no terminó en " + LIMITE_MS / 1000 + " s con datos grandes";
        } catch (ExecutionException e) {
            Throwable causa = e.getCause();
            if (causa instanceof AssertionError) {
                return "❌ incorrecto con datos grandes: " + causa.getMessage();
            }
            return "💥 " + causa.getClass().getSimpleName() + " con datos grandes: " + causa.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "interrumpido";
        } finally {
            ejecutor.shutdownNow();
        }
    }

    // ---------------------------------------------------------------- pruebas

    private static List<Reto> retos() {
        List<Reto> lista = new ArrayList<>();

        lista.add(new Reto(1, "tieneDuplicados", "O(n)", () -> {
            igual(false, Retos.tieneDuplicados(new int[] {}), "{}");
            igual(false, Retos.tieneDuplicados(new int[] {1, 2, 3}), "{1,2,3}");
            igual(true, Retos.tieneDuplicados(new int[] {1, 2, 1}), "{1,2,1}");
            igual(true, Retos.tieneDuplicados(new int[] {-5, 0, 7, -5}), "{-5,0,7,-5}");
        }, () -> {
            int[] grande = rango(300_000);
            igual(false, Retos.tieneDuplicados(grande), "300.000 distintos");
        }));

        lista.add(new Reto(2, "sumaDeDos", "O(n)", () -> {
            igualArr(new int[] {0, 1}, Retos.sumaDeDos(new int[] {2, 7, 11, 15}, 9), "{2,7,11,15} → 9");
            igualArr(new int[] {1, 2}, Retos.sumaDeDos(new int[] {3, 2, 4}, 6), "{3,2,4} → 6");
            igualArr(new int[] {0, 1}, Retos.sumaDeDos(new int[] {3, 3}, 6), "{3,3} → 6");
            igualArr(null, Retos.sumaDeDos(new int[] {1, 2, 3}, 100), "{1,2,3} → 100 (no existe)");
        }, () -> {
            int n = 300_000;
            int[] grande = rango(n);
            igualArr(new int[] {n - 2, n - 1}, Retos.sumaDeDos(grande, (n - 2) + (n - 1)), "los dos últimos");
        }));

        lista.add(new Reto(3, "primerNoRepetido", "O(n)", () -> {
            igual(0, Retos.primerNoRepetido("programa"), "\"programa\"");
            igual(4, Retos.primerNoRepetido("aabbcdd"), "\"aabbcdd\"");
            igual(-1, Retos.primerNoRepetido("aabb"), "\"aabb\"");
            igual(-1, Retos.primerNoRepetido(""), "\"\"");
        }, () -> {
            String grande = "ab".repeat(150_000) + "z";
            igual(300_000, Retos.primerNoRepetido(grande), "300.000 repetidos y una 'z' al final");
        }));

        lista.add(new Reto(4, "buscarEnRotado", "O(log n)", () -> {
            int[] r = {40, 50, 60, 10, 20, 30};
            igual(4, Retos.buscarEnRotado(r, 20), "20");
            igual(0, Retos.buscarEnRotado(r, 40), "40");
            igual(2, Retos.buscarEnRotado(r, 60), "60");
            igual(-1, Retos.buscarEnRotado(r, 35), "35 (no está)");
            igual(0, Retos.buscarEnRotado(new int[] {7}, 7), "{7}");
            igual(1, Retos.buscarEnRotado(new int[] {1, 3}, 3), "{1,3} sin rotar");
            igual(-1, Retos.buscarEnRotado(new int[] {}, 3), "{}");
        }, () -> {
            int n = 1_000_000;
            int corte = 654_321;
            int[] rotado = new int[n];
            for (int i = 0; i < n; i++) {
                rotado[i] = ((i + corte) % n) * 2;
            }
            for (int i = 0; i < n; i++) {
                int esperado = (i - corte + n) % n;
                int obtenido = Retos.buscarEnRotado(rotado, i * 2);
                if (obtenido != esperado) {
                    throw new AssertionError("buscar " + (i * 2) + ": esperaba " + esperado + ", obtuve " + obtenido);
                }
            }
        }));

        lista.add(new Reto(5, "sonAnagramas", "O(n)", () -> {
            igual(true, Retos.sonAnagramas("roma", "amor"), "roma/amor");
            igual(false, Retos.sonAnagramas("roma", "rama"), "roma/rama");
            igual(false, Retos.sonAnagramas("abc", "ab"), "abc/ab");
            igual(true, Retos.sonAnagramas("", ""), "vacíos");
            igual(false, Retos.sonAnagramas("aab", "abb"), "aab/abb");
        }, () -> {
            String a = "abcdefghij".repeat(200_000);
            String b = new StringBuilder(a).reverse().toString();
            igual(true, Retos.sonAnagramas(a, b), "2.000.000 letras");
        }));

        lista.add(new Reto(6, "masFrecuente", "O(n)", () -> {
            igual(3, Retos.masFrecuente(new int[] {1, 3, 2, 3, 3, 1}), "{1,3,2,3,3,1}");
            igual(-7, Retos.masFrecuente(new int[] {-7}), "{-7}");
            igual(5, Retos.masFrecuente(new int[] {5, 5, 9}), "{5,5,9}");
        }, () -> {
            int n = 300_000;
            int[] grande = rango(n);
            grande[n - 1] = 123;
            igual(123, Retos.masFrecuente(grande), "300.000 valores, 123 aparece dos veces");
        }));

        lista.add(new Reto(7, "raizEntera", "O(log x)", () -> {
            igualL(0, Retos.raizEntera(0), "0");
            igualL(1, Retos.raizEntera(1), "1");
            igualL(4, Retos.raizEntera(17), "17");
            igualL(5, Retos.raizEntera(25), "25");
            igualL(1_000_000_000L, Retos.raizEntera(1_000_000_000_000_000_000L), "10^18");
            igualL(3_037_000_499L, Retos.raizEntera(Long.MAX_VALUE), "Long.MAX_VALUE");
        }, () -> {
            for (long i = 0; i < 200_000; i++) {
                long x = Long.MAX_VALUE - i * 1_000_003L;
                igualL(raizReferencia(x), Retos.raizEntera(x), "x = " + x);
            }
        }));

        lista.add(new Reto(8, "fusionarOrdenados", "O(n + m)", () -> {
            igualArr(new int[] {1, 2, 3, 4, 5}, Retos.fusionarOrdenados(new int[] {1, 3, 5}, new int[] {2, 4}), "{1,3,5}+{2,4}");
            igualArr(new int[] {1, 2}, Retos.fusionarOrdenados(new int[] {}, new int[] {1, 2}), "{}+{1,2}");
            igualArr(new int[] {1, 1, 2, 2}, Retos.fusionarOrdenados(new int[] {1, 2}, new int[] {1, 2}), "{1,2}+{1,2}");
        }, () -> {
            int n = 2_000_000;
            int[] pares = new int[n];
            int[] impares = new int[n];
            for (int i = 0; i < n; i++) {
                pares[i] = i * 2;
                impares[i] = i * 2 + 1;
            }
            int[] r = Retos.fusionarOrdenados(pares, impares);
            if (r.length != 2 * n || r[0] != 0 || r[2 * n - 1] != 2 * n - 1 || r[n] != n) {
                throw new AssertionError("resultado mal fusionado");
            }
        }));

        return lista;
    }

    // ---------------------------------------------------------------- helpers

    /** Referencia para validar: Math.sqrt con ajuste fino (el reto es NO usarlo). */
    private static long raizReferencia(long x) {
        long r = (long) Math.sqrt((double) x);
        while (r > 0 && r > x / r) {
            r--;
        }
        while ((r + 1) <= x / (r + 1)) {
            r++;
        }
        return r;
    }

    private static int[] rango(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }
        return a;
    }

    private static void igual(Object esperado, Object obtenido, String caso) {
        if (!esperado.equals(obtenido)) {
            throw new AssertionError(caso + ": esperaba " + esperado + ", obtuve " + obtenido);
        }
    }

    private static void igualL(long esperado, long obtenido, String caso) {
        if (esperado != obtenido) {
            throw new AssertionError(caso + ": esperaba " + esperado + ", obtuve " + obtenido);
        }
    }

    private static void igualArr(int[] esperado, int[] obtenido, String caso) {
        if (!Arrays.equals(esperado, obtenido)) {
            throw new AssertionError(caso + ": esperaba " + Arrays.toString(esperado)
                    + ", obtuve " + Arrays.toString(obtenido));
        }
    }
}
