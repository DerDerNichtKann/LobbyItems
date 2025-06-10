package net.derdernichtskann.lobbyItems.CosmeticsBox;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CosmeticCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final CosmeticBoxListener boxListener;

    public CosmeticCommand(JavaPlugin plugin, CosmeticBoxListener boxListener) {
        this.plugin = plugin;
        this.boxListener = boxListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            String message = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.only-players", "&cThis command can only be executed by players!"));
            sender.sendMessage(message);
            return true;
        }

        Player player = (Player) sender;

        List<String> allowedWorlds = plugin.getConfig().getStringList("command.allowed-worlds");
        if (!allowedWorlds.isEmpty() && !allowedWorlds.contains(player.getWorld().getName())) {
            String message = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.wrong-world", "&cYou cannot use this command in this world!"));
            player.sendMessage(message);
            return true;
        }

        String permission = plugin.getConfig().getString("cosmetic-box.permission", "lobbyitems.cosmeticbox");
        if (!player.hasPermission(permission)) {
            String message = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.no-permission", "&cYou don't have permission to use this command!"));
            player.sendMessage(message);
            return true;
        }

        boxListener.openLobbyItemsGUI(player);
        return true;
    }
}