package cl.grupobios.fichatecnica.exceptions;

public class ConcurrencyException extends RuntimeException{
    public ConcurrencyException(String message) {
        super(message);
    }
}
