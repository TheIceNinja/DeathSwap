package net.theiceninja.deathswap.game.states;

import lombok.AllArgsConstructor;
import net.theiceninja.deathswap.game.Game;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

@AllArgsConstructor
public class CommonListenerState implements Listener {

    private Game game;

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (game.getGameState() instanceof ActiveGameState) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (game.getGameState() instanceof ActiveGameState) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (game.getGameState() instanceof ActiveGameState) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        if (game.getGameState() instanceof ActiveGameState) return;

        event.setCancelled(true);
    }
}
