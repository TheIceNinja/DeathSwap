package net.theiceninja.deathswap.commands;

import net.theiceninja.deathswap.commands.subcommands.*;
import net.theiceninja.deathswap.game.Game;
import net.theiceninja.deathswap.utils.ColorUtil;
import net.theiceninja.deathswap.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeathSwapCommand implements CommandExecutor, TabCompleter {

    private final List<SubCommand> subCommands = new ArrayList<>();

    public DeathSwapCommand(Game game) {
        subCommands.add(new SetLocationSubCommand(game.getPlugin()));
        subCommands.add(new StartSubCommand(game));
        subCommands.add(new StopSubCommand(game));
        subCommands.add(new ReviveSubCommand(game));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.MUST_BE_PLAYER_ERROR);
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("deathswap.admin")) {
            player.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ColorUtil.color("&eUsage: /deathswap <start|stop|setlocation|revive>"));
            return true;
        }

        for (SubCommand subCommand : subCommands) {
            if (args[0].equalsIgnoreCase(subCommand.getName())) {
                subCommand.execute(player, args);
                break;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> complete = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("deathswap.admin")) {
            complete.add("start");
            complete.add("stop");
            complete.add("setlocation");
            complete.add("revive");
        }

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : complete) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(a);
            }

            return result;
        }

        return null;
    }
}
