package net.derdernichtskann.lobbyItems.CosmeticsBox;

import net.derdernichtskann.lobbyItems.LobbyItems;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class CosmeticManager {
    private static final Map<Player, CosmeticType> activeCosmetics = new HashMap<>();
    private static FileConfiguration config;

    public static void initialize(FileConfiguration configuration) {
        config = configuration;
    }

    public static void applyCosmetic(Player player, CosmeticType type) {
        removeCosmetic(player);
        activeCosmetics.put(player, type);

        if (type == CosmeticType.ELYTRA) {
            player.getInventory().setChestplate(getCosmeticElytra());
        }

        if (type == CosmeticType.GRAPPLING) {
            player.getInventory().addItem(getCosmeticGrapplingHook());
        }

        if (type == CosmeticType.DOUBLE_JUMP) {
            player.getInventory().addItem(getCosmeticDoubleJump());
        }
    }

    public static void removeCosmetic(Player player) {
        activeCosmetics.remove(player);

        ItemStack chest = player.getInventory().getChestplate();
        if (isCosmeticElytra(chest)) {
            player.getInventory().setChestplate(null);
        }

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (isCosmeticGrapplingHook(item)) {
                player.getInventory().setItem(i, null);
            }
            if (isCosmeticDoubleJump(item)) {
                player.getInventory().setItem(i, null);
            }
        }
    }

    public static CosmeticType getCosmetic(Player player) {
        return activeCosmetics.getOrDefault(player, null);
    }

    public static ItemStack getCosmeticElytra() {
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        ItemMeta meta = elytra.getItemMeta();
        if (meta != null) {
            String displayName = config.getString("cosmetics.item-names.elytra", "&bCosmetic Elytra");
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            meta.setUnbreakable(true);
            elytra.setItemMeta(meta);
        }
        return elytra;
    }

    public static boolean isCosmeticElytra(ItemStack item) {
        if (item == null || item.getType() != Material.ELYTRA || !item.hasItemMeta()) {
            return false;
        }
        String expectedName = ChatColor.translateAlternateColorCodes('&',
                config.getString("cosmetics.item-names.elytra", "&bCosmetic Elytra"));
        return expectedName.equals(item.getItemMeta().getDisplayName());
    }

    public static ItemStack getCosmeticGrapplingHook() {
        ItemStack rod = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = rod.getItemMeta();
        if (meta != null) {
            String displayName = config.getString("cosmetics.item-names.grappling-hook", "&bCosmetic Grappling Hook");
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            meta.setUnbreakable(true);
            rod.setItemMeta(meta);
        }
        return rod;
    }

    public static boolean isCosmeticGrapplingHook(ItemStack item) {
        if (item == null || item.getType() != Material.FISHING_ROD || !item.hasItemMeta()) {
            return false;
        }
        String expectedName = ChatColor.translateAlternateColorCodes('&',
                config.getString("cosmetics.item-names.grappling-hook", "&bCosmetic Grappling Hook"));
        return expectedName.equals(item.getItemMeta().getDisplayName());
    }

    public static ItemStack getCosmeticDoubleJump() {
        ItemStack feather = new ItemStack(Material.FEATHER);
        ItemMeta meta = feather.getItemMeta();
        if (meta != null) {
            String displayName = config.getString("cosmetics.item-names.double-jump", "&bCosmetic Double Jump");
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            meta.setUnbreakable(true);
            feather.setItemMeta(meta);
        }
        return feather;
    }

    public static boolean isCosmeticDoubleJump(ItemStack item) {
        if (item == null || item.getType() != Material.FEATHER || !item.hasItemMeta()) {
            return false;
        }
        String expectedName = ChatColor.translateAlternateColorCodes('&',
                config.getString("cosmetics.item-names.double-jump", "&bCosmetic Double Jump"));
        return expectedName.equals(item.getItemMeta().getDisplayName());
    }

    public static boolean isWorldAllowed(String worldName) {
        return config.getStringList("cosmetics.allowed-worlds").isEmpty() ||
                config.getStringList("cosmetics.allowed-worlds").contains(worldName);
    }
}