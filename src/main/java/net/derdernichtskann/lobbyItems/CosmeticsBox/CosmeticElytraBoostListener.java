package net.derdernichtskann.lobbyItems.CosmeticsBox;

import net.derdernichtskann.lobbyItems.LobbyItems;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CosmeticElytraBoostListener implements Listener {
    private final Map<UUID, Integer> boostTaskIDs = new HashMap<>();
    private final FileConfiguration config;

    public CosmeticElytraBoostListener(FileConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (!CosmeticManager.isWorldAllowed(player.getWorld().getName())) {
            return;
        }

        if (!event.isSneaking()) {
            cancelBoostTask(player);
            return;
        }

        if (CosmeticManager.getCosmetic(player) != CosmeticType.ELYTRA) {
            return;
        }

        if (!player.isGliding()) return;
        if (boostTaskIDs.containsKey(player.getUniqueId())) return;

        int boostDurationTicks = config.getInt("items.elytra.boost-duration-ticks", 100);
        double boostVelocity = config.getDouble("items.elytra.boost-velocity", 1.25);

        int taskId = Bukkit.getScheduler().runTaskTimer(LobbyItems.instance, new Runnable() {
            int counter = 0;

            @Override
            public void run() {
                if (!player.isGliding() || !player.isSneaking()) {
                    cancelBoostTask(player);
                    return;
                }

                // Create particle effects
                Vector right = player.getLocation().getDirection().clone()
                        .rotateAroundY(Math.toRadians(90)).multiply(0.3);
                Vector left = player.getLocation().getDirection().clone()
                        .rotateAroundY(Math.toRadians(-90)).multiply(0.3);

                right.add(player.getLocation().getDirection().clone().multiply(-0.2));
                left.add(player.getLocation().getDirection().clone().multiply(-0.2));

                player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK,
                        player.getLocation().clone().add(right), 1, 0, 0, 0, 0);
                player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK,
                        player.getLocation().clone().add(left), 1, 0, 0, 0, 0);

                // Apply boost velocity
                player.setVelocity(player.getLocation().getDirection().clone().multiply(boostVelocity));

                counter++;
                if (counter >= boostDurationTicks) {
                    cancelBoostTask(player);
                }
            }
        }, 0L, 1L).getTaskId();

        boostTaskIDs.put(player.getUniqueId(), taskId);
    }

    private void cancelBoostTask(Player player) {
        UUID uuid = player.getUniqueId();
        if (boostTaskIDs.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(boostTaskIDs.get(uuid));
            boostTaskIDs.remove(uuid);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getSlotType() == InventoryType.SlotType.ARMOR &&
                event.getSlot() == 38 &&
                CosmeticManager.isCosmeticElytra(event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (CosmeticManager.isCosmeticElytra(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }
}