package me.tuskdev.towns.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.enums.Rank;
import me.tuskdev.towns.hook.GriefPreventionHook;
import me.tuskdev.towns.model.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class InfoCommand {

    private final String usage, unknown;
    private final List<String> success;
    private final TownCache townCache;

    public InfoCommand(TownCache townCache, FileConfiguration fileConfiguration) {
        this.usage = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("info-usage"));
        this.unknown = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-not-exists"));
        this.success = fileConfiguration.getStringList("info-success").stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).toList();

        this.townCache = townCache;
    }

    @Command(
            name = "town.info",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context) {
        Town town = context.argsCount() >= 1 ? townCache.get(context.getArg(0)) : townCache.get(context.getSender().getUniqueId());
        if (town == null) {
            context.sendMessage(context.argsCount() >= 1 ? unknown : usage);
            return;
        }

        String offices = town.getMembers().stream().filter(uuid -> town.getMemberRank(uuid) == Rank.OFFICE).map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).toList().toString().replace("[", "").replace("]", "");
        String members = town.getMembers().stream().filter(uuid -> town.getMemberRank(uuid) == Rank.MEMBER).map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).toList().toString().replace("[", "").replace("]", "");
        success.forEach(message -> context.sendMessage(message.replace("{town-name}", town.getName()).replace("{town-owner}", GriefPreventionHook.getClaimById(town.getClaimId()).getOwnerName()).replace("{town-offices}", offices).replace("{town-members}", members).replace("{balance}", town.getBalance() + "")));
    }

}
