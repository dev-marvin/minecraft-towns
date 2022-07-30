package me.tuskdev.towns.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.enums.Rank;
import me.tuskdev.towns.model.Town;
import me.tuskdev.towns.util.NumberUtil;
import me.tuskdev.towns.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LogsCommand {

    private final String usage, number, unknown, permission, title;
    private final TownCache townCache;

    public LogsCommand(TownCache townCache, FileConfiguration fileConfiguration) {
        this.usage = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("logs-usage"));
        this.number = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("invalid-number"));
        this.unknown = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-not-exists"));
        this.permission = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-permission"));
        this.title = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("logs-success"));

        this.townCache = townCache;
    }

    @Command(
            name = "town.logs",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context) {
        if (context.argsCount() <= 0) {
            context.sendMessage(usage);
            return;
        }

        int page = NumberUtil.tryParseInt(context.getArg(context.argsCount()-1));
        if (page <= 0) {
            context.sendMessage(number);
            return;
        }

        Player player = context.getSender();
        Town town = context.argsCount() == 1 ? townCache.get(player.getUniqueId()) : townCache.get(context.getArg(0));
        if (town == null) {
            context.sendMessage(unknown);
            return;
        }

        Rank playerRank = town.getMemberRank(player.getUniqueId());
        if (playerRank != Rank.LEADER && playerRank != Rank.OFFICE && !player.isOp()) {
            context.sendMessage(permission);
            return;
        }

        TextUtil.getPage(town.getLogs(), page, title.replace("{town-name}", town.getName()), 20).forEach(context::sendMessage);
    }

}
