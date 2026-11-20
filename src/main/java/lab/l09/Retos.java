package lab.l09;

/**
 * ✍️ TU ARCHIVO. Implementa cada método y corre {@code ./run.sh retos}.
 *
 * Para cada reto, antes de programar, escribe en el comentario:
 *   - la solución ingenua y su complejidad;
 *   - la solución que vas a implementar, su complejidad en TIEMPO y en ESPACIO.
 * Así practicas exactamente lo que vas a decir en voz alta en la entrevista.
 *
 * El verificador revisa que la respuesta sea correcta y que aguante datos grandes
 * dentro de un límite de tiempo: una solución O(n²) no pasa la prueba de rendimiento.
 * Soluciones de referencia: docs/SOLUCIONES.md (¡después de intentarlo!).
 */
public final class Retos {

    private Retos() {
    }

    /**
     * Reto 1 · ¿El arreglo tiene algún valor repetido?
     * Objetivo: O(n) tiempo.
     * Ingenua: ...  | Mía: ...
     */
    public static boolean tieneDuplicados(int[] nums) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Reto 2 · Two Sum: devuelve los índices {i, j} (i < j) de dos números que suman
     * el objetivo, o null si no existen.
     * Objetivo: O(n) tiempo.
     * Ingenua: ...  | Mía: ...
     */
    public static int[] sumaDeDos(int[] nums, int objetivo) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Reto 3 · Índice del primer carácter que aparece una sola vez en el texto, o -1.
     * Ejemplos: "programa" → 0 ('p'), "aabbcdd" → 4 ('c'), "aabb" → -1.
     * Objetivo: O(n) tiempo.
     * Ingenua: ...  | Mía: ...
     */
    public static int primerNoRepetido(String texto) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Reto 4 · Buscar en un arreglo ordenado y ROTADO (sin repetidos).
     * Ejemplo: {40, 50, 60, 10, 20, 30}, buscar 20 → 4. Si no está → -1.
     * Objetivo: O(log n) tiempo.
     * Ingenua: ...  | Mía: ...
     */
    public static int buscarEnRotado(int[] rotado, int buscado) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Reto 5 · ¿Son anagramas? ("roma", "amor" → true). Solo letras minúsculas a-z.
     * Objetivo: O(n) tiempo (O(n log n) también pasa; ¿sabes explicar la diferencia?).
     * Ingenua: ...  | Mía: ...
     */
    public static boolean sonAnagramas(String a, String b) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Reto 6 · El valor que más se repite. Si hay empate, cualquiera de los empatados.
     * Objetivo: O(n) tiempo.
     * Ingenua: ...  | Mía: ...
     */
    public static int masFrecuente(int[] nums) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Reto 7 · Raíz cuadrada entera (piso) de x, SIN usar Math.sqrt.
     * Ejemplo: 17 → 4, 1_000_000_000_000_000_000L → 1_000_000_000.
     * Objetivo: O(log x). Cuidado con el desbordamiento al calcular medio * medio.
     * Ingenua: ...  | Mía: ...
     */
    public static long raizEntera(long x) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Reto 8 · Fusionar dos arreglos ya ordenados en uno ordenado.
     * Objetivo: O(n + m) tiempo, sin volver a ordenar.
     * Ingenua: ...  | Mía: ...
     */
    public static int[] fusionarOrdenados(int[] a, int[] b) {
        throw new UnsupportedOperationException("TODO");
    }
}
