package eu.raymano.minigames;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class MiniGames implements Listener {

    public static HashMap<UUID, Counter> playersInGame = new HashMap<>();

    @EventHandler
    public void leavePlayer(PlayerQuitEvent e) {
        leavePlayer(e.getPlayer());
    }

    public static void leavePlayer(Player p) {
        UUID uuid = p.getUniqueId();
        Counter miniGame = playersInGame.get(uuid);
        if (miniGame != null) {
            miniGame.playerLeave(p);
            playersInGame.remove(uuid);
        }
    }
}
