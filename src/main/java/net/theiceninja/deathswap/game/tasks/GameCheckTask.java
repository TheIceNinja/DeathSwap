package net.theiceninja.deathswap.game.tasks;

import lombok.AllArgsConstructor;
import net.theiceninja.deathswap.game.Game;
import net.theiceninja.deathswap.game.states.ActiveGameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
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

            teleportToRandomPlayer();
            game.playsound(Sound.BLOCK_NOTE_BLOCK_BIT);
            return;
        }

        if (timeLeft <= 5) {
            game.sendMessage("&fמשתגרים בעוד&8: &e" + timeLeft);
            game.playsound(Sound.BLOCK_NOTE_BLOCK_PLING);
        }
    }

    private void teleportToRandomPlayer() {
        final List<UUID> alreadyTeleported = new ArrayList<>(); // the list of the players who were teleported
        final Map<UUID, Location> locationMap = new HashMap<>();

        for (UUID playerUUID : game.getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            locationMap.put(player.getUniqueId(), player.getLocation());
        }

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
            Location location = locationMap.get(teleportedPlayer.getUniqueId());
            player.teleport(location);
        }

        game.sendMessage("&aהשתגרתם!");
        game.setRounds(game.getRounds() + 1);
        game.updateScoreBoard();
        game.sendMessage("&fזאת הייתה ההשתגרות ה&e" + game.getRounds() + " &fשלכם!");

        alreadyTeleported.clear();
        locationMap.clear();
        ActiveGameState activeGameState = (ActiveGameState) game.getGameState();

        int randomNumber = (int) game.randomizer(39, (60 * 5) + 1);
        activeGameState.setCheckTask(new GameCheckTask(randomNumber, game));
        activeGameState.getCheckTask().runTaskTimer(game.getPlugin(), 0, 20);
    }
}
