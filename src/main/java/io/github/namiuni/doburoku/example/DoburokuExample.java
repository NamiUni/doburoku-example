package io.github.namiuni.doburoku.example;

import io.github.miniplaceholders.api.MiniPlaceholders;
import io.github.namiuni.doburoku.spi.argument.ArgumentResolverRegistry;
import io.github.namiuni.doburoku.spi.result.ResultResolverRegistry;
import io.github.namiuni.doburoku.standard.DoburokuStandard;
import io.github.namiuni.doburoku.standard.argument.MiniMessageArgumentTransformer;
import io.leangen.geantyref.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.slf4j.event.Level;

@NullMarked
public final class DoburokuExample extends JavaPlugin {

    private final MiniMessageService miniMessageService;

    public DoburokuExample() {
        this.miniMessageService = DoburokuStandard.builder(MiniMessageService.class)
                .argument((ArgumentResolverRegistry registry) -> registry
                                .plus(Player.class, (parameter, argument) -> argument.displayName())
                                .plus(new TypeToken<List<Locale>>() {}, (parameter, locales) -> {
                                    final List<TextComponent> components = locales.stream()
                                            .map(Locale::toString)
                                            .map(Component::text)
                                            .toList();
                                    return Component.join(JoinConfiguration.arrayLike(), components);
                                })
                                .plus(LocalizedTime.class, (parameter, localizedTime) -> localizedTime.now())
                                .plus(Location.class, (parameter, location) -> {
                                    final String position = "X:%s, Y:%s, Z:%s".formatted(location.x(), location.y(), location.z());
                                    return Component.text(position);
                                })
                        , MiniMessageArgumentTransformer.create()) // Use a parameter name as a placeholder key
                .result((ResultResolverRegistry registry) -> registry
                        .plus(new TypeToken<Consumer<Level>>() {}, (method, component) -> level -> {
                            switch (level) {
                                case INFO -> this.getComponentLogger().info(component);
                                case WARN -> this.getComponentLogger().warn(component);
                                case ERROR -> this.getComponentLogger().error(component);
                                case DEBUG -> this.getComponentLogger().debug(component);
                                case TRACE -> this.getComponentLogger().trace(component);
                            }
                        })
                        .plus(PlayerMessage.class, (method, component) -> player -> {
                            if (!this.miniPlaceholdersEnabled()) {
                                player.sendMessage(component);
                                return;
                            }

                            final List<ComponentLike> argumentsList = new ArrayList<>(component.arguments());
                            argumentsList.add(Argument.tagResolver(MiniPlaceholders.getAudienceGlobalPlaceholders(player)));
                            final ComponentLike[] arguments = argumentsList.toArray(ComponentLike[]::new);

                            final TranslatableComponent result = Component.translatable(component.key(), arguments);
                            player.sendMessage(result);
                        })
                        .plus(int.class, ((method, component) -> Bukkit.broadcast(component))))
                .brew();
    }

    @Override
    public void onLoad() {
        // Initialize TranslationStore and add it to GlobalTranslator
        final MiniMessageTranslationStore translationStore = MiniMessageTranslationStore.create(Key.key("doburoku", "messages"));
        translationStore.registerAll(Locale.US, ResourceBundle.getBundle("messages", Locale.US, UTF8ResourceBundleControl.get()), false);
        translationStore.registerAll(Locale.JAPAN, ResourceBundle.getBundle("messages", Locale.JAPAN, UTF8ResourceBundleControl.get()), false);
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
