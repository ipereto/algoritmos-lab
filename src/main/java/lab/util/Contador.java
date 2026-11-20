package lab.util;

/**
 * Cuenta "operaciones elementales" (una comparación, una lectura, una llamada).
 * Big O habla de cómo crece este número cuando crece n, no de milisegundos.
 */
public final class Contador {

    private long valor;

    public void inc() {
        valor++;
    }

    public void sumar(long cantidad) {
        valor += cantidad;
    }

    public long valor() {
        return valor;
    }

    public void reiniciar() {
        valor = 0;
    }
}
