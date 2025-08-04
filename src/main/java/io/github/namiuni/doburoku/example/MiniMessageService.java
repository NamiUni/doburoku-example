package io.github.namiuni.doburoku.example;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.slf4j.event.Level;

@NullMarked
public interface MiniMessageService {

    Consumer<Level> loadTranslations(int count, List<Locale> locales);

    MessageSender joinMessage(LocalizedTime localizedTime);

    Component joinBroadcast(Player moonshiner, int totalMoonshiners);

    void brewBroadcast(Location location);
}
