package net.simpleshop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Gives everyone 1 shard every 60 seconds and shows "Next Shard In Xs" on the action bar
 * to players inside 600 blocks of the main End island. The bar is hidden everywhere else.
 */
public final class ShardCountdown {

    private static final TextColor FUCHSIA = TextColor.color(0xFF00FF);
    private static final int INTERVAL_SECONDS = 60;
    private static final double RADIUS = 600.0;
    private static final double RADIUS_SQ = RADIUS * RADIUS;

    private final JavaPlugin plugin;
    private final ShardManager shardManager;

    // players who currently have the countdown on screen (so we only clear it once)
    private final Set<UUID> showing = new HashSet<>();
    private int secondsLeft = INTERVAL_SECONDS;

    public ShardCountdown(JavaPlugin plugin, ShardManager shardManager) {
        this.plugin = plugin;
        this.shardManager = shardManager;
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 20L, 20L);
    }

    private void tick() {
        secondsLeft--;

        if (secondsLeft <= 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                shardManager.add(player.getUniqueId(), 1);
                player.sendMessage(Component.text("You received ")
                        .append(Component.text("1 Shard", FUCHSIA)));
            }
            shardManager.save();
            secondsLeft = INTERVAL_SECONDS;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID id = player.getUniqueId();

            if (isInZone(player)) {
                player.sendActionBar(Component.text("Next Shard In ", NamedTextColor.WHITE)
                        .append(Component.text(secondsLeft + "s", FUCHSIA)));
                showing.add(id);
            } else if (showing.remove(id)) {
                // left the radius or changed dimension: hide it
                player.sendActionBar(Component.empty());
            }
        }
    }

    private static boolean isInZone(Player player) {
        if (player.getWorld().getEnvironment() != World.Environment.THE_END) return false;
        double x = player.getLocation().getX();
        double z = player.getLocation().getZ();
        return (x * x + z * z) <= RADIUS_SQ;
    }
}
