package io.github.namiuni.doburoku.example;

import io.github.miniplaceholders.api.MiniPlaceholders;
import io.github.namiuni.doburoku.reflect.core.DefaultTranslatableResolver;
import io.github.namiuni.doburoku.reflect.core.DoburokuBrewery;
import io.github.namiuni.doburoku.reflect.minimessage.MiniMessageArgumentRenderer;
import io.leangen.geantyref.TypeToken;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.slf4j.event.Level;

@NullMarked
public final class DoburokuExample extends JavaPlugin {

    private @MonotonicNonNull MiniMessageService miniMessageService = null;

    @Override
    public void onLoad() {
        // Brewing minimessage service
        this.miniMessageService = DoburokuBrewery.from(MiniMessageService.class)
                .translatable(DefaultTranslatableResolver.create("example"))
                .argument(resolvers -> resolvers
                        .add(Player.class, Player::displayName)
                        .add(Location.class, location -> {
                            final String position = "X:%s, Y:%s, Z:%s".formatted(location.x(), location.y(), location.z());
                            return Component.text(position);
                        })
                        .add(LocalizedTime.class, LocalizedTime::now)
                        .add(new TypeToken<List<Locale>>() {}, locales -> {
                            final List<TextComponent> components = locales.stream()
                                    .map(Locale::toString)
                                    .map(Component::text)
                                    .toList();
                            return Component.join(JoinConfiguration.arrayLike(), components);
                        }), MiniMessageArgumentRenderer.instance()) // Transform miniMessage argument
                .ferment(handlers -> handlers
                        .add(void.class, component -> {
                            Bukkit.getServer().broadcast(component);
                            return null;
                        })
                        .add(new TypeToken<Consumer<Level>>() {}, component -> level -> {
                            final ComponentLogger logger = this.getComponentLogger();
                            switch (level) {
                                case INFO -> logger.info(component);
                                case WARN -> logger.warn(component);
                                case ERROR -> logger.error(component);
                                case TRACE -> logger.trace(component);
                                case DEBUG -> logger.debug(component);
                            }
                        })
                        .add(MessageSender.class, component -> player -> {
                            if (!this.miniPlaceholdersEnabled()) {
                                player.sendMessage(component);
                                return;
                            }

                            final TagResolver placeholders = TagResolver.builder()
                                    .resolver(MiniPlaceholders.getAudienceGlobalPlaceholders(player))
                                    .build();
                            final ComponentLike[] newArguments = Stream
                                    .concat(component.arguments().stream(), Stream.of(Argument.tagResolver(placeholders)))
                                    .toArray(ComponentLike[]::new);
                            final TranslatableComponent result = Component.translatable(component.key(), newArguments);

                            player.sendMessage(result);
                        }))
                .brew();

        // Initialize TranslationStore and add it to GlobalTranslator
        final MiniMessageTranslationStore translationStore = MiniMessageTranslationStore.create(Key.key("doburoku", "example"));
        translationStore.registerAll(Locale.US, ResourceBundle.getBundle("example", Locale.US, UTF8ResourceBundleControl.get()), false);
        translationStore.registerAll(Locale.JAPAN, ResourceBundle.getBundle("example", Locale.JAPAN, UTF8ResourceBundleControl.get()), false);
        GlobalTranslator.translator().addSource(translationStore);

        final List<Locale> locales = List.of(Locale.US, Locale.JAPAN);
        this.miniMessageService.loadTranslations(locales.size(), locales).accept(Level.INFO);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new DoburokuListener(miniMessageService), this);
    }

    private boolean miniPlaceholdersEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders");
    }
}
