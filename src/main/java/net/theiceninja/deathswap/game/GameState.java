package net.theiceninja.deathswap.game;

import lombok.Getter;
import lombok.Setter;
import net.theiceninja.deathswap.DeathSwapPlugin;
import net.theiceninja.deathswap.game.states.CommonListenerState;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class GameState implements Listener {

    @Getter @Setter
    private Game game;

    public void onEnable(DeathSwapPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getPluginManager().registerEvents(new CommonListenerState(game), plugin);
    }

    public void onDisable() {
        HandlerList.unregisterAll(this);
        HandlerList.unregisterAll(new CommonListenerState(game));
    }

}
