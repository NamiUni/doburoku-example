package io.github.namiuni.doburoku.example;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
@FunctionalInterface
public interface PlayerMessage {
    void send(Player player);
}
