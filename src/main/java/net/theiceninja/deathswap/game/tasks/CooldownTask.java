package net.theiceninja.deathswap.game.tasks;

import lombok.RequiredArgsConstructor;
import net.theiceninja.deathswap.game.Game;
import net.theiceninja.deathswap.game.states.ActiveGameState;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class CooldownTask extends BukkitRunnable {

    private int timeLeft = 11;
    private final Game game;

    @Override
    public void run() {
        timeLeft--;
        if (timeLeft <= 0) {
            cancel();
            game.setGameState(new ActiveGameState());

            return;
        }

        game.sendTitle("&eהמשחק מתחיל בעוד&8: &b" + timeLeft);
    }
}
