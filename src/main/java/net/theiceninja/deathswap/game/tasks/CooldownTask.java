package net.theiceninja.deathswap.game.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.theiceninja.deathswap.game.Game;
import net.theiceninja.deathswap.game.states.ActiveGameState;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class CooldownTask extends BukkitRunnable {

    @Getter
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
        System.out.println();
        game.sendTitle("&eהמשחק מתחיל בעוד&8: &b" + timeLeft);
        game.playsound(Sound.BLOCK_NOTE_BLOCK_PLING);
        game.updateScoreBoard();
    }
}
