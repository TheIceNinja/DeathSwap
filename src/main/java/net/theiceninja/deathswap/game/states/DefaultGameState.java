package net.theiceninja.deathswap.game.states;

import net.theiceninja.deathswap.game.GameState;
import net.theiceninja.deathswap.utils.ColorUtil;
import net.theiceninja.deathswap.utils.Messages;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DefaultGameState extends GameState {

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(ColorUtil.color(
                Messages.PREFIX + "&a" + player.getDisplayName()
                        + " &2נכנס לשרת!"
        ));

        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(ColorUtil.color(
                Messages.PREFIX + "&a" + player.getDisplayName()
                        + " &cיצא מהשרת!"
        ));
    }
}
