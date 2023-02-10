package net.theiceninja.deathswap.commands.subcommands;

import lombok.AllArgsConstructor;
import net.theiceninja.deathswap.game.Game;
import net.theiceninja.deathswap.game.states.ActiveGameState;
import net.theiceninja.deathswap.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class ReviveSubCommand implements SubCommand {

    private Game game;

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 1) {
            player.sendMessage(ColorUtil.color("&cאתה צריך לשים את השם של השחקן כדי להחיות אותו!"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ColorUtil.color("&cשחקן זה לא קיים בשרת!"));
            return;
        }

        if (!(game.getGameState() instanceof ActiveGameState)) {
            player.sendMessage(ColorUtil.color("&cאתה לא יכול להחיות מישהו כשהמשחק לא פעיל!"));
            return;
        }

        if (!game.isSpectating(target)) {
            player.sendMessage(ColorUtil.color("&cשחקן זה לא צופה במשחק בכלל!"));
            return;
        }

        game.getSpectators().remove(target.getUniqueId());
        game.addPlayer(target);
        game.sendMessage("&eהשחקן &b&l" + target.getDisplayName() + " &eחזר לחיים!");
    }

    @Override
    public String getName() {
        return "revive";
    }
}
