package io.github.namiuni.doburoku.example;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class DoburokuListener implements Listener {

    private final MiniMessageService miniMessageService;

    public DoburokuListener(final MiniMessageService miniMessageService) {
        this.miniMessageService = miniMessageService;
    }

    @EventHandler
    private void onJoin(final PlayerJoinEvent event) {

        final Player moonshiner = event.getPlayer();
        final int totalMoonshiners = Bukkit.getServer().getOnlinePlayers().size();
        final Component message = this.miniMessageService.joinBroadcast(moonshiner, totalMoonshiners);
        event.joinMessage(message);

        final LocalizedTime time = new LocalizedTime(moonshiner.locale());
        this.miniMessageService.joinMessage(time).send(moonshiner);
    }

    @EventHandler
    private void onBrew(final BrewEvent event) {
        final Location location = event.getBlock().getLocation();
        this.miniMessageService.brewBroadcast(location);
    }
}
