package cl.grupobios.fichatecnica.command;

public interface Command<T> {
    T execute();
}
