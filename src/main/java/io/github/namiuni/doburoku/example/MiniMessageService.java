package io.github.namiuni.doburoku.example;

import io.github.namiuni.doburoku.annotation.Locales;
import io.github.namiuni.doburoku.annotation.annotations.Key;
import io.github.namiuni.doburoku.annotation.annotations.ResourceBundle;
import io.github.namiuni.doburoku.annotation.annotations.Value;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.slf4j.event.Level;

@NullMarked
@ResourceBundle(baseName = "messages")
public interface MiniMessageService {

    @Key("example.load.translations")
    @Value(locale = Locales.EN_US, content = "Loaded <count> translations: <locales>")
    @Value(locale = Locales.JA_JP, content = "翻訳を<count>件読み込みました: <locales>")
    Consumer<Level> loadTranslations(int count, List<Locale> locales);

    @Key("example.join.message")
    @Value(locale = Locales.EN_US, content = "Welcome! <player_name>!! Current Time: <localized_time>")
    @Value(locale = Locales.JA_JP, content = "ようこそ! <player_name>!! 現在時刻: <localized_time>")
    PlayerMessage joinMessage(LocalizedTime localizedTime);

    @Key("example.join.broadcast")
    @Value(locale = Locales.EN_US, content = "<moonshiner> came to brew doburoku! Total <total_moonshiners> moonshiners!!")
    @Value(locale = Locales.JA_JP, content = "<moonshiner>がどぶろくを醸造しに来た! 密造者は合計で<total_moonshiners>人だ!!")
    Component joinBroadcast(Player moonshiner, int totalMoonshiners);

    @Key("example.brew.broadcast")
    @Value(locale = Locales.EN_US, content = "Doburoku was brewed at <location>!")
    @Value(locale = Locales.JA_JP, content = "どぶろくが<location>で醸造された!")
    int brewBroadcast(Location location);
}
