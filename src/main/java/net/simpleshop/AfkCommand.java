package net.simpleshop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/** /afk teleports you to the End AFK spot, facing west. */
public final class AfkCommand {

    private static final double X = 100.376;
    private static final double Y = 49;
    private static final double Z = 0.829;
    private static final float YAW = 90f;   // 90 = facing west
    private static final float PITCH = 0f;

    private final CombatTag combatTag;

    public AfkCommand(JavaPlugin plugin, CombatTag combatTag) {
        this.combatTag = combatTag;
        PluginCommand cmd = plugin.getCommand("afk");
        if (cmd != null) cmd.setExecutor(this::onCommand);
    }

    private boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use /afk.");
            return true;
        }

        if (combatTag.isInCombat(player)) {
            player.sendMessage(Component.text("You can't use /afk while in combat.", NamedTextColor.RED));
            return true;
        }

        World end = null;
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.THE_END) {
                end = world;
                break;
            }
        }
        if (end == null) {
            player.sendMessage(Component.text("The End isn't available.", NamedTextColor.RED));
            return true;
        }

        player.teleportAsync(new Location(end, X, Y, Z, YAW, PITCH));
        return true;
    }
}
