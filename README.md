# Algoritmos Lab · Complejidad algorítmica (Big O) en Java

Labs prácticos para **entender en caliente** qué significa O(1), O(log n), O(n), O(n log n), O(n²) y O(2ⁿ), por qué importa en código de producción y cómo explicarlo en una entrevista.

- **Java 21 puro, sin dependencias.** No necesita Maven ni internet para compilar: `./run.sh` usa `javac` directamente.
- **Cuenta operaciones, no solo milisegundos.** Big O describe cómo crece el trabajo, y las tablas lo muestran con una columna `factor` (x2, x4…) o `diferencia` (+1, +2…).
- **Mismo método que los labs de Kafka y Quarkus:** predecir → ejecutar → depurar con breakpoints → comparar → romper algo.

## Cómo correrlo

| Dónde | Cómo |
|---|---|
| **GitHub Codespaces** (recomendado: no usa tu computador del trabajo) | Botón **Code → Codespaces → Create codespace**. El devcontainer trae Java 21 y corre `./run.sh` al abrir. |
| Local con Java 21 | `./run.sh` |
| IDE (IntelliJ IDEA o VS Code) | Abre la carpeta (hay un `pom.xml` mínimo) y ejecuta `lab.Main` con el número del lab como argumento. |

```bash
./run.sh            # menú
./run.sh 02         # un lab
./run.sh all        # labs 00 a 08 (~15 s)
./run.sh retos      # tus retos de entrevista (lab 09)
./run.sh retos 4    # solo el reto 4
```

## Chuleta: cómo reconocer cada complejidad

| Big O | Al duplicar n… | Patrón en el código | Analogía | Ejemplo en Java |
|---|---|---|---|---|
| O(1) | igual | acceso directo por clave o índice | casillero numerado | `HashMap.get`, `array[i]` |
| O(log n) | +1 paso | partir a la mitad en cada paso | adivina el número / diccionario | búsqueda binaria, `TreeMap.get`, índice B-tree |
| O(n) | x2 | un bucle sobre los datos | buscar a alguien en una fila | `ArrayList.contains`, recorrer una lista |
| O(n log n) | algo más de x2 | dividir + mezclar | ordenar un mazo partiéndolo | `Arrays.sort`, `List.sort` |
| O(n²) | x4 | bucle dentro de bucle sobre los mismos datos | todos se saludan con todos | burbuja, `list.contains()` dentro de un `for` |
| O(2ⁿ) | se eleva al cuadrado | recursión que se ramifica sin memoria | rumor que cada uno cuenta a dos | Fibonacci recursivo ingenuo |

## El método (para cada lab)

1. **Predice.** Antes de correrlo, responde las preguntas del lab abajo. Escríbelas, aunque sea en una línea.
2. **Ejecuta.** `./run.sh NN` y mira las columnas `factor` / `diferencia`.
3. **Depura.** Pon breakpoints en las líneas marcadas `// 🔴 BREAKPOINT NN-X`. En bucles grandes usa **breakpoints condicionales** (clic derecho sobre el punto rojo → *Condition*), o el debugger se detendrá millones de veces.
4. **Compara** tu predicción con el resultado (respuestas en los `<details>`).
5. **Rompe algo.** Haz el cambio propuesto y explica por qué cambió la tabla.
6. **Dilo en voz alta.** Cada lab cierra con la pregunta de entrevista que entrena. Tienes las respuestas modelo en [`docs/ENTREVISTA.md`](docs/ENTREVISTA.md).

---

## Lab 00 · Medir: operaciones vs milisegundos

**Concepto.** Los milisegundos dependen de la máquina, del JIT (Just-In-Time, compilación justo a tiempo) y del GC (Garbage Collector, recolector de basura). Big O cuenta **cuántas operaciones** hace el algoritmo y cómo crecen con n.

**Predice.**
1. Si corres 8 veces la misma suma de 2 millones de números, ¿los ms serán iguales?
2. `sumarTresVeces` hace el triple de trabajo. ¿Es O(3n)?

**Depura.** `00-A` con condición `x == datos[3]`.

<details><summary>Compara</summary>

1. No: las primeras corridas son más lentas porque la JVM (Java Virtual Machine, máquina virtual de Java) interpreta el código hasta que el JIT lo compila. Las ops sí son idénticas.
2. Es O(n). Las constantes se ignoran: lo que importa es que al duplicar n el trabajo se duplica.
</details>

