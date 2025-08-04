package io.github.namiuni.doburoku.example;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record LocalizedTime(Locale locale) {

    public Component now() {
        final LocalTime now = LocalTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                .withLocale(locale);

        return Component.text(now.format(formatter));
    }
}
