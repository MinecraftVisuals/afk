package dev.afkshard;

import org.bukkit.plugin.java.JavaPlugin;

public final class AfkShardPlugin extends JavaPlugin {

    private ShardTimer shardTimer;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        shardTimer = new ShardTimer(this);
        shardTimer.runTaskTimer(this, 20L, 20L);

        AfkCommand afk = new AfkCommand();
        getCommand("afk").setExecutor(afk);
        getServer().getPluginManager().registerEvents(shardTimer, this);
    }

    @Override
    public void onDisable() {
        if (shardTimer != null) {
            shardTimer.cancel();
        }
    }
}