**Rompe algo.** Cambia la parte A para medir con `n = 20_000` en vez de 2 millones. ¿Los ms siguen siendo útiles para comparar? (Pista: el ruido se come la señal; por eso los benchmarks serios usan JMH (Java Microbenchmark Harness, arnés de microbenchmarks de Java)).

**Entrevista:** *"¿Qué es Big O y por qué ignora las constantes?"*

---

## Lab 01 · O(1) vs O(n): `ArrayList.contains` vs `HashSet.contains`

**Concepto.** La lista recorre elemento por elemento (fila de personas). El `HashSet` calcula el hash de la clave y salta al casillero (bucket) correcto.

**Predice.** Con 160.000 elementos, ¿cuántas veces más lento será `ArrayList.contains` que con 10.000? ¿Y `HashSet.contains`?

**Depura.**
- `01-A`: mira el contador avanzar.
- `01-B`: haz **Step Into** hasta `HashMap.getNode` y busca la expresión `(n - 1) & hash`. Ahí está el O(1): una cuenta, no un recorrido.

<details><summary>Compara</summary>

ArrayList: ~16 veces más lento (n creció x16). HashSet: prácticamente igual.
</details>

**Rompe algo.** Busca un valor que **sí existe** y está al principio de la lista (`lista.contains(0)`). ¿Qué le pasa al tiempo de ArrayList? Por eso Big O suele hablar del **peor caso**.

**Entrevista:** *"¿Cuál es la complejidad de `contains` en una List y en un Set?"*

---

## Lab 02 · El O(n²) escondido: cruzar dos listas de clientes

**Concepto.** El caso más común en código real: *"¿qué clientes de A también están en B?"*. Tres versiones: bucles anidados, un solo `for` con `list.contains()` y `HashSet`.

**Predice.**
1. Con 16.000 clientes, ¿cuántas comparaciones hace la versión anidada?
2. La versión con `list.contains()` tiene **un solo** `for`. ¿Es O(n)?

**Depura.**
- `02-A`: ¿cuántas veces pasa por aquí por cada cliente de A?
- `02-B`: Step Into en `contains` → `ArrayList.indexOf` → `indexOfRange`. Ahí está el bucle escondido.
- `02-C`: construcción del set.

<details><summary>Compara</summary>

1. 256 millones (16.000²).
2. No: `contains` recorre la lista, así que es O(n·m), igual de cuadrática. Es más rápida que la anidada solo por la constante (compara `String`s directamente), no por la forma de la curva.
</details>

**Rompe algo.** Convierte `Cliente` de `record` a una `class` normal **sin** `equals` ni `hashCode`, y haz que la versión HashSet guarde `Cliente` en lugar del `id`. ¿Cuántos encuentra ahora? ¿Por qué?

**Entrevista:** *"Este proceso tardaba 1 s en QA (Quality Assurance, aseguramiento de calidad) y 3 horas en producción. ¿Qué buscarías?"*

---

## Lab 03 · O(log n): adivina el número y búsqueda binaria

**Concepto.** Cada paso descarta la mitad de los datos. Duplicar n cuesta **un** paso más.

**Predice.**
1. ¿Cuántas preguntas de "mayor o menor" necesitas para adivinar un número entre 0 y mil millones?
2. Si el arreglo pasa de 1 millón a 4 millones, ¿cuántos pasos más da la búsqueda binaria?

**Depura.** `03-A`: agrega `lo`, `hi` y `medio` a *Watches* y mira cómo el rango se parte a la mitad. `03-B`: compara las dos formas de calcular el medio en la parte C.

<details><summary>Compara</summary>

1. 30 (2³⁰ ≈ mil millones).
2. 2 pasos más (4 = 2²).
</details>

**Rompe algo.** La parte C ya muestra el bug de `(lo + hi) / 2` con números grandes: desborda `int`, da negativo y la búsqueda se pierde. Pon un breakpoint condicional en `03-B` con `medio < 0` para verlo. La parte D muestra qué pasa si el arreglo no está ordenado.

**Entrevista:** *"¿Por qué la búsqueda binaria es O(log n) y qué requisito tiene?"*

---

## Lab 04 · O(n log n): burbuja vs merge sort

**Concepto.** Burbuja compara todos contra todos: O(n²). Merge sort parte el arreglo a la mitad (log n niveles) y en cada nivel mezcla n elementos: O(n log n). Es lo mejor que se puede lograr ordenando por comparaciones.

