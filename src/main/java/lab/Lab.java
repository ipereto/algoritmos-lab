package lab;

/** Cada lab es un experimento que se ejecuta desde {@link Main}. */
public interface Lab {

    String id();

    String titulo();

    void ejecutar(String[] args);
}
