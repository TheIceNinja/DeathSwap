package net.theiceninja.deathswap.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.theiceninja.deathswap.DeathSwapPlugin;
import net.theiceninja.deathswap.game.states.ActiveGameState;
import net.theiceninja.deathswap.game.states.CooldownGameState;
import net.theiceninja.deathswap.game.states.DefaultGameState;
import net.theiceninja.deathswap.utils.ColorUtil;
import net.theiceninja.deathswap.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Game {

    private final List<UUID> players = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();

    @Setter
    private int rounds = 0;

    private GameState gameState;
    private final DeathSwapPlugin plugin;

    public void setGameState(GameState gameState) {
        if (this.gameState != null) this.gameState.onDisable();

        this.gameState = gameState;
        gameState.setGame(this);
        gameState.onEnable(plugin);
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());

        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.teleport(getSpawnLocation());
        updateScoreBoard();
    }

    public void removePlayer(Player player) {
        if (isPlaying(player)) players.remove(player.getUniqueId());
        if (!(gameState instanceof ActiveGameState)) return;

        spectators.add(player.getUniqueId());
        player.setGameMode(GameMode.SPECTATOR);
        updateScoreBoard();
        playsound(Sound.ENTITY_BLAZE_HURT);
        sendMessage("&cהשחקן &e&l" + player.getDisplayName() + " &cנפסל!");
        if (players.size() == 1) {
            Player winner = Bukkit.getPlayer(players.get(0));
            if (winner == null) return;

            sendMessage("&c&lהמנצח של המשחק הוא &6&l" + winner.getName());
            setGameState(new DefaultGameState());
        } else if (players.isEmpty()) {
            sendMessage("&cמנצח המשחק לא נמצא.");
            setGameState(new DefaultGameState());
        }
    }

    public void sendMessage(String message) {
        for (UUID playerUUID : players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.sendMessage(ColorUtil.color(Messages.PREFIX + message));
        }

        for (UUID playerUUID : spectators) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.sendMessage(ColorUtil.color(Messages.PREFIX + message));
        }
    }

    public void sendTitle(String message) {
        for (UUID playerUUID : this.players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.sendTitle(ColorUtil.color("&#FA1B00&lDeath&#E300FA&lSwap"), ColorUtil.color(message), 0, 40, 0);
        }

        for (UUID playerUUID : this.spectators) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.sendTitle(ColorUtil.color("&#FA1B00&lDeath&#E300FA&lSwap"), ColorUtil.color(message), 0, 40, 0);
        }
    }

    private void setScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        List<String> scoreboardLines = new ArrayList<>();

        Objective objective = scoreboard.registerNewObjective("ice",
                "dummy",
                ColorUtil.color("&#3bb6fb&lN&#4bbce7&li&#5bc3d3&ln&#6bc9be&lj&#7bd0aa&la&#8bd696&lN&#9bdd82&le&#abe36e&lt&#bbea5a&lw&#cbf045&lo&#dbf731&lr&#ebfd1d&lk &7| &fחילופי מוות"));
        scoreboardLines.add("&r");

        if (getGameState() instanceof CooldownGameState) {
            CooldownGameState cooldownGameState = (CooldownGameState) getGameState();
            if (cooldownGameState.getCooldownTask() == null) return;

            scoreboardLines.add("&fהמשחק מתחיל בעוד&8: &e" + cooldownGameState.getCooldownTask().getTimeLeft());
        } else if (getGameState() instanceof ActiveGameState) {
            scoreboardLines.add("&fמצבך במשחק&8: " + (isPlaying(player) ? "&aחי" : "&7צופה"));
            scoreboardLines.add("&r ");
            scoreboardLines.add("&fכמות הסיבובים&8: &2" +  getRounds());
        }

        scoreboardLines.add("&f");
        scoreboardLines.add("&fשחקנים שיש במשחק&8: &6" + players.size());

        scoreboardLines.add("&r ");
        scoreboardLines.add("&7play.iceninja.us.to");

        for (int i = 0; i < scoreboardLines.size(); i++) {
            String line = ColorUtil.color(scoreboardLines.get(i));
            objective.getScore(line).setScore(scoreboardLines.size() - i);
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    public void updateScoreBoard() {
        for (UUID playerUUID : players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            setScoreboard(player);
        }

        for (UUID playerUUID : spectators) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            setScoreboard(player);
        }
    }

    public void playsound(Sound sound) {
        for (UUID playerUUID : players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.playSound(player, sound, 1, 1);
        }

        for (UUID playerUUID : spectators) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.playSound(player, sound, 1, 1);
        }
    }

    public double randomizer(int a, int b) {
        return (double) b + (Math.random() * (a - b + 1));
    }

    public boolean isPlaying(Player player) {
        return this.players.contains(player.getUniqueId());
    }

    public boolean isSpectating(Player player) {
        return this.spectators.contains(player.getUniqueId());
    }

    public Location getSpawnLocation() {
        return plugin.getConfig().getLocation("game.spawn");
    }

    public Location getSpectatorsLocation() {
        return plugin.getConfig().getLocation("game.spectators");
    }
}
sealed