**Predice.**
1. Con 16.000 elementos, ¿cuántas veces más comparaciones hace burbuja que merge sort?
2. Tienes 100.000 datos desordenados y vas a buscar **5 veces**. ¿Conviene ordenar primero?

**Depura.** `04-A`: mira la pestaña *Frames* (pila de llamadas). ¿Qué tan profunda llega? `04-B` con condición `hi - lo == 3`: mira cómo mezcla dos mitades ya ordenadas.

<details><summary>Compara</summary>

1. ~630 veces más (128 millones vs 203 mil).
2. No. Ordenar cuesta ~1,7 millones de ops; 5 búsquedas lineales, 500 mil. Ordenar empieza a compensar a partir de ~log n búsquedas (≈17).
</details>

**Rompe algo.** Pasa a burbuja un arreglo **ya ordenado**. Hace las mismas comparaciones. Agrega una bandera `huboIntercambio` que corte el bucle si una pasada no intercambió nada. ¿Cuál es ahora el mejor caso? ¿Y el peor?

**Entrevista:** *"¿Cuál es la complejidad de ordenar? ¿Siempre conviene ordenar antes de buscar?"*

---

## Lab 05 · Colecciones Java bajo la lupa

**Concepto.**
- A. `LinkedList.get(i)` camina nodo por nodo: dentro de un `for` es O(n²) accidental.
- B. `ArrayList.add` es O(1) **amortizado**: a veces copia todo el arreglo, pero tan pocas veces que en promedio cuesta lo mismo.
- C. `TreeMap` es O(log n) en lugar de O(1), pero mantiene el orden y responde preguntas de rango (`floorEntry`, `subMap`).

**Predice.**
1. Recorrer una `LinkedList` de 40.000 con `get(i)`: ¿cuánto más lento que con for-each?
2. Al agregar 1 millón de elementos, ¿cuántas veces se redimensiona el arreglo interno?

**Depura.** `05-A` con LinkedList: Step Into hasta `LinkedList.node(int)` y mira el bucle. `05-B`: ¿cada cuánto entra? `05-C`: Step Into en `floorEntry`.

<details><summary>Compara</summary>

1. Miles de veces más lento, y x4 cada vez que n se duplica.
2. Solo 29 veces, con ~2,4 copias por elemento en promedio, sin importar n.
</details>

**Rompe algo.** En `ArregloDinamico`, cambia el crecimiento a `datos.length + 10` (crecer de a 10 en vez de x1,5). Vuelve a correr la parte B: ¿qué pasa con las "copias por elemento"? Acabas de convertir `add` en O(n).

**Entrevista:** *"¿ArrayList o LinkedList? ¿Qué significa O(1) amortizado?"*

---

## Lab 06 · Colisiones en HashMap: cuando O(1) deja de serlo

**Concepto.** `HashMap` es O(1) **en promedio**, si `hashCode()` reparte bien las claves. Si todas devuelven el mismo hash, caen en el mismo bucket. Desde Java 8, un bucket con 8 o más colisiones se convierte en árbol (*treeify*): si la clave es `Comparable` baja a O(log n); si no lo es, el árbol no sabe hacia dónde buscar y queda en O(n).

**Predice.** Con 16.000 claves con `hashCode()` constante y sin `Comparable`, ¿cuántas comparaciones hará?

**Depura.** `06-A`: mira la pila de llamadas: `HashMap.putVal` → `TreeNode.putTreeVal` → `find`. Busca también `treeifyBin` en `HashMap`.

<details><summary>Compara</summary>

Unos 256 millones: cada operación revisa casi todo el bucket, O(n) por operación y O(n²) en total.
</details>

**Rompe algo.** En `ClaveMala`, cambia `hashCode()` a `valor % 10`. ¿Mejora? ¿Cuánto? ¿Y con `valor % 1000`?

**Entrevista:** *"¿HashMap siempre es O(1)? ¿Qué pasa si `hashCode()` está mal implementado?"*

---

## Lab 07 · Tiempo vs espacio

**Concepto.** El truco más usado en entrevistas: **gastar memoria para ahorrar tiempo**.
- Two Sum: bucles anidados O(n²) con memoria O(1) vs un `HashMap` de "ya vistos" O(n) con memoria O(n).
- Fibonacci: la recursión ingenua recalcula lo mismo millones de veces, O(2ⁿ). Con memoización cada valor se calcula una vez, O(n).

