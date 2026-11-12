package dev.afkshard;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AfkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
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
            player.sendMessage("The End world isn't loaded.");
            return true;
        }

        // yaw 90 = facing west, pitch 0 = level
        Location target = new Location(end, 100.376, 49, 0.829, 90f, 0f);
        player.teleportAsync(target);
        return true;
    }
}
