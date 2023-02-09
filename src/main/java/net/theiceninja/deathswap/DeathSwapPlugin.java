package net.theiceninja.deathswap;

import net.theiceninja.deathswap.commands.DeathSwapCommand;
import net.theiceninja.deathswap.game.Game;
import net.theiceninja.deathswap.game.states.DefaultGameState;
import net.theiceninja.deathswap.utils.ColorUtil;
import net.theiceninja.deathswap.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathSwapPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(false);
        saveDefaultConfig();

        Game game = new Game(this);
        game.setGameState(new DefaultGameState());

        getCommand("deathswap").setExecutor(new DeathSwapCommand(game));
        getCommand("deathswap").setTabCompleter(new DeathSwapCommand(game));

        Bukkit.getConsoleSender().sendMessage(ColorUtil.color(Messages.PREFIX + "&adeath swap is now enabled!"));
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ColorUtil.color(Messages.PREFIX + "&cdeath swap is now disabled!"));
    }
}
