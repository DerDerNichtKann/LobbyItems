package net.derdernichtskann.lobbyItems.CosmeticsBox;

import net.derdernichtskann.lobbyItems.LobbyItems;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CosmeticGrapplingHookListener implements Listener {
    private final FileConfiguration config;

    public CosmeticGrapplingHookListener(FileConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerUseGrapplingHook(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!CosmeticManager.isWorldAllowed(player.getWorld().getName())) {
            return;
        }

        if (event.getItem() == null || event.getItem().getType() != Material.FISHING_ROD) {
            return;
        }

        if (CosmeticManager.getCosmetic(player) != CosmeticType.GRAPPLING) {
            return;
        }

        if (!CosmeticManager.isCosmeticGrapplingHook(event.getItem())) {
            return;
        }

        int cooldownTicks = config.getInt("items.grappling-hook.cooldown-ticks", 60);
        if (player.hasCooldown(Material.FISHING_ROD)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (event.getState().equals(PlayerFishEvent.State.BITE) ||
                event.getState().equals(PlayerFishEvent.State.FISHING)) {
            return;
        }

        if (!CosmeticManager.isWorldAllowed(player.getWorld().getName())) {
            return;
        }

        if (!event.getHook().isOnGround()) {
            return;
        }

        if (CosmeticManager.getCosmetic(player) != CosmeticType.GRAPPLING) {
            return;
        }

        int cooldownTicks = config.getInt("items.grappling-hook.cooldown-ticks", 60);
        double velocityMultiplier = config.getDouble("items.grappling-hook.velocity-multiplier", 3.0);
        double upwardVelocity = config.getDouble("items.grappling-hook.upward-velocity", 0.9);

        player.setCooldown(Material.FISHING_ROD, cooldownTicks);
        player.setVelocity(player.getLocation()
                .subtract(event.getHook().getLocation())
                .toVector()
                .normalize()
                .multiply(-velocityMultiplier)
                .setY(upwardVelocity));

        event.setCancelled(true);
        event.getHook().remove();
    }

    @EventHandler
    public void onHookHitPlayer(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof org.bukkit.entity.FishHook)) return;
        if (!(event.getHitEntity() instanceof Player)) return;

        Player hitPlayer = (Player) event.getHitEntity();

        if (CosmeticManager.isWorldAllowed(hitPlayer.getWorld().getName())) {
            event.setCancelled(true);
        }
    }
}