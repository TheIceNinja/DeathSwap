package net.theiceninja.deathswap.game.states;

import lombok.Getter;
import net.theiceninja.deathswap.DeathSwapPlugin;
import net.theiceninja.deathswap.game.GameState;
import net.theiceninja.deathswap.game.tasks.CooldownTask;
import net.theiceninja.deathswap.utils.ColorUtil;
import net.theiceninja.deathswap.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CooldownGameState extends GameState {

    @Getter
    private CooldownTask cooldownTask;

    @Override
    public void onEnable(DeathSwapPlugin plugin) {
        super.onEnable(plugin);

        for (Player player : Bukkit.getOnlinePlayers()) {
            getGame().addPlayer(player);
        }

        getGame().sendMessage("&aהמשחק עומד להתחיל עכשיו!");

        this.cooldownTask = new CooldownTask(getGame());
        this.cooldownTask.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.cooldownTask != null) this.cooldownTask.cancel();
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(null);
        player.kickPlayer(ColorUtil.color(Messages.PREFIX + "&cהמשחק כבר התחיל כשיגמר הקולדאון אז תוכל להכנס מצופה."));
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!getGame().isPlaying(player)) return;

        event.setQuitMessage(null);
        getGame().removePlayer(player);
    }
}
