package me.tuskdev.towns.listener;

import me.ryanhamshire.GriefPrevention.Claim;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.hook.GriefPreventionHook;
import me.tuskdev.towns.model.Town;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final String join, left;
    private final TownCache townCache;

    public PlayerMoveListener(TownCache townCache, FileConfiguration fileConfiguration) {
        this.join = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-join"));
        this.left = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-left"));

        this.townCache = townCache;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Claim fromClaim = GriefPreventionHook.getClaimAt(event.getFrom());
        Claim toClaim = GriefPreventionHook.getClaimAt(event.getTo());
        if (fromClaim != null && toClaim != null && fromClaim.getID().equals(toClaim.getID())) return;

        Player player = event.getPlayer();

        if (fromClaim != null) {
            Town fromTown = townCache.get(fromClaim.getID());
            if (fromTown != null) player.sendMessage(left.replace("{town-name}", fromTown.getName()));
        }

        if (toClaim != null) {
            Town toTown = townCache.get(toClaim.getID());
            if (toTown != null) player.sendMessage(join.replace("{town-name}", toTown.getName()));
        }
    }

}
