package eu.raymano.minigames;

import eu.raymano.utils.Utils;
import eu.raymano.lang.Message;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.function.Consumer;

public abstract class Counter {
    public Counter(World world) {
        this.world = world;
    }

    protected final LinkedHashSet<Player> players = new LinkedHashSet<>();
    protected int second = 1000;
    public int taskID = -1;
    public boolean gameStarted = false;
    private final World world;

    private void counterDown(int seconds) {
        second = seconds;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Utils.plugin, () -> {
            second--;
            if (second > 0) {
                sendTitle("§a" + second, "", 0, 40, 5);
            } else {
                Bukkit.getScheduler().cancelTask(taskID);
                gameStarted = true;
                startGame();
            }
        }, 10, 20);
    }

    public boolean playerJoin(Player p, boolean force) {
        if (!gameStarted && getCountPlayers() < getMaxPlayers()) {
            playerJoinToGame(p);
            MiniGames.playersInGame.put(p.getUniqueId(), this);
            int second = getSecond();
            p.setGameMode(GameMode.ADVENTURE);
            ((CraftInventoryPlayer) p.getInventory()).getInventory().clear();
            p.teleport(getSpawnLocalization());
            if (second > 0) {
                if (second < this.second) {
                    Bukkit.getScheduler().cancelTask(taskID);
                    counterDown(second);
                }
            } else {
                Bukkit.getScheduler().cancelTask(taskID);
            }
            return true;
        } else {
            if (gameStarted && force) {
                if (playerJoinAsSpectator(p)) {
                    MiniGames.playersInGame.put(p.getUniqueId(), this);
                    return true;
                } else
                    return false;
            }
        }
        return false;
    }

    public void playerLeave(Player p) {
        MiniGames.playersInGame.remove(p.getUniqueId());
        if (!gameStarted) {
            if (getSecond() == 0) {
                Bukkit.getScheduler().cancelTask(taskID);
                second = 1000;
            }
        }
    }

    public World getWorld() {
        return world;
    }

    public WorldServer getNmsWorld() {
        return ((CraftWorld) world).getHandle();
    }

    protected abstract void sendTitle(String text, String text2, int a, int b, int c);

    public abstract void sendMessage(Message message);

    public abstract void sendMessage(Message message, String... strings);

    public abstract void getPlayers(Consumer<Player> consumer);

    protected abstract void startGame();

    public abstract void playerJoinToGame(Player p);

    public abstract boolean playerJoinAsSpectator(Player p);

    protected abstract int getSecond();

    public abstract Location getSpawnLocalization();

    public abstract int getMaxPlayers();

    public abstract int getCountPlayers();
}