# Soluciones de referencia de los retos (lab 09)

> ⚠️ Intenta cada reto primero. La idea es que llegues aquí a comparar, no a copiar.

Todas pasan el verificador (`./run.sh retos` → 8 de 8). Para probarlas, copia el método a `Retos.java` y agrega los imports de `java.util` que haga falta.

---

## 1. tieneDuplicados

**Ingenua:** Comparar cada par con dos bucles: O(n²) tiempo, O(1) espacio.

**Idea:** Un `HashSet` de vistos. `add` devuelve `false` si el valor ya estaba.

**Complejidad:** O(n) tiempo, O(n) espacio.

```java
public static boolean tieneDuplicados(int[] nums) {
    Set<Integer> vistos = new HashSet<>();
    for (int x : nums) {
        if (!vistos.add(x)) {
            return true;
        }
    }
    return false;
}
```

**Para la entrevista:** Alternativa sin memoria extra: ordenar y comparar vecinos, O(n log n) tiempo y O(1) espacio adicional (si puedes modificar el arreglo).

---

## 2. sumaDeDos (Two Sum)

**Ingenua:** Probar cada par: O(n²).

**Idea:** Para cada número, pregunta si su complemento (`objetivo - x`) ya apareció. Guarda valor → índice en un `HashMap`.

**Complejidad:** O(n) tiempo, O(n) espacio.

```java
public static int[] sumaDeDos(int[] nums, int objetivo) {
    Map<Integer, Integer> indicePorValor = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        Integer j = indicePorValor.get(objetivo - nums[i]);
        if (j != null) {
            return new int[] {j, i};
        }
        indicePorValor.put(nums[i], i);
    }
    return null;
}
```

**Para la entrevista:** Si el arreglo ya viniera ordenado, dos punteros (uno al inicio, otro al final) lo resuelven en O(n) con O(1) espacio.

---

## 3. primerNoRepetido

**Ingenua:** Para cada carácter, contar sus apariciones recorriendo el texto: O(n²).

**Idea:** Dos pasadas: la primera cuenta, la segunda devuelve el primero con conteo 1.

**Complejidad:** O(n) tiempo, O(k) espacio, donde k es el número de caracteres distintos.

```java
public static int primerNoRepetido(String texto) {
    Map<Character, Integer> conteo = new HashMap<>();
    for (int i = 0; i < texto.length(); i++) {
        conteo.merge(texto.charAt(i), 1, Integer::sum);
    }
    for (int i = 0; i < texto.length(); i++) {
        if (conteo.get(texto.charAt(i)) == 1) {
            return i;
        }
    }
    return -1;
}
```

**Para la entrevista:** Si el alfabeto es fijo (por ejemplo ASCII), un `int[128]` en lugar del `HashMap` es más rápido y el espacio es O(1).

---

## 4. buscarEnRotado

**Ingenua:** Búsqueda lineal: O(n).

**Idea:** Búsqueda binaria modificada. En cada paso, **una de las dos mitades está ordenada** (`a[lo] <= a[medio]` dice cuál). Si el buscado cae en el rango de la mitad ordenada, vas ahí; si no, a la otra.

**Complejidad:** O(log n) tiempo, O(1) espacio.

```java
public static int buscarEnRotado(int[] a, int x) {
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) {
        int medio = lo + (hi - lo) / 2;
        if (a[medio] == x) {
            return medio;
        }
        if (a[lo] <= a[medio]) {
            if (a[lo] <= x && x < a[medio]) {
                hi = medio - 1;
            } else {
                lo = medio + 1;
            }
        } else {
            if (a[medio] < x && x <= a[hi]) {
                lo = medio + 1;
            } else {
                hi = medio - 1;
            }
        }
    }
    return -1;
}
```

**Para la entrevista:** Es una de las preguntas favoritas porque obliga a entender *por qué* funciona la búsqueda binaria, no solo a memorizarla.

---

