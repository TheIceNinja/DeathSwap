package net.theiceninja.deathswap.game.states;

import lombok.Getter;
import lombok.Setter;
import net.theiceninja.deathswap.DeathSwapPlugin;
import net.theiceninja.deathswap.game.GameState;
import net.theiceninja.deathswap.game.tasks.GameCheckTask;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.UUID;

public class ActiveGameState extends GameState {

    @Getter @Setter
    private GameCheckTask checkTask;

    @Override
    public void onEnable(DeathSwapPlugin plugin) {
        super.onEnable(plugin);
        getGame().sendMessage("&aהמשחק התחיל!");
        getGame().updateScoreBoard();

        int randomNumber = (int) getGame().randomizer(39, (60 * 5) + 1);
        this.checkTask = new GameCheckTask(randomNumber, getGame());
        this.checkTask.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.checkTask != null) checkTask.cancel();

        getGame().sendMessage("&cהמשחק נגמר...");
        getGame().playsound(Sound.ENTITY_ENDER_DRAGON_DEATH);
        for (UUID playerUUID : getGame().getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.setGameMode(GameMode.ADVENTURE);
            player.setFoodLevel(20);
            player.setHealth(20);

            player.getInventory().clear();
            player.teleport(getGame().getSpawnLocation());
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }

        for (UUID playerUUID : getGame().getSpectators()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.setGameMode(GameMode.ADVENTURE);
            player.setFoodLevel(20);
            player.setHealth(20);

            player.getInventory().clear();
            player.teleport(getGame().getSpawnLocation());
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }

        getGame().getPlayers().clear();
        getGame().getSpectators().clear();
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!getGame().isPlaying(player)) return;

        event.setDeathMessage(null);
        getGame().removePlayer(player);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        getGame().removePlayer(player);
        event.setJoinMessage(null);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (getGame().isSpectating(player)) {
            getGame().getSpectators().remove(player.getUniqueId());
            event.setQuitMessage(null);
        } else {
            getGame().removePlayer(player);
            event.setQuitMessage(null);
        }
    }

    @EventHandler
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player)
            event.setCancelled(true);
    }
}
