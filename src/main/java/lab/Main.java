package lab;

import java.util.Arrays;
import java.util.List;

import lab.l00.Lab00Medicion;
import lab.l01.Lab01ConstanteVsLineal;
import lab.l02.Lab02CuadraticoEscondido;
import lab.l03.Lab03Logaritmico;
import lab.l04.Lab04Ordenamiento;
import lab.l05.Lab05ColeccionesJava;
import lab.l06.Lab06ColisionesHashMap;
import lab.l07.Lab07TiempoVsEspacio;
import lab.l08.Lab08ConsultasEIndices;
import lab.l09.Lab09Retos;

/**
 * Punto de entrada.
 *
 * <pre>
 *   ./run.sh            → menú
 *   ./run.sh 02         → un lab
 *   ./run.sh all        → labs 00 a 08
 *   ./run.sh retos      → retos de entrevista (lab 09)
 *   ./run.sh retos 4    → solo el reto 4
 * </pre>
 */
public final class Main {

    private static final List<Lab> LABS = List.of(
            new Lab00Medicion(),
            new Lab01ConstanteVsLineal(),
            new Lab02CuadraticoEscondido(),
            new Lab03Logaritmico(),
            new Lab04Ordenamiento(),
            new Lab05ColeccionesJava(),
            new Lab06ColisionesHashMap(),
            new Lab07TiempoVsEspacio(),
            new Lab08ConsultasEIndices(),
            new Lab09Retos());

    private Main() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            menu();
            return;
        }
        String opcion = args[0].toLowerCase();
        String[] resto = Arrays.copyOfRange(args, 1, args.length);

        if (opcion.equals("all")) {
            LABS.stream().filter(l -> !l.id().equals("09")).forEach(l -> l.ejecutar(resto));
            return;
        }
        if (opcion.equals("retos")) {
            opcion = "09";
        }
        String id = opcion.length() == 1 ? "0" + opcion : opcion;
        LABS.stream()
                .filter(l -> l.id().equals(id))
                .findFirst()
                .ifPresentOrElse(l -> l.ejecutar(resto), () -> {
                    System.out.println("No existe el lab '" + args[0] + "'.");
                    menu();
                });
    }

    private static void menu() {
        System.out.println("Labs de complejidad algorítmica (Big O)\n");
        for (Lab l : LABS) {
            System.out.printf("  %s  %s%n", l.id(), l.titulo());
        }
        System.out.println("\nUso: ./run.sh <número> | all | retos [n]");
    }
}