## 5. sonAnagramas

**Ingenua:** Ordenar ambos textos y compararlos: O(n log n).

**Idea:** Un arreglo de 26 contadores: suma por cada letra de `a`, resta por cada letra de `b`; todos deben terminar en 0.

**Complejidad:** O(n) tiempo, O(1) espacio (el arreglo de 26 no crece con n).

```java
public static boolean sonAnagramas(String a, String b) {
    if (a.length() != b.length()) {
        return false;
    }
    int[] conteo = new int[26];
    for (int i = 0; i < a.length(); i++) {
        conteo[a.charAt(i) - 'a']++;
        conteo[b.charAt(i) - 'a']--;
    }
    for (int c : conteo) {
        if (c != 0) {
            return false;
        }
    }
    return true;
}
```

**Para la entrevista:** Con Unicode arbitrario, usa un `HashMap<Character, Integer>` (O(k) espacio).

---

## 6. masFrecuente

**Ingenua:** Para cada valor, contar cuántas veces aparece: O(n²).

**Idea:** Un `HashMap` de conteos, actualizando el mejor sobre la marcha con `merge`.

**Complejidad:** O(n) tiempo, O(n) espacio.

```java
public static int masFrecuente(int[] nums) {
    Map<Integer, Integer> conteo = new HashMap<>();
    int mejor = nums[0];
    int mejorConteo = 0;
    for (int x : nums) {
        int c = conteo.merge(x, 1, Integer::sum);
        if (c > mejorConteo) {
            mejorConteo = c;
            mejor = x;
        }
    }
    return mejor;
}
```

**Para la entrevista:** Si te piden el que aparece **más de n/2 veces** (elemento mayoritario), existe el algoritmo de votación de Boyer-Moore: O(n) tiempo y O(1) espacio.

---

## 7. raizEntera

**Ingenua:** Probar 0, 1, 2, … hasta pasarse: O(√x), que para 10¹⁸ son mil millones de pasos.

**Idea:** Búsqueda binaria sobre la **respuesta**: buscas el mayor `m` con `m² ≤ x`. Para no desbordar `long`, compara `m <= x / m` en lugar de `m * m <= x`, y limita `hi` a 3.037.000.499 (la raíz de `Long.MAX_VALUE`).

**Complejidad:** O(log x) tiempo, O(1) espacio.

```java
public static long raizEntera(long x) {
    long lo = 0;
    long hi = Math.min(x, 3_037_000_499L);
    long respuesta = 0;
    while (lo <= hi) {
        long medio = lo + (hi - lo) / 2;
        if (medio == 0 || medio <= x / medio) {
            respuesta = medio;
            lo = medio + 1;
        } else {
            hi = medio - 1;
        }
    }
    return respuesta;
}
```

**Para la entrevista:** "Búsqueda binaria sobre la respuesta" es un patrón que aparece mucho: capacidad mínima de un camión, velocidad mínima para terminar a tiempo, etc.

---

## 8. fusionarOrdenados

**Ingenua:** Concatenar y ordenar: O((n+m) log(n+m)).

**Idea:** Dos punteros, uno por arreglo: siempre copias el menor de los dos actuales. Es el paso de mezcla de merge sort (lab 04).

**Complejidad:** O(n + m) tiempo, O(n + m) espacio para el resultado.

```java
public static int[] fusionarOrdenados(int[] a, int[] b) {
    int[] r = new int[a.length + b.length];
    int i = 0;
    int j = 0;
    int k = 0;
    while (i < a.length && j < b.length) {
        r[k++] = a[i] <= b[j] ? a[i++] : b[j++];
    }
    while (i < a.length) {
        r[k++] = a[i++];
    }
    while (j < b.length) {
        r[k++] = b[j++];
    }
    return r;
}
```

**Para la entrevista:** Usar `<=` (y no `<`) hace que la mezcla sea **estable**: ante empates conserva primero los de `a`.
