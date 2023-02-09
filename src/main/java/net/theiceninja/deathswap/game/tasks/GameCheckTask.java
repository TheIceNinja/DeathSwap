package net.theiceninja.deathswap.game.tasks;

import lombok.AllArgsConstructor;
import net.theiceninja.deathswap.game.Game;
import net.theiceninja.deathswap.game.states.ActiveGameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@AllArgsConstructor
public class GameCheckTask extends BukkitRunnable {

    private int timeLeft;
    private Game game;

    @Override
    public void run() {
        timeLeft--;
        if (timeLeft <= 0) {
            cancel();

            // todo: swap
            teleportToRandomPlayer();
            return;
        }

        if (timeLeft <= 5) {
            game.sendMessage("&fמשתגרים בעוד&8: &e" + timeLeft);
        }

        game.sendActionBar("&cDebugging action bar&8: &e" + timeLeft);
    }

    private void teleportToRandomPlayer() {
        final List<UUID> alreadyTeleported = new ArrayList<>(); // the list of the players who were teleported

        for (UUID playerUUID : game.getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            int randomPlayerIndex = (int) game.randomizer(-1, game.getPlayers().size());
            Player teleportedPlayer = Bukkit.getPlayer(game.getPlayers().get(randomPlayerIndex));
            while (player.equals(teleportedPlayer) || alreadyTeleported.contains(teleportedPlayer.getUniqueId())) {
                randomPlayerIndex = (int) game.randomizer(-1, game.getPlayers().size());
                teleportedPlayer = Bukkit.getPlayer(game.getPlayers().get(randomPlayerIndex));
            }

            alreadyTeleported.add(teleportedPlayer.getUniqueId());
            Location location = teleportedPlayer.getLocation();
            player.teleport(location);
        }

        game.sendMessage("&aהשתגרתם!");
        game.setRounds(game.getRounds() + 1);
        game.sendMessage("&fזאת הייתה ההשתגרות ה&e" + game.getRounds() + " &fשלכם!");

        alreadyTeleported.clear();
        ActiveGameState activeGameState = (ActiveGameState) game.getGameState();

        int randomNumber = (int) game.randomizer(39, (60 * 4) + 1);
        activeGameState.setCheckTask(new GameCheckTask(randomNumber, game));
        activeGameState.getCheckTask().runTaskTimer(game.getPlugin(), 0, 20);
    }
}
