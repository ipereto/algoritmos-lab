package lab.l08;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import lab.Lab;
import lab.util.Contador;
import lab.util.Medir;
import lab.util.Tabla;

/**
 * Lab 08 · Big O en el mundo real: el problema N+1 y los índices.
 *
 * Simulamos una tabla de clientes y contamos dos cosas:
 *  - consultas: viajes a la base de datos (cada uno cuesta latencia de red);
 *  - filas leídas: trabajo dentro de la base de datos.
 * Sin índice, buscar por id es un full scan O(n). Con índice (aquí: un arreglo
 * ordenado + búsqueda binaria, como un B-tree simplificado) es O(log n).
 */
public class Lab08ConsultasEIndices implements Lab {

    record ClienteFila(int id, String nombre) {
    }

    record Pedido(int id, int clienteId) {
    }

    /** Una "tabla" de base de datos de juguete. */
    static final class TablaClientes {
        private final ClienteFila[] filas;
        private final ClienteFila[] indicePorId; // null = sin índice
        final Contador consultas = new Contador();
        final Contador filasLeidas = new Contador();

        TablaClientes(List<ClienteFila> datos, boolean conIndice) {
            this.filas = datos.toArray(ClienteFila[]::new);
            if (conIndice) {
                indicePorId = filas.clone();
                Arrays.sort(indicePorId, Comparator.comparingInt(ClienteFila::id));
            } else {
                indicePorId = null;
            }
        }

        /** SELECT * FROM clientes WHERE id = ? */
        ClienteFila buscarPorId(int id) {
            consultas.inc(); // 🔴 BREAKPOINT 08-A (en N+1, ¿cuántas veces pasa por aquí?)
            return indicePorId != null ? buscarEnIndice(id) : escanear(id);
        }

        /** SELECT * FROM clientes WHERE id IN (...) */
        List<ClienteFila> buscarPorIds(Set<Integer> ids) {
            consultas.inc();
            List<ClienteFila> resultado = new ArrayList<>();
            if (indicePorId != null) {
                for (int id : ids) {
                    resultado.add(buscarEnIndice(id));
                }
            } else {
                for (ClienteFila f : filas) { // un solo full scan para todos los ids
                    filasLeidas.inc();
                    if (ids.contains(f.id())) {
                        resultado.add(f);
                    }
                }
            }
            return resultado;
        }

        private ClienteFila escanear(int id) {
            for (ClienteFila f : filas) {
                filasLeidas.inc();
                if (f.id() == id) {
                    return f;
                }
            }
            return null;
        }

        private ClienteFila buscarEnIndice(int id) {
            int lo = 0;
            int hi = indicePorId.length - 1;
            while (lo <= hi) {
                filasLeidas.inc(); // 🔴 BREAKPOINT 08-B (es la búsqueda binaria del lab 03)
                int medio = (lo + hi) >>> 1;
                int actual = indicePorId[medio].id();
                if (actual == id) {
                    return indicePorId[medio];
                }
                if (actual < id) {
                    lo = medio + 1;
                } else {
                    hi = medio - 1;
                }
            }
            return null;
        }
    }

    @Override
    public String id() {
        return "08";
    }

    @Override
    public String titulo() {
        return "Big O en producción: problema N+1 e índices de base de datos";
    }

    @Override
    public void ejecutar(String[] args) {
        Medir.titulo("Lab 08 · " + titulo());

        int totalClientes = 50_000;
        int totalPedidos = 2_000;
        List<ClienteFila> clientes = new ArrayList<>();
        for (int i = 0; i < totalClientes; i++) {
            clientes.add(new ClienteFila(i, "Cliente " + i));
        }
        java.util.Collections.shuffle(clientes, new Random(1)); // la tabla no está ordenada físicamente
        Random r = new Random(2);
        List<Pedido> pedidos = new ArrayList<>();
        for (int i = 0; i < totalPedidos; i++) {
            pedidos.add(new Pedido(i, r.nextInt(totalClientes)));
        }

        Medir.seccion(String.format("Mostrar %,d pedidos con el nombre del cliente (tabla de %,d clientes)",
                totalPedidos, totalClientes));
        Tabla t = new Tabla("estrategia", "consultas", "filas leídas", "tiempo estimado (ms)");
        for (boolean conIndice : new boolean[] {false, true}) {
            TablaClientes tabla = new TablaClientes(clientes, conIndice);
            nMasUno(pedidos, tabla);
            t.fila("N+1 " + (conIndice ? "con índice" : "sin índice"),
                    tabla.consultas.valor(), tabla.filasLeidas.valor(), estimar(tabla));

            TablaClientes tabla2 = new TablaClientes(clientes, conIndice);
            enLote(pedidos, tabla2);
            t.fila("1 consulta IN (...) " + (conIndice ? "con índice" : "sin índice"),
                    tabla2.consultas.valor(), tabla2.filasLeidas.valor(), estimar(tabla2));
        }
        t.imprimir();
        Medir.nota("Modelo del tiempo estimado: 1 ms de red por consulta + 0,1 µs por fila leída.");
        Medir.nota("N+1 sin índice: 'un bucle de consultas' × 'un full scan cada una' = el O(n²) del lab 02, en SQL.");
        Medir.nota("El índice cambia O(n) por O(log n) dentro de la base; el lote elimina los viajes de red.");
        Medir.nota("En JPA (Java Persistence API, API de persistencia de Java) esto aparece con relaciones LAZY recorridas");
        Medir.nota("en un bucle; se arregla con JOIN FETCH, @EntityGraph o @BatchSize.");
    }

    /** Patrón N+1: una consulta por cada pedido. */
    static Map<Integer, String> nMasUno(List<Pedido> pedidos, TablaClientes tabla) {
        Map<Integer, String> nombres = new HashMap<>();
        for (Pedido p : pedidos) {
            nombres.put(p.id(), tabla.buscarPorId(p.clienteId()).nombre());
        }
        return nombres;
    }

    /** Una sola consulta con todos los ids. */
    static Map<Integer, String> enLote(List<Pedido> pedidos, TablaClientes tabla) {
        Set<Integer> ids = new LinkedHashSet<>();
        for (Pedido p : pedidos) {
            ids.add(p.clienteId());
        }
        Map<Integer, String> porId = new HashMap<>();
        for (ClienteFila c : tabla.buscarPorIds(ids)) {
            porId.put(c.id(), c.nombre());
        }
        Map<Integer, String> nombres = new HashMap<>();
        for (Pedido p : pedidos) {
            nombres.put(p.id(), porId.get(p.clienteId()));
        }
        return nombres;
    }

    private static double estimar(TablaClientes t) {
        return t.consultas.valor() * 1.0 + t.filasLeidas.valor() * 0.0001;
    }
}
