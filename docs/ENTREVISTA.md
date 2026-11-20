# Big O en entrevistas: preguntas típicas y cómo responderlas

## La fórmula para cualquier respuesta

1. **Di la complejidad.** "Es O(n) en tiempo y O(n) en espacio."
2. **Justifícala con la estructura del código.** "Un solo recorrido, y dentro un `contains` sobre un HashSet, que es O(1) en promedio."
3. **Propón una mejora o explica el trade-off.** "Si la memoria fuera una restricción, puedo ordenar en sitio y usar dos punteros: O(n log n) en tiempo y O(1) en espacio."

Lo que evalúan no es el número, sino el **razonamiento**. Menciona siempre tiempo **y** espacio, aunque no te pregunten por el espacio.

---

## Preguntas y respuestas modelo

### 1. ¿Qué es la notación Big O? · Lab 00
> "Describe cómo crece el tiempo o la memoria de un algoritmo cuando crece el tamaño de la entrada. No mide segundos, mide la tendencia. Normalmente se habla del peor caso, y se ignoran las constantes porque con datos grandes lo que domina es la forma de la curva: 3n y n son ambos O(n)."

### 2. ¿Cuál es la complejidad de tu solución? · Lab 09
Recorre tu código en voz alta, bucle por bucle, y nombra el costo de cada llamada a librería:
> "Tengo un bucle sobre los n elementos y dentro hago un `put` en un HashMap, O(1) promedio. Total O(n) en tiempo. Guardo hasta n entradas, así que O(n) en espacio."

### 3. ¿Puedes mejorarla? · Labs 02 y 07
> "La versión con dos bucles es O(n²) en tiempo y O(1) en memoria. Si guardo lo que ya vi en un HashSet, bajo a O(n) en tiempo a cambio de O(n) en memoria. Si la memoria no es una restricción, esa es la mejor opción."

Si no ves una mejora, da la **cota inferior**: "No creo que se pueda bajar de O(n), porque al menos tengo que mirar cada elemento una vez."

### 4. ¿Cuál es la complejidad de `contains` en List y en Set? · Lab 01
> "En `ArrayList` es O(n), porque recorre elemento por elemento. En `HashSet` es O(1) en promedio, porque calcula el hash y va directo al bucket. En `TreeSet` es O(log n), porque es un árbol balanceado."

### 5. ¿HashMap siempre es O(1)? · Lab 06
> "Es O(1) en promedio para `get` y `put`. Si muchas claves colisionan en el mismo bucket, se degrada. Desde Java 8, cuando un bucket supera 8 colisiones se convierte en un árbol rojo-negro, y si las claves son `Comparable` el peor caso queda en O(log n) en lugar de O(n). Por eso hay que implementar `equals()` y `hashCode()` juntos y con buena distribución."

### 6. ¿ArrayList o LinkedList? · Lab 05
> "ArrayList tiene acceso por índice O(1) y `add` al final O(1) amortizado: a veces redimensiona el arreglo copiándolo, pero tan pocas veces que en promedio es constante. Insertar o borrar en el medio es O(n). LinkedList inserta en O(1) si ya tienes el nodo, pero `get(i)` es O(n), así que recorrerla con un índice es O(n²). En la práctica casi siempre gana ArrayList, también por localidad de memoria en caché."

### 7. ¿Qué significa O(1) amortizado? · Lab 05
> "Que una operación individual puede ser cara, pero el costo promedio sobre una secuencia larga es constante. En ArrayList, cuando se llena el arreglo se copia todo (O(n)), pero como crece x1,5, eso pasa cada vez menos y en promedio cada `add` cuesta unas pocas copias."

### 8. ¿Por qué la búsqueda binaria es O(log n)? ¿Qué requisito tiene? · Lab 03
> "Porque en cada paso descarta la mitad del rango: con mil millones de elementos basta con 30 pasos. El requisito es que los datos estén ordenados. Si tengo que ordenarlos primero, eso cuesta O(n log n), así que solo vale la pena si voy a buscar muchas veces. Ojo con calcular el medio como `(lo + hi) / 2`, que puede desbordar: mejor `lo + (hi - lo) / 2`."

### 9. ¿Cuál es la complejidad de ordenar? · Lab 04
> "O(n log n) con algoritmos de comparación como merge sort, que es la cota inferior para ordenar comparando. En Java, `Arrays.sort` sobre primitivos usa Dual-Pivot Quicksort, y `List.sort` sobre objetos usa TimSort, que es estable y aprovecha tramos ya ordenados. Burbuja o inserción son O(n²), aunque inserción es buena para arreglos casi ordenados o muy pequeños."

### 10. ¿Conviene ordenar antes de buscar? · Lab 04
> "Depende de cuántas búsquedas haga. Ordenar cuesta O(n log n) y luego cada búsqueda O(log n). Si busco k veces de forma lineal pago O(k·n). Ordenar compensa más o menos cuando k supera log n. Y si solo necesito pertenencia, un HashSet es O(n) para construir y O(1) por búsqueda, mejor que las dos opciones."

### 11. ¿Por qué Fibonacci recursivo es tan lento? · Lab 07
> "Porque cada llamada se ramifica en dos y recalcula los mismos subproblemas una y otra vez: es O(2ⁿ). Con memoización guardo cada resultado y baja a O(n) en tiempo y espacio. Con una versión iterativa con dos variables queda O(n) en tiempo y O(1) en espacio."

### 12. Esta consulta es lenta en producción, ¿qué harías? · Lab 08
> "Primero miro el plan de ejecución con `EXPLAIN`. Si hace un full scan, es O(n) sobre la tabla; un índice B-tree en la columna del filtro lo baja a O(log n). Después reviso si hay un problema N+1 en el ORM (Object-Relational Mapping, mapeo objeto-relacional): una consulta por cada elemento de una lista. Eso se arregla con `JOIN FETCH`, `@EntityGraph` o `@BatchSize`, para que sea una o pocas consultas en vez de N."

### 13. Funcionaba en QA y en producción tarda horas · Lab 02
> "Lo primero que busco es algo cuadrático escondido: bucles anidados sobre colecciones que crecen con los datos, o un `contains`/`indexOf`/`remove` sobre una List dentro de un `for`. Con los datos pequeños de QA (Quality Assurance, aseguramiento de calidad) no se nota, pero si los datos crecen x100 el tiempo crece x10.000. Lo confirmaría con un profiler y lo arreglaría con un HashSet o un HashMap."

---

## Errores que conviene evitar

- **Quedarse callado mientras piensas.** Razona en voz alta. Empezar con la solución ingenua y decirlo ("primero la simple, luego la optimizo") es una estrategia válida y bien vista.
- **Responder con milisegundos** cuando preguntan por complejidad.
- **Olvidar el costo de lo que llamas.** `list.contains()`, `list.remove(obj)`, `String +=` en un bucle o `substring` repetido tienen costo propio.
- **Olvidar el espacio**, sobre todo cuando cambias memoria por tiempo.
- **Inventar.** "Creo que es O(n log n) porque domina el sort; déjame verificarlo" vale más que una respuesta segura y equivocada.

## Cómo practicar

1. Haz los retos del lab 09 con temporizador y explicando en voz alta (grábate si puedes).
2. Para cada reto, ten lista la respuesta de tres partes: complejidad, justificación y trade-off.
3. Practica también en inglés: *"It's O(n) time and O(n) space, because I iterate once and store up to n entries in a hash set."*
