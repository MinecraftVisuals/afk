package dev.afkshard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ShardTimer extends BukkitRunnable implements Listener {

    private final AfkShardPlugin plugin;
    private final Map<UUID, Integer> remaining = new HashMap<>();

    public ShardTimer(AfkShardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        double radius = plugin.getConfig().getDouble("radius", 600);
        double radiusSq = radius * radius;
        int interval = Math.max(1, plugin.getConfig().getInt("interval-seconds", 60));
        String message = plugin.getConfig().getString("message", "<light_purple>Next Shard In <white>%ss");

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID id = player.getUniqueId();

            if (!isInZone(player, radiusSq)) {
                // Left the radius or changed dimension: hide + reset
                if (remaining.remove(id) != null) {
                    player.sendActionBar(Component.empty());
                }
                continue;
            }

            int left = remaining.getOrDefault(id, interval);
            if (left <= 0) {
                giveShard(player);
                left = interval;
            }

            player.sendActionBar(MiniMessage.miniMessage().deserialize(String.format(message, left)));
            remaining.put(id, left - 1);
        }
    }

    private boolean isInZone(Player player, double radiusSq) {
        if (player.getWorld().getEnvironment() != World.Environment.THE_END) {
            return false;
        }
        double x = player.getLocation().getX();
        double z = player.getLocation().getZ();
        return (x * x + z * z) <= radiusSq;
    }

    private void giveShard(Player player) {
        for (String cmd : plugin.getConfig().getStringList("reward-commands")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        remaining.remove(event.getPlayer().getUniqueId());
    }
}
