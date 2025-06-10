package net.derdernichtskann.lobbyItems;

import net.derdernichtskann.lobbyItems.CosmeticsBox.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class LobbyItems extends JavaPlugin {
    PluginDescriptionFile pdf = this.getDescription();
    public static LobbyItems instance;
    @Override
    public void onEnable() {
        instance = this;
        Metrics metrics = new Metrics(this, 26146);
        saveDefaultConfig();

        CosmeticManager.initialize(getConfig());

        CosmeticBoxListener boxListener = new CosmeticBoxListener(this);
        CosmeticJoinListener joinListener = new CosmeticJoinListener(this);
        CosmeticItemListener itemListener = new CosmeticItemListener(this, boxListener);
        CosmeticCommand command = new CosmeticCommand(this, boxListener);

        getServer().getPluginManager().registerEvents(boxListener, this);
        getServer().getPluginManager().registerEvents(joinListener, this);
        getServer().getPluginManager().registerEvents(itemListener, this);
        getServer().getPluginManager().registerEvents(new CosmeticGrapplingHookListener(getConfig()), this);
        getServer().getPluginManager().registerEvents(new CosmeticElytraBoostListener(getConfig()), this);
        getServer().getPluginManager().registerEvents(new CosmeticDoubleJumpListener(getConfig()), this);

        getCommand("cosmetics").setExecutor(command);

        Bukkit.getConsoleSender().sendMessage( "------------------------------------------");
        Bukkit.getConsoleSender().sendMessage("| §6LobbyItems " + pdf.getVersion() + " by DerDerNichtsKann");
        Bukkit.getConsoleSender().sendMessage( "| §7Has been §2Enabled");
        Bukkit.getConsoleSender().sendMessage("==========================================");

    }

    public void reloadPluginConfig() {
        reloadConfig();
        CosmeticManager.initialize(getConfig());
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("==========================================");
        Bukkit.getConsoleSender().sendMessage("| §6LobbyItems " + pdf.getVersion() + " by DerDerNichtsKann");
        Bukkit.getConsoleSender().sendMessage("| §7Has been §cDisabled");
        Bukkit.getConsoleSender().sendMessage("==========================================");
    }
}
