package net.derdernichtskann.lobbyItems.CosmeticsBox;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class CosmeticDoubleJumpListener implements Listener {
    private final FileConfiguration config;

    public CosmeticDoubleJumpListener(FileConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!CosmeticManager.isWorldAllowed(player.getWorld().getName())) {
            return;
        }

        if (event.getItem() == null || event.getItem().getType() != Material.FEATHER) {
            return;
        }

        if (CosmeticManager.getCosmetic(player) != CosmeticType.DOUBLE_JUMP) {
            return;
        }

        int cooldownTicks = config.getInt("items.double-jump.cooldown-ticks", 60);
        if (player.hasCooldown(Material.FEATHER)) {
            event.setCancelled(true);
            return;
        }

        player.setCooldown(Material.FEATHER, cooldownTicks);

        event.setCancelled(true);

        double jumpHeight = config.getDouble("items.double-jump.jump-height", 1.25);
        double horizontalBoost = config.getDouble("items.double-jump.horizontal-boost", 2.5);

        Vector jumpDirection = player.getLocation().getDirection().normalize().multiply(horizontalBoost);
        jumpDirection.setY(jumpHeight);
        player.setVelocity(jumpDirection);

        // Play sound from config
        String soundName = config.getString("items.double-jump.sound", "ENTITY_ENDER_DRAGON_FLAP");
        try {
            Sound sound = Sound.valueOf(soundName);
            player.getWorld().playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            // Fallback to default sound if config sound is invalid
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);
        }
    }
}