**Predice.** ¿Cuántas llamadas hace `fibRecursivo(35)`? ¿Y `fibMemo(35)`?

**Depura.** `07-A`: observa crecer el mapa `vistos`. `07-B` con condición `n == 2`: cuenta cuántas veces se detiene para `fib(30)` antes de rendirte.

<details><summary>Compara</summary>

Casi 30 millones vs 69. Cada +5 en n multiplica las llamadas recursivas por ~11.
</details>

**Rompe algo.** Escribe `fibIterativo` con dos variables y un `for`: O(n) en tiempo y **O(1)** en memoria. Ahora tienes tres soluciones con tres perfiles distintos para contar en una entrevista.

**Entrevista:** *"¿Puedes mejorar tu solución? ¿Qué sacrificas?"*

---

## Lab 08 · Big O en producción: problema N+1 e índices

**Concepto.** Lo que viste con listas pasa igual con la base de datos:
- **Sin índice**, `WHERE id = ?` es un full scan: O(n). **Con índice** (un B-tree; aquí simulado con un arreglo ordenado y búsqueda binaria), O(log n).
- **N+1**: una consulta por cada elemento de una lista. Si además no hay índice, es el O(n²) del lab 02, pero en SQL (Structured Query Language, lenguaje de consulta estructurado) y con latencia de red en cada vuelta.

**Predice.** Ordena de peor a mejor: N+1 sin índice, N+1 con índice, una consulta `IN (...)` sin índice, una consulta `IN (...)` con índice.

**Depura.** `08-A`: en N+1, ¿cuántas veces pasa por aquí? `08-B`: es la búsqueda binaria del lab 03.

<details><summary>Compara</summary>

N+1 sin índice (~7 s) → N+1 con índice (~2 s, casi todo red) → `IN` sin índice (~6 ms) → `IN` con índice (~4 ms). El índice ataca las filas leídas; el lote ataca los viajes de red. En el mundo real necesitas los dos.
</details>

**Rompe algo.** Sube `totalClientes` a 500.000. ¿Qué estrategia empeora más? ¿Cuál casi no cambia?

**Entrevista:** *"Esta consulta es lenta, ¿qué harías?"* y *"¿Qué es el problema N+1 en JPA (Java Persistence API, API de persistencia de Java)?"*

---

## Lab 09 · Retos de entrevista

Tú escribes [`Retos.java`](src/main/java/lab/l09/Retos.java); el verificador revisa **corrección** con casos pequeños y **rendimiento** con datos grandes (límite de 3 s). Una solución O(n²) es correcta, pero sale 🐢.

| # | Reto | Objetivo |
|---|---|---|
| 1 | ¿Tiene duplicados? | O(n) |
| 2 | Two Sum (índices) | O(n) |
| 3 | Primer carácter no repetido | O(n) |
| 4 | Buscar en arreglo ordenado y rotado | O(log n) |
| 5 | ¿Son anagramas? | O(n) |
| 6 | Valor más frecuente | O(n) |
| 7 | Raíz cuadrada entera sin `Math.sqrt` | O(log x) |
| 8 | Fusionar dos arreglos ordenados | O(n + m) |

**Cómo practicarlo como entrevista real:**
1. Pon un temporizador de 20 minutos por reto.
2. **Antes de programar**, escribe en el comentario la solución ingenua y su complejidad, y luego la que vas a implementar (tiempo **y** espacio).
3. Implementa la ingenua primero si te bloqueas: `./run.sh retos N` te dirá 🐢, y eso también es aprendizaje.
4. Optimiza hasta el ✅.
5. Compara con [`docs/SOLUCIONES.md`](docs/SOLUCIONES.md) **solo después**.

Leyenda: ⏳ pendiente · ❌ incorrecto · 🐢 correcto pero lento · ✅ aprobado.

---

## Estructura

```
src/main/java/lab/
├── Main.java                 menú y ejecución
├── util/                     Contador, Medir, Tabla
├── l00 … l08/                un paquete por lab
└── l09/
    ├── Retos.java            ✍️ tu archivo
    └── Lab09Retos.java       verificador
docs/
├── ENTREVISTA.md             preguntas típicas y respuestas modelo
└── SOLUCIONES.md             soluciones de los retos
```

La CI (Continuous Integration, integración continua) en GitHub Actions compila y corre todos los labs en cada push y pull request.
