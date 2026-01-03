package cl.grupobios.fichatecnica.utils;

import java.security.SecureRandom;
import java.util.Random;

public class GeneratorCodigo {
    private static final String CARACTERES = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0123456789";
    private static final Random RANDOM = new SecureRandom();
    private static final int LONGITUD_CODIGO = 20;

    public static String generarCodigoAleatorio() {
        StringBuilder codigo = new StringBuilder(LONGITUD_CODIGO);

        for (int i = 0; i < LONGITUD_CODIGO; i++) {
            int index = RANDOM.nextInt(CARACTERES.length());
            codigo.append(CARACTERES.charAt(index));
        }

        return codigo.toString();
    }
}
