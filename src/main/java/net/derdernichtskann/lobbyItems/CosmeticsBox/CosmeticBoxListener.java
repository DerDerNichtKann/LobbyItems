package net.derdernichtskann.lobbyItems.CosmeticsBox;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class CosmeticBoxListener implements Listener {

    private final JavaPlugin plugin;

    public CosmeticBoxListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void openLobbyItemsGUI(Player player) {
        String title = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("gui.title", "&9&lLobby Items"));
        int size = plugin.getConfig().getInt("gui.size", 27);

        Inventory gui = Bukkit.createInventory(null, size, title);

        for (int slot = 0; slot < gui.getSize(); slot++) {
            ItemStack filler = getFillerPane(slot);
            gui.setItem(slot, filler);
        }

        setupGrapplingHook(gui, player);
        setupDoubleJump(gui, player);
        setupElytra(gui, player);

        player.openInventory(gui);
    }

    private void setupGrapplingHook(Inventory gui, Player player) {
        if (!plugin.getConfig().getBoolean("items.grappling-hook.enabled", true)) return;

        int slot = plugin.getConfig().getInt("items.grappling-hook.slot", 10);
        String materialName = plugin.getConfig().getString("items.grappling-hook.material", "FISHING_ROD");
        Material material = Material.valueOf(materialName);

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String displayName = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("items.grappling-hook.display-name", "&6Grappling Hook"));
        meta.setDisplayName(displayName);

        String permission = plugin.getConfig().getString("items.grappling-hook.permission", "lobbyitems.grappling");
        List<String> lore;

        if (player.hasPermission(permission)) {
            lore = plugin.getConfig().getStringList("items.grappling-hook.lore.available");
        } else {
            lore = plugin.getConfig().getStringList("items.grappling-hook.lore.no-permission");
        }

        meta.setLore(lore.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList()));

        item.setItemMeta(meta);
        gui.setItem(slot, item);
    }

    private void setupDoubleJump(Inventory gui, Player player) {
        if (!plugin.getConfig().getBoolean("items.double-jump.enabled", true)) return;

        int slot = plugin.getConfig().getInt("items.double-jump.slot", 13);
        String materialName = plugin.getConfig().getString("items.double-jump.material", "FEATHER");
        Material material = Material.valueOf(materialName);

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String displayName = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("items.double-jump.display-name", "&6Double Jump"));
        meta.setDisplayName(displayName);

        String permission = plugin.getConfig().getString("items.double-jump.permission", "lobbyitems.doublejump");
        List<String> lore;

        if (player.hasPermission(permission)) {
            lore = plugin.getConfig().getStringList("items.double-jump.lore.available");
        } else {
            lore = plugin.getConfig().getStringList("items.double-jump.lore.no-permission");
        }

        meta.setLore(lore.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList()));

        item.setItemMeta(meta);
        gui.setItem(slot, item);
    }

    private void setupElytra(Inventory gui, Player player) {
        if (!plugin.getConfig().getBoolean("items.elytra.enabled", true)) return;

        int slot = plugin.getConfig().getInt("items.elytra.slot", 16);
        String materialName = plugin.getConfig().getString("items.elytra.material", "ELYTRA");
        Material material = Material.valueOf(materialName);

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String displayName = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("items.elytra.display-name", "&6Elytra"));
        meta.setDisplayName(displayName);

        String permission = plugin.getConfig().getString("items.elytra.permission", "lobbyitems.elytra");
        List<String> lore;

        if (player.hasPermission(permission)) {
            lore = plugin.getConfig().getStringList("items.elytra.lore.available");
        } else {
            lore = plugin.getConfig().getStringList("items.elytra.lore.no-permission");
        }

        meta.setLore(lore.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList()));

        item.setItemMeta(meta);
        gui.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String guiTitle = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("gui.title", "&9&lLobby Items"));

        if (!event.getView().getTitle().equals(guiTitle)) return;
        event.setCancelled(true);

        int slot = event.getRawSlot();
        Player player = (Player) event.getWhoClicked();
        CosmeticType chosen = CosmeticType.NONE;

        if (slot == plugin.getConfig().getInt("items.grappling-hook.slot", 10)) {
            chosen = CosmeticType.GRAPPLING;
        } else if (slot == plugin.getConfig().getInt("items.double-jump.slot", 13)) {
            chosen = CosmeticType.DOUBLE_JUMP;
        } else if (slot == plugin.getConfig().getInt("items.elytra.slot", 16)) {
            chosen = CosmeticType.ELYTRA;
        }

        if (chosen == CosmeticType.NONE) return;

        String permission = getPermissionForCosmetic(chosen);
        if (!player.hasPermission(permission)) {
            String noPermMessage = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.no-permission", "&cYou don't have permission to use this item!"));
            player.sendMessage(noPermMessage);

            String errorSoundName = plugin.getConfig().getString("sounds.error", "BLOCK_NOTE_BLOCK_BASS");
            Sound errorSound = Sound.valueOf(errorSoundName);
            player.playSound(player.getLocation(), errorSound, 1.0f, 1.0f);
            return;
        }

        CosmeticManager.applyCosmetic(player, chosen);
        player.closeInventory();

        String successMessage = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.cosmetic-activated", "&aCosmetic activated!"));
        player.sendMessage(successMessage);

        String successSoundName = plugin.getConfig().getString("sounds.success", "ENTITY_PLAYER_LEVELUP");
        Sound successSound = Sound.valueOf(successSoundName);
        player.playSound(player.getLocation(), successSound, 1.0f, 1.0f);
    }

    private String getPermissionForCosmetic(CosmeticType type) {
        switch (type) {
            case GRAPPLING:
                return plugin.getConfig().getString("items.grappling-hook.permission", "lobbyitems.grappling");
            case DOUBLE_JUMP:
                return plugin.getConfig().getString("items.double-jump.permission", "lobbyitems.doublejump");
            case ELYTRA:
                return plugin.getConfig().getString("items.elytra.permission", "lobbyitems.elytra");
            default:
                return "";
        }
    }

    private ItemStack getFillerPane(int slot) {
        int row = slot / 9;
        String materialPath = "gui.filler.row-" + row + ".material";
        String defaultMaterial = row == 0 ? "LIGHT_BLUE_STAINED_GLASS_PANE" :
                row == 1 ? "CYAN_STAINED_GLASS_PANE" : "BLUE_STAINED_GLASS_PANE";

        String materialName = plugin.getConfig().getString(materialPath, defaultMaterial);
        Material material = Material.valueOf(materialName);

        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        pane.setItemMeta(meta);
        return pane;
    }
}