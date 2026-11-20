package lab.util;

import java.util.ArrayList;
import java.util.List;

/** Tabla de texto alineada para comparar resultados en consola. */
public final class Tabla {

    private final String[] columnas;
    private final List<String[]> filas = new ArrayList<>();

    public Tabla(String... columnas) {
        this.columnas = columnas;
    }

    public Tabla fila(Object... valores) {
        if (valores.length != columnas.length) {
            throw new IllegalArgumentException("Se esperaban " + columnas.length + " valores");
        }
        String[] fila = new String[valores.length];
        for (int i = 0; i < valores.length; i++) {
            fila[i] = formatear(valores[i]);
        }
        filas.add(fila);
        return this;
    }

    private static String formatear(Object v) {
        if (v instanceof Integer || v instanceof Long) {
            return String.format("%,d", ((Number) v).longValue());
        }
        if (v instanceof Double d) {
            return String.format("%,.3f", d);
        }
        return String.valueOf(v);
    }

    public void imprimir() {
        int[] anchos = new int[columnas.length];
        for (int i = 0; i < columnas.length; i++) {
            anchos[i] = columnas[i].length();
            for (String[] f : filas) {
                anchos[i] = Math.max(anchos[i], f[i].length());
            }
        }
        StringBuilder sb = new StringBuilder();
        linea(sb, columnas, anchos);
        for (int i = 0; i < columnas.length; i++) {
            sb.append("  ").append("─".repeat(anchos[i]));
        }
        sb.append('\n');
        for (String[] f : filas) {
            linea(sb, f, anchos);
        }
        System.out.print(sb);
    }

    private static void linea(StringBuilder sb, String[] celdas, int[] anchos) {
        for (int i = 0; i < celdas.length; i++) {
            sb.append("  ").append(" ".repeat(anchos[i] - celdas[i].length())).append(celdas[i]);
        }
        sb.append('\n');
    }
}
