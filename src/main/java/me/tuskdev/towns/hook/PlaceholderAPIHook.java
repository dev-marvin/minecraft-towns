package me.tuskdev.towns.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.model.Town;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final TownCache townCache;

    public PlaceholderAPIHook(TownCache townCache) {
        this.townCache = townCache;
    }

    @Override
    public String getIdentifier() {
        return "mcparty";
    }

    @Override
    public String getAuthor() {
        return "marvin";
    }

    @Override
    public String getVersion() {
        return "1.0-SNAPSHOT";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.equalsIgnoreCase("town")) {
            Town town = townCache.get(player.getUniqueId());
            return town != null ? String.format("§8[§6%s§8]", town.getName()) : "";
        }

        return null;
    }
}
