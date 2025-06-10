package net.derdernichtskann.lobbyItems.CosmeticsBox;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class CosmeticJoinListener implements Listener {

    private final JavaPlugin plugin;

    public CosmeticJoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getConfig().getBoolean("cosmetic-box.give-on-join.enabled", true)) {
            return;
        }

        List<String> allowedWorlds = plugin.getConfig().getStringList("cosmetic-box.give-on-join.worlds");
        if (!allowedWorlds.isEmpty() && !allowedWorlds.contains(player.getWorld().getName())) {
            return;
        }

        String permission = plugin.getConfig().getString("cosmetic-box.permission", "lobbyitems.cosmeticbox");
        if (!player.hasPermission(permission)) {
            return;
        }

        ItemStack cosmeticBox = createCosmeticBoxItem();

        int targetSlot = plugin.getConfig().getInt("cosmetic-box.give-on-join.slot", -1);

        if (targetSlot >= 0 && targetSlot < 9) {
            player.getInventory().setItem(targetSlot, cosmeticBox);
        } else {
            player.getInventory().addItem(cosmeticBox);
        }

        if (plugin.getConfig().getBoolean("cosmetic-box.give-on-join.send-message", true)) {
            String message = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.box-received", "&aYou received a Cosmetic Box! Right-click to open."));
            player.sendMessage(message);
        }
    }

    private ItemStack createCosmeticBoxItem() {
        String materialName = plugin.getConfig().getString("cosmetic-box.material", "ENDER_CHEST");
        Material material = Material.valueOf(materialName);

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String displayName = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("cosmetic-box.display-name", "&6&lCosmetic Box"));
        meta.setDisplayName(displayName);

        List<String> lore = plugin.getConfig().getStringList("cosmetic-box.lore");
        if (!lore.isEmpty()) {
            meta.setLore(lore.stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList()));
        }

        item.setItemMeta(meta);
        return item;
    }
}