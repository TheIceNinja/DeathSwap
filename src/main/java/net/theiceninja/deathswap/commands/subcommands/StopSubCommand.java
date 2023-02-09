package net.theiceninja.deathswap.commands.subcommands;

import lombok.AllArgsConstructor;
import net.theiceninja.deathswap.game.Game;
import net.theiceninja.deathswap.game.states.ActiveGameState;
import net.theiceninja.deathswap.game.states.DefaultGameState;
import net.theiceninja.deathswap.utils.ColorUtil;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class StopSubCommand implements SubCommand {

    private Game game;

    @Override
    public void execute(Player player, String[] args) {
        if (!(game.getGameState() instanceof ActiveGameState)) {
            player.sendMessage(ColorUtil.color("&cאי אפשר לעצור את המשחק כשהוא לא פועל.."));
            return;
        }

        game.setGameState(new DefaultGameState());
    }

    @Override
    public String getName() {
        return "stop";
    }
}
