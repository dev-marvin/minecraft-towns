package me.tuskdev.towns.listener;

import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.controller.TownController;
import me.tuskdev.towns.enums.Rank;
import me.tuskdev.towns.hook.VaultHook;
import me.tuskdev.towns.model.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ClaimListener implements Listener {

    private final String abandon;
    private final TownCache townCache;
    private final TownController townController;

    public ClaimListener(TownCache townCache, TownController townController, FileConfiguration fileConfiguration) {
        this.abandon = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("abandon-claim"));

        this.townCache = townCache;
        this.townController = townController;
    }

    @EventHandler
    public void onClaimDelete(ClaimDeletedEvent event) {
        Town town = townCache.get(event.getClaim().getID());
        if (town == null) return;

        town.getMembersMap().forEach((uuid, rank) -> {
            if (rank == Rank.LEADER) VaultHook.depositPlayer(uuid, town.getBalance());

            townCache.set(uuid, null);
        });

        townCache.remove(town.getName());
        townController.delete(town.getName());

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(abandon.replace("{town-name}", town.getName()));
        Bukkit.broadcastMessage("");
    }
}
