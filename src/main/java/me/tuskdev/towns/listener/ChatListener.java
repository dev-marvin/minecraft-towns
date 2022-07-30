package me.tuskdev.towns.listener;

import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.hook.GriefPreventionHook;
import me.tuskdev.towns.model.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;
import java.util.Optional;

public class ChatListener implements Listener {

    private final String message;
    private final TownCache townCache;

    public ChatListener(TownCache townCache, FileConfiguration fileConfiguration) {
        this.message = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("chat-message"));

        this.townCache = townCache;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Town town = townCache.get(event.getPlayer().getUniqueId());
        if (!town.isEnableChat()) return;

        event.setCancelled(true);

        String message = this.message.replace("{player}", event.getPlayer().getName()).replace("{message}", event.getMessage()).replace("{town-name}", town.getName());
        town.getMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> player.sendMessage(message));
        Optional.ofNullable(Bukkit.getPlayer(GriefPreventionHook.getClaimById(town.getClaimId()).getOwnerID())).ifPresent(player -> player.sendMessage(message));
    }

}
