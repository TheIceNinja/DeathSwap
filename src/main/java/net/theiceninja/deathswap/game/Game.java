package net.theiceninja.deathswap.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.theiceninja.deathswap.DeathSwapPlugin;
import net.theiceninja.deathswap.game.states.ActiveGameState;
import net.theiceninja.deathswap.game.states.DefaultGameState;
import net.theiceninja.deathswap.utils.ColorUtil;
import net.theiceninja.deathswap.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Game {

    private final List<UUID> players = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();

    private GameState gameState;
    private final DeathSwapPlugin plugin;

    @Setter
    private int rounds = 0;

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());

        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.teleport(getSpawnLocation());
    }

    public void removePlayer(Player player) {
        if (isPlaying(player)) players.remove(player.getUniqueId());
        if (!(gameState instanceof ActiveGameState)) return;

        spectators.add(player.getUniqueId());
        player.setGameMode(GameMode.SPECTATOR);
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

    public void sendActionBar(String str) {
        for (UUID playerUUID : players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ColorUtil.color(str)));
        }

        for (UUID playerUUID : spectators) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ColorUtil.color(str)));
        }
    }

    public void setGameState(GameState gameState) {
        if (this.gameState != null) this.gameState.onDisable();

        this.gameState = gameState;
        gameState.setGame(this);
        gameState.onEnable(plugin);
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
