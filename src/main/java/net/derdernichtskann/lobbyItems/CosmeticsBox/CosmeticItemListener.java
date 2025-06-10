package net.derdernichtskann.lobbyItems.CosmeticsBox;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CosmeticItemListener implements Listener {

    private final JavaPlugin plugin;
    private final CosmeticBoxListener boxListener;

    public CosmeticItemListener(JavaPlugin plugin, CosmeticBoxListener boxListener) {
        this.plugin = plugin;
        this.boxListener = boxListener;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        String expectedName = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("cosmetic-box.display-name", "&6&lCosmetic Box"));

        if (!item.getItemMeta().getDisplayName().equals(expectedName)) {
            return;
        }

        List<String> allowedWorlds = plugin.getConfig().getStringList("cosmetic-box.use-in-worlds");
        if (!allowedWorlds.isEmpty() && !allowedWorlds.contains(player.getWorld().getName())) {
            String message = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.wrong-world", "&cYou cannot use this item in this world!"));
            player.sendMessage(message);
            return;
        }

        String permission = plugin.getConfig().getString("cosmetic-box.permission", "lobbyitems.cosmeticbox");
        if (!player.hasPermission(permission)) {
            String message = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.no-permission", "&cYou don't have permission to use this item!"));
            player.sendMessage(message);
            return;
        }

        event.setCancelled(true);
        boxListener.openLobbyItemsGUI(player);
    }
}