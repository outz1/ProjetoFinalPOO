package model;

import exception.LocadoraException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Formatação e leitura de datas e valores em reais. */
public final class Formato {
    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Formato() {
    }

    public static LocalDate lerData(String texto) throws LocadoraException {
        try {
            return LocalDate.parse(texto.trim(), DATA);
        } catch (DateTimeParseException e) {
            throw new LocadoraException("Data inválida: \"" + texto + "\". Use o formato dd/mm/aaaa.");
        }
    }

    public static String data(LocalDate data) {
        return data == null ? "-" : data.format(DATA);
    }

    public static String moeda(double valor) {
        return String.format(BRASIL, "R$ %,.2f", valor);
    }

    /** Número com duas casas e vírgula, para preencher campos de entrada. */
    public static String decimal(double valor) {
        return String.format(BRASIL, "%.2f", valor);
    }

    /** Arredonda para centavos, evitando resíduos como 165.00000000000003. */
    public static double centavos(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
