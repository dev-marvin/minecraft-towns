package me.tuskdev.towns.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.model.Town;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TeleportCommand {

    private final String unknown, teleported;
    private final TownCache townCache;

    public TeleportCommand(TownCache townCache, FileConfiguration fileConfiguration) {
        this.townCache = townCache;
        this.unknown = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-not-exists"));
        this.teleported = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-teleported"));
    }

    @Command(
            name = "town.teleport",
            aliases = { "tp", "home" },
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context) {
        Town town = townCache.get(context.getSender().getUniqueId());
        if (town == null) {
            context.sendMessage(unknown);
            return;
        }

        context.getSender().teleport(town.getCoordinates().build());
        context.sendMessage(teleported.replace("{town-name}", town.getName()));
    }
    
}